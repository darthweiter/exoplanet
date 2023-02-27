package exoplanet.groundstation;

import com.fasterxml.jackson.databind.JsonNode;

import exoplanet.robot.Direction;
import exoplanet.robot.Robot;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Bodenstation {

	private List<RoboterManagement> robots = null;
	private ArrayList<Robot> robotList = new ArrayList<>();
	private ArrayList<Planet> planetList = new ArrayList<>();
	private Thread requestListener;
	private Scanner scanner;

	private String hostnameStation = "localhost";
	private int portStation;

	private String hostnamePlanet;
	private int portPlanet;

	// TODO use this flag for using JSON-Protocol instead of normal Protocol, it
	// intercept the messages and encode/decode the msg correctly
	private boolean useJson;

	private Robot currentRobot;
	private Planet currentPlanet;
	private ObjectMapper mapper = new ObjectMapper();

	class RequestListener extends Thread {
		private ServerSocket serverSocket;
		private int port;

		public RequestListener(int port) {
			super();
			this.port = port;
		}

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(port);
//				serverSocket.setSoTimeout(2000);

				while (!Thread.interrupted()) {
					try {
						Socket robotSocket = serverSocket.accept();
						System.out.println("cuurentRobot: " +currentRobot);
						Robot robotTemp = currentRobot;
						robots.add(new RoboterManagement(Bodenstation.this, robotSocket, robotTemp));
					} catch (SocketTimeoutException e) {
					}
				}
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bodenstation(int port) {
		robots = Collections.synchronizedList(new ArrayList<RoboterManagement>());
		scanner = new Scanner(System.in);
		this.portStation = port;
		requestListener = new RequestListener(port);
		requestListener.start();

		getPlanets();
		getRobots();
	}

	public void getPlanets() {
		HttpResponse response = createRestRequest("GET", "http://localhost:12345/api/v1/planeten", null);

		try {
			Planet[] array = mapper.readValue(response.getEntity().getContent(), Planet[].class);
			planetList.clear();
			for (int i = 0; i <= array.length - 1; i++) {
				planetList.add(array[i]);
			}
		} catch (StreamReadException e) {
			e.printStackTrace();
		} catch (DatabindException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getRobots() {
		HttpResponse response = createRestRequest("GET", "http://localhost:12345/api/v1/roboter", null);
		Robot[] array;
		try {
			array = mapper.readValue(response.getEntity().getContent(), Robot[].class);
			robotList.clear();
			for (int i = 0; i <= array.length - 1; i++) {
				currentRobot = array[i];
				array[i].connectToStation("localhost", 3141);
				robotList.add(array[i]);
			}
		} catch (StreamReadException e) {
			e.printStackTrace();
		} catch (DatabindException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void isPlanetKnown(String[] split) {
		try {
			Planet planetTemp;
			Robot robotTemp;
			HttpResponse response = null;
			boolean keinNeuerPlanet = false;
			int x = 0;
			for (Planet planet : planetList) {
				x++;
				if (planet.getId() == 0) {
					planetTemp = new Planet(0, planet.getName(), Integer.parseInt(split[1]),
							Integer.parseInt(split[2]));
					response = createRestRequest("POST", "http://localhost:12345/api/v1/planeten", planetTemp);

					planetTemp = mapper.readValue(response.getEntity().getContent(), Planet.class);
					currentRobot.setPlanetId(planetTemp.getId());

					for (int i = 0; i <= planetList.size() - 1; i++) {
						if (planetList.get(i).getName().equalsIgnoreCase(planetTemp.getName())) {
							planetList.get(i).setId(planetTemp.getId());
							planetList.get(i).setHeight(planetTemp.getHeight());
							planetList.get(i).setWidth(planetTemp.getWidth());
						}
					}

					response = createRestRequest("POST", "http://localhost:12345/api/v1/roboter", currentRobot);
					robotTemp = mapper.readValue(response.getEntity().getContent(), Robot.class);
					for (int i = 0; i <= robotList.size() - 1; i++) {
						if (robotList.get(i).getName().equalsIgnoreCase(robotTemp.getName())) {
							robotList.get(i).setId(robotTemp.getId());
							robotList.get(i).setPlanetId(robotTemp.getPlanetId());
						}
					}

					if (planetList.size() == x) {
						keinNeuerPlanet = true;
					}
				}

				if (planet.getId() != 0 && keinNeuerPlanet) {
					response = createRestRequest("POST", "http://localhost:12345/api/v1/roboter", currentRobot);
					robotTemp = mapper.readValue(response.getEntity().getContent(), Robot.class);
					for (int i = 0; i <= robotList.size() - 1; i++) {
						if (robotList.get(i).getName().equalsIgnoreCase(robotTemp.getName())) {
							robotList.get(i).setId(robotTemp.getId());
							robotList.get(i).setPlanetId(robotTemp.getPlanetId());
						}
					}
				}
			}
		} catch (StreamReadException e) {
			e.printStackTrace();
		} catch (DatabindException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void saveMeasure(Robot robot, String ground, String temperature) {
		createRestRequest("POST", "http://localhost8080/api/v1/messdaten", new Messdaten(robot.getPlanetId(),
				robot.getX(), robot.getY(), ground, Double.parseDouble(temperature)));
	}

	public void ausgabe(String ausgabe) {
		System.out.println(ausgabe);
	}

	public void shutdown() {
		robots.forEach((robot) -> {
			try {
				robot.close();
			} catch (Exception e) {
			}
		});
	}

	public String eingabe() {
		return scanner.nextLine();
	}

	public RoboterManagement getRM() {
		for (RoboterManagement rm : robots) {
			System.out.println("currentrobot id "+currentRobot);
			System.out.println(robots.size());
			if (currentRobot != null && rm.getRobot().getName().equals(currentRobot.getName())) {
				return rm;
			}
		}
		return null;
	}

	public void showCommands() {
		System.out.println("Folgende Befehle gibt es:");
		System.out.println("findPlanet -> Findet einen neuen Planeten zum erkunden");
		System.out.println("createRobot -> erstelle einen Roboter, dieser wird automatisch ausgewählt");
		System.out.println("showRobots -> zeige alle Roboter an");
		System.out.println("showPlanets -> zeige alle Planeten an");
		System.out.println("selectRobot|robotername -> wähle diesen Roboter aus um eine AKtion auszuführen");
		System.out.println("orbit:robotername -> Trete in die Umlaufbahn des Planeten mit dem Roboter ein");
		System.out.println(
				"land:POSITION|x|y|direction -> Lande den Roboter auf den Planeten. X und Y sind Koordinaten, für direction NORTH,EAST,SOUTH oder WEST eingeben");
		System.out.println("move -> Bewege Roboter in die Richtung, in die er schaut");
		System.out.println("scan -> Scanne das Feld vor dem Roboter");
		System.out.println("mvscan -> Bewege Roboter in die Richtung, in die er schaut und scanne das Feld");
		System.out.println("rotate|Richtung -> Roboter dreht sich, für 'Richtung' 'left' oder 'right' eingeben");
		System.out.println("exit -> Roboter wird aufgegeben");
		System.out.println("shutdown -> Bodenstation wird geschlossen");

	}

	public boolean checkRobotName(String eingabeRobotName) {
		for (Robot robot : robotList) {
			if (robot.getName().equals(eingabeRobotName)) {
				return false;
			}
		}
		return true;

	}

	public HttpResponse createRestRequest(String requestType, String uri, Object object) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = null;
		HttpPut put = null;
		HttpGet get = null;
		try {

			if (requestType.equals("POST")) {
				post = new HttpPost(uri);
				StringEntity params = new StringEntity(mapper.writeValueAsString(object), ContentType.APPLICATION_JSON);
				post.setEntity(params);

				return client.execute(post);

			} else if (requestType.equals("PUT")) {
				put = new HttpPut(uri);
				StringEntity params = new StringEntity(mapper.writeValueAsString(object), ContentType.APPLICATION_JSON);
				put.setEntity(params);

				return client.execute(put);

			} else if (requestType.equals("GET")) {
				get = new HttpGet(uri);

				return client.execute(get);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public boolean checkPlanetName(String planetName) {
		for (Planet planet : planetList) {
			if (planet.getName().equalsIgnoreCase(planetName)) {
			}
			currentPlanet = planet;
			return true;
		}
		return false;
	}

	public boolean noKollision() {
		long planetId = currentRobot.getPlanetId();
		for (Robot robot : robotList) {
			if (robot.getPlanetId() == planetId) {
				if (robot.getX() == getRM().getNewX() && robot.getY() == getRM().getNewY()) {
					return false;
				}
			}
		}

		return true;
	}

	public void run() {
		boolean running = true;
//		currentRobot = null;

		while (running) {
			String eingabe = scanner.nextLine();

			if (eingabe.equalsIgnoreCase("createRobot")) {
				System.out.println("Nun geben sie den Namen des Roboters ein:");
				String robotName = scanner.nextLine();
				if (checkRobotName(robotName)) {
					currentRobot = new Robot(robotName, 0, hostnameStation, portStation, useJson);
					robotList.add(currentRobot);
					currentRobot.connectToStation("localhost", 3141);
					System.out.println("Nenne den Planetennamen, auf den der Roboter geschickt werden soll");
					String planetName = scanner.nextLine();
					if (checkPlanetName(planetName)) {
						currentRobot.setPlanetId(currentPlanet.getId());
						System.out.println("Roboter " + robotName + " wurder erstellt");
					}
				} else {
					System.out.println("Error: Robotername bereits vergeben");
					System.out.println("Roboter konnte nicht erstellt werden");
				}

			} else if (eingabe.contains("land:POSITION|")) {
				String[] splitEingabe = eingabe.split("\\|");
				System.out.println("Roboter bei landung: " + currentRobot);
				currentRobot.setX(Integer.parseInt(splitEingabe[1]));
				currentRobot.setY(Integer.parseInt(splitEingabe[2]));
				currentRobot.setDirection(Direction.valueOf(splitEingabe[3]));
				getRM().sendToRobot(eingabe);

			} else if (eingabe.contains("orbit:")) {
				System.out.println("orbitCommand");
				if (getRM() != null) {
					System.out.println("sende Command");
					getRM().sendToRobot(eingabe);
				} else {
					System.out.println("RM ist null");

				}

			} else if (eingabe.equalsIgnoreCase("move")) {
				if (noKollision()) {
					getRM().sendToRobot(eingabe);
				}

			} else if (eingabe.equalsIgnoreCase("scan")) {
				getRM().sendToRobot(eingabe);

			} else if (eingabe.equalsIgnoreCase("mvscan")) {
				if (noKollision()) {
					getRM().sendToRobot(eingabe);
				}

			} else if (eingabe.contains("rotate|")) {
				getRM().sendToRobot(eingabe);

			} else if (eingabe.equalsIgnoreCase("exit")) {
				getRM().sendToRobot(eingabe);

			} else if (eingabe.equalsIgnoreCase("showRobots")) {
				for (Robot robot : robotList) {
					System.out.println(robot.getName());
				}

			} else if (eingabe.contains("selectRobot|")) {
				boolean robotFound = false;

				for (Robot robot : robotList) {
					if (robot.getName().equalsIgnoreCase(eingabe.split("\\|")[1])) {
						currentRobot = robot;
						robotFound = true;
					}
					if (!robotFound) {
						System.out.println("Roboter gibt es nicht");
					}
				}

			} else if (eingabe.equalsIgnoreCase("showPlanets")) {
				for (Planet planet : planetList) {
					System.out.println(planet.getName());
				}
			} else if (eingabe.equalsIgnoreCase("shutdown")) {
				running = false;
				shutdown();
			} else if (eingabe.equalsIgnoreCase("showCommands")) {
				showCommands();
			} else if (eingabe.equalsIgnoreCase("findPlanet")) {
				System.out.println("Gebe den Namen des Planeten für die Expedition ein");
				String eingabePlanet = scanner.nextLine();
				if (checkPlanetName(eingabePlanet)) {
					Planet planet = new Planet(0, eingabePlanet, 0, 0);
					planetList.add(planet);
					currentPlanet = planet;
					System.out.println("Planet: " + eingabePlanet + " wurde gefunden");

				}
			} else {
				System.out.println("Command gibt es nicht");
			}
		}
	}

}

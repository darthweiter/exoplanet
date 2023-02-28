package exoplanet.groundstation;

import com.fasterxml.jackson.databind.JsonNode;
import exoplanet.commands.model.DIRECTION;
import exoplanet.commands.receive.ReceiveCommandInit;
import exoplanet.robot.Robot;
import exoplanet.robot.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
						System.out.println("cuurentRobot: " + currentRobot);
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

	public void isPlanetKnown(ReceiveCommandInit command) {
		try {
			Planet newPlanet = null;
			Planet planetTemp;
			Robot robotTemp;
			boolean isNewPlanet = false;
			HttpResponse response = null;
			for (Planet planet : planetList) {
				if (planet.getId() == 0) {
					newPlanet = planet;
					isNewPlanet = true;
				}
			}

			if (isNewPlanet) {
				planetTemp = new Planet(0, newPlanet.getName(), command.getSize().width(), command.getSize().height());
				response = createRestRequest("POST", "http://localhost:12345/api/v1/planeten", planetTemp);
				var content = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String contentValue = reader.readLine();
				JsonNode node = mapper.readTree(contentValue);
				System.out.println(node);

				planetTemp = mapper.readValue(contentValue, Planet.class);
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
			}

			if (!isNewPlanet) {
				response = createRestRequest("POST", "http://localhost:12345/api/v1/roboter", currentRobot);
				robotTemp = mapper.readValue(response.getEntity().getContent(), Robot.class);
				for (int i = 0; i <= robotList.size() - 1; i++) {
					if (robotList.get(i).getName().equalsIgnoreCase(robotTemp.getName())) {
						robotList.get(i).setId(robotTemp.getId());
						robotList.get(i).setPlanetId(robotTemp.getPlanetId());
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
		System.exit(0);
	}

	public String eingabe() {
		return scanner.nextLine();
	}

	public RoboterManagement getRM() {
		for (RoboterManagement rm : robots) {
			System.out.println("currentrobot id " + currentRobot);
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
		System.out.println("rotate:Richtung -> Roboter dreht sich, für 'Richtung' 'left' oder 'right' eingeben");
		System.out.println("exit -> Roboter wird aufgegeben");
		System.out.println("charge:duration -> Roboter versucht Energie über Solar zu erzeugen, Duration gibt einen Sekundenwert an");
		// Muss für JSON-Kommunikation vor der Erstellung des Roboters ausgeführt werden, wenn es noch auf Textprotokoll ist
		System.out.println("json -> wechselt zwischen JSON Protokoll und TextProtokoll bei neuen Robotern. Ist Standardmäßig auf Textprotokoll");
		System.out.println("currentRobot -> zeige den momentan ausgewählten Roboter an");
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
				// post.addHeader("content-type", "application/");
				post.setEntity(params);

				return client.execute(post);

			} else if (requestType.equals("PUT")) {
				put = new HttpPut(uri);
				StringEntity params = new StringEntity(mapper.writeValueAsString(object), ContentType.APPLICATION_JSON);
				// put.addHeader("content-type", "application/x-www-form-urlencoded");
				put.setEntity(params);

				var response = client.execute(put);
				JsonNode node = mapper.readTree(response.getEntity().getContent());
				System.out.println(node);

				return response;

			} else if (requestType.equals("GET")) {
				get = new HttpGet(uri);

				return client.execute(get);

//			Planet[] array = mapper.readValue(response.getEntity().getContent(), Planet[].class);
//			planetList.add(array[0]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public boolean checkPlanetName(String planetName) {
		if (planetList.size() == 0) {
			return true;
		}
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
	
	public Planet getPlanet(long id) {
		for (Planet planet : planetList) {
			if(planet.getId() == id) {
				return planet;
			}
		}
		return null;
	}
	
	public RoboterManagement getSpecificRM(long id) {
		for (RoboterManagement rm : robots) {
			if (rm.getRobot().getId() == id) {
				return rm;
			}
		}
		return null;
	}
	
	
	public void autoMove() {
		Robot robot = currentRobot;
		boolean running = true;
		Planet planet = getPlanet(robot.getPlanetId());
		while(running){
			// Roboter rotiert nach RECHTS, wenn er am Rand ist und in die Richtung des Randes schaut
			if(robot.getX() == 0 && robot.getDirection() == DIRECTION.NORTH || 
			   robot.getX() == planet.getWidth() && robot.getDirection() == DIRECTION.EAST ||
			   robot.getY() == 0 && robot.getDirection() == DIRECTION.WEST ||
			   robot.getY() == planet.getHeight() && robot.getDirection() == DIRECTION.SOUTH) 
			{
				getSpecificRM(robot.getId()).sendToRobot("rotate:RIGHT");
			}
			
			if (noKollision()) {
				getSpecificRM(robot.getId()).sendToRobot("mvscaned");
			}
		}
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
					System.out.println("Soll der Roboter im JSON-Format kommunizieren J/N");
					String json = scanner.nextLine();
					if(json.equalsIgnoreCase("J"));
				} else {
					System.out.println("Error: Robotername bereits vergeben");
					System.out.println("Roboter konnte nicht erstellt werden");
				}

			} else if (eingabe.contains("land:POSITION|")) {
				String[] splitEingabe = eingabe.split("\\|");
				System.out.println("Roboter bei landung: " + currentRobot);
				currentRobot.setX(Integer.parseInt(splitEingabe[1]));
				currentRobot.setY(Integer.parseInt(splitEingabe[2]));
				currentRobot.setDirection(DIRECTION.valueOf(splitEingabe[3]));
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

			} else if (eingabe.contains("rotate:")) {
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
						if(robot.getStatus() != Status.CRASHED) {
							currentRobot = robot;
							robotFound = true;
							System.out.println("Roboter: " +robot.getName()+ " wurde ausgewählt");
						}
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
			} else if (eingabe.equalsIgnoreCase("getpos")) {
				
			}else if (eingabe.contains("charge:")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.equalsIgnoreCase("json")) {
				if(useJson) {
					useJson = false;
					System.out.println("JSON aus, Text an");
				}else {
					useJson = true;
					System.out.println("JSON an, Text aus");
				}
			}else if(eingabe.equalsIgnoreCase("currentRobot")) {
				if(currentRobot != null) {
					System.out.println("Aktuell ausgewählter Roboter: " + currentRobot.getName());
				}else {
					System.out.println("Aktuell kein Roboter ausgewählt");
				}
			}
			else {
				System.out.println("Command gibt es nicht");
			}
		}
	}

}

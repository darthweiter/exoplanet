package exoplanet.groundstation;

import com.fasterxml.jackson.databind.JsonNode;
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

	private  List<RoboterManagement> robots = null;
	private  ArrayList<Robot> robotList = new ArrayList<>();
	private ArrayList<Planet> planetList = new ArrayList<>();
	private Thread requestListener;
	private Scanner scanner;
	
	private  String hostnameStation = "localhost";
	private  int portStation;
	
	private  String hostnamePlanet;
	private  int portPlanet;

	//TODO use this flag for using JSON-Protocol instead of normal Protocol, it intercept the messages and encode/decode the msg correctly
	private boolean useJson;
	
	private  Robot currentRobot;
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
				serverSocket.setSoTimeout(5000);
				
				while (!Thread.interrupted()) {
					try {
						Socket robotSocket = serverSocket.accept();
						robots.add(new RoboterManagement(Bodenstation.this, robotSocket, currentRobot));
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
			for(int i=0;i<=array.length-1; i++) {
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
			for(int i=0;i<=array.length-1; i++) {
				currentRobot = array[i];
				array[i].connectToStation("localhost", 3141);
				robotList.add(array[i]);
			}
		} catch (StreamReadException e) {
			e.printStackTrace();
		} catch (DatabindException e) {
			e.printStackTrace();
		}
		catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void isPlanetKnown(String[] split) {
		int temp = Integer.parseInt(split[3]);
		for (Planet planet : planetList) {
			if(planet.getId() == temp) {
				
			}else {
				HttpResponse response = createRestRequest("POST", "http://localhost:12345/api/v1/planeten", new Planet(0, "NewPlanet", Integer.parseInt(split[1]), Integer.parseInt(split[2])));
				try {
					Planet newPlanet = mapper.readValue(response.getEntity().getContent(), Planet.class);
					planetList.add(newPlanet);
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
			
		}
	}
	
	public void saveMeasure(Robot robot, String ground, String temperature) {
		createRestRequest("POST", "http://localhost8080/api/v1/messdaten", new Messdaten(robot.getPlanetId(), robot.getX(), robot.getY(), ground, Double.parseDouble(temperature)));
	}
	
	
	public void send() {
		
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
		for(RoboterManagement rm : robots) {
			if(currentRobot != null && rm.getRobot().getName().equals(currentRobot.getName())) {
				return rm;
			}
		}
		return null;
	}
	
	public void showCommands() {
		System.out.println("Folgende Befehle gibt es:");
		System.out.println("createRobot -> erstelle einen Roboter, dieser wird automatisch ausgewählt");
		System.out.println("showRobots -> zeige alle Roboter an");
		System.out.println("showPlanets -> zeige alle Planeten an");
		System.out.println("selectRobot|robotername -> wähle diesen Roboter aus um eine AKtion auszuführen");
		System.out.println("orbit:robotername -> Trete in die Umlaufbahn des Planeten mit dem Roboter ein");
		System.out.println("land:POSITION|x|y|direction -> Lande den Roboter auf den Planeten. X und Y sind Koordinaten, für direction NORTH,EAST,SOUTH oder WEST eingeben");
		System.out.println("move -> Bewege Roboter in die Richtung, in die er schaut");
		System.out.println("scan -> Scanne das Feld vor dem Roboter");
		System.out.println("mvscan -> Bewege Roboter in die Richtung, in die er schaut und scanne das Feld");
		System.out.println("rotate|Richtung -> Roboter dreht sich, für 'Richtung' 'left' oder 'right' eingeben");
		System.out.println("exit -> Roboter wird aufgegeben");
		System.out.println("shutdown -> Bodenstation wird geschlossen");
		
	}
	
	public boolean checkRobotName(String eingabeRobotName) {
		for (Robot robot : robotList) {
			if(robot.getName().equals(eingabeRobotName)) {
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
		
		if(requestType.equals("POST")) {
			post = new HttpPost(uri);
			StringEntity params = new StringEntity(mapper.writeValueAsString(object), ContentType.APPLICATION_JSON);
			//post.addHeader("content-type", "application/");
			post.setEntity(params);
			
			return client.execute(post);
			
			
		}else if (requestType.equals("PUT")) {
			put = new HttpPut(uri);
			StringEntity params = new StringEntity(mapper.writeValueAsString(object), ContentType.APPLICATION_JSON);
			//put.addHeader("content-type", "application/x-www-form-urlencoded");
			put.setEntity(params);
			
			return client.execute(put);
			
			
			
		}else if (requestType.equals("GET")) {
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
	
	public void run() {
		boolean running = true;
		currentRobot = null;
		
		while(running) {
			String eingabe = scanner.nextLine();
			
			if(eingabe.equalsIgnoreCase("createRobot")) {
				System.out.println("Nun geben sie den Namen des Roboters ein:");
				String robotName = scanner.nextLine();
				if(checkRobotName(robotName)) {
					System.out.println("Bitte den Planetennamen eingeben, auf den der Roboter landen soll");
					String planetName = scanner.nextLine();
					for(Planet planet : planetList) {
						if(planet.getName().equalsIgnoreCase(planetName)) {
							currentRobot = new Robot(robotName, planet.getId(), hostnameStation, portStation, useJson);
							HttpResponse response = createRestRequest("POST", "http://localhost:12345/api/v1/roboter", currentRobot);
							if(response.getStatusLine().getStatusCode() == 200){
								robotList.add(currentRobot);
								System.out.println("Roboter erfolgreich erstellt");
							}else {
								try {
									JsonNode node = mapper.readTree(response.getEntity().getContent());
									System.out.println(node);
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
								currentRobot = null;
								System.out.println("ERROR: Roboter konnte nicht erstellt werden");
							}
						}else {
							System.out.println("ERROR: Planet nicht gefunden");
						}
					}
				}else {
					System.out.println("Error: Robotername bereits vergeben");
				}
			}else if(eingabe.contains("land:POSITION|")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.contains("orbit:")) {
				//Optional.ofNullable(getRM()).ifPresent(roboterManagement -> roboterManagement.sendToRobot(eingabe));
				System.out.println("orbitCommand");
				if(getRM() != null){
					System.out.println("sende Command");
				getRM().sendToRobot(eingabe);
				}

			}else if(eingabe.equalsIgnoreCase("move")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.equalsIgnoreCase("scan")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.equalsIgnoreCase("mvscan")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.equalsIgnoreCase("rotate")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.equalsIgnoreCase("exit")) {
				getRM().sendToRobot(eingabe);
				
			}else if(eingabe.equalsIgnoreCase("showRobots")) {
				for(Robot robot : robotList) {
					System.out.println(robot.getName());
				}
				
			}else if(eingabe.contains("selectRobot|")) {
				for(Robot robot : robotList) {
					if(robot.getName().equalsIgnoreCase(eingabe.split("\\|")[1])) {
						currentRobot = robot;
					} else {
						System.out.println("Roboter gibt es nicht");
					}
				}
				
			}else if(eingabe.equalsIgnoreCase("showPlanets")) {
				for(Planet planet : planetList) {
					System.out.println(planet.getName());
				}
			}else if(eingabe.equalsIgnoreCase("shutdown")) {
				running = false;
				shutdown();
			}else if(eingabe.equalsIgnoreCase("showCommands")) {
				showCommands();
			}else {
				System.out.println("Command gibt es nicht");
			}
		}
	}
	
}

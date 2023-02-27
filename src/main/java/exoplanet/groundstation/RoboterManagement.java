package exoplanet.groundstation;

import exoplanet.robot.Direction;
import exoplanet.robot.Robot;
import exoplanet.robot.Status;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;


public class RoboterManagement extends Thread {

	private Bodenstation bs;
	private Socket robotSocket;
	private BufferedReader in;
	private PrintWriter out;
	private Robot robot;
	
	public RoboterManagement(Bodenstation bs, Socket socketRoboter, Robot robot){
		System.out.println("RM Robot: "+robot);
		
		this.robot = robot;
		this.bs = bs;
		this.robotSocket = socketRoboter;
		try {
			in = new BufferedReader(new InputStreamReader(robotSocket.getInputStream()));
			out = new PrintWriter(robotSocket.getOutputStream(), true);
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		String messageFromRobot;
		try {
			while(robot.getStatus() != Status.CRASHED) {
				messageFromRobot = in.readLine();
				System.out.println(messageFromRobot);
				String[] split = messageFromRobot.split("\\|");
				if(split[0].contains("init:SIZE")) {
					System.out.println("Planetisknown");
					bs.isPlanetKnown(split);
					
				}else if(split[0].contains("landed:MEASURE")) {
					System.out.println("Roboter bei rm: "+robot);
					bs.createRestRequest("PUT", "http://localhost:12345/api/v1/roboter/"+robot.getId(), robot);
					bs.createRestRequest("POST", "http://localhost:12345/api/v1/messdaten", new Messdaten(robot.getPlanetId(), robot.getX(), robot.getY(), split[1], Double.parseDouble(split[2])));
					bs.ausgabe("Erfolgreich gelandet");
					
				}else if(split[0].contains("scaned:MEASURE")) {
					bs.createRestRequest("POST", "http://localhost:12345/api/v1/messdaten", new Messdaten(robot.getPlanetId(), getNewX(), getNewY(), split[1], Double.parseDouble(split[2])));
					bs.ausgabe("Erfolgreicher scan");
					
				}else if(split[0].contains("moved:POSITION")) {
					robot.setX(Integer.parseInt(split[1]));
					robot.setY(Integer.parseInt(split[2]));
					bs.createRestRequest("PUT", "http://localhost:12345/api/v1/roboter/" + robot.getId(), robot);
					bs.ausgabe("Erfoglreich bewegt");
					
				}else if(split[0].contains("mvscaned")) {
					
				}else if(split[0].contains("rotated")){
					robot.setDirection(Direction.valueOf(messageFromRobot.split(":")[1]));
					bs.createRestRequest("PUT", "http://localhost:12345/api/v1/roboter/" + robot.getId(), robot);
					bs.ausgabe("Erfolgreich rotiert");
					
				}else if(split[0].contains("crashed")) {
					robot.setStatus(Status.CRASHED);
					bs.createRestRequest("PUT", "http://localhost:12345/api/v1/roboter/" + robot.getId(), robot);
					bs.ausgabe("Roboter kaputt");
					
				}else if(split[0].contains("error")) {
					bs.ausgabe("Error vom Planeten: " + split[0].split(":")[1]);
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateRobotPosition(int x, int y, Direction direction) {
		
	}
	
	public int getNewX() {
	if(robot.getDirection() == Direction.EAST) {
		return robot.getX()+1;
	}else if(robot.getDirection() == Direction.WEST) {
		return robot.getX()-1;		
	}
	return robot.getX();
		
	}
	
	public int getNewY() {
		if(robot.getDirection() == Direction.NORTH) {
			return robot.getY()-1;
		}else if(robot.getDirection() == Direction.SOUTH) {
			return robot.getY()+1;
		}
		return robot.getY();
	}
		
	
	public void sendToRobot(String message) {
		out.println(message);
	}
	
	public void close() {
		try {
			robotSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Robot getRobot() {
		return robot;
	}
		
	
}

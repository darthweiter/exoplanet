package exoplanet.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StationReceiver extends Thread{

	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	private Robot robot;
	public StationReceiver(Robot robot, String hostname, int port) {
		this.robot = robot;
		try {
			socket = new Socket(hostname, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String commandStation;
		while(!robot.getStatus().equals(Status.CRASHED)) {
			try {
				commandStation = in.readLine();
				String[] split = commandStation.split("\\|");
				if(split[0].contains("land:POSITION")) {
					robot.sendToPlanet(commandStation);
				}else if(split[0].contains("scan")) {
					robot.scan();
				}else if(split[0].contains("move")) {
					robot.move();
				}else if(split[0].contains("mvscan")) {
					robot.move();
					robot.scan();
				}else if(split[0].contains("rotate")) {
					robot.rotate(split[1]);
				}else if(split[0].contains("exit")) {
					robot.exit();
				}else if(split[0].contains("orbit")){
					robot.connectToPlanet("localhost", 8150);
					robot.sendToPlanet(commandStation);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendToStation(String MessageToStation) {
		out.println(MessageToStation);
	}
	

}

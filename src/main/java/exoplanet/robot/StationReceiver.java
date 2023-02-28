package exoplanet.robot;

import exoplanet.commands.ACommandClass;
import exoplanet.commands.CommandParser;
import exoplanet.commands.error.CommandNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

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
				ACommandClass command = CommandParser.parse(commandStation);
				robot.execute(command);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CommandNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void sendToStation(String MessageToStation) {
		out.println(MessageToStation);
	}
	

}

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

public class PlanetReceiver extends Thread {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	private Robot robot;

//TODO flag korrekt einbauen, ob JSON verwendet werden soll oder nicht
	private boolean useJson = true;
	
	public PlanetReceiver(Robot robot, String hostname, int port, boolean useJson) {
		this.robot = robot;
		this.useJson = useJson;
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
		try {
			String messagePlanet;
			// Empfange Nachrichten des Exoplanten
			while (!(robot.getStatus() == Status.CRASHED)) {
				messagePlanet = in.readLine();
				System.out.println("Nachricht vom Planet: " + messagePlanet);
				ACommandClass command;
				if(useJson) {
					command = CommandParser.parseJson(messagePlanet);
				} else {
					command = CommandParser.parse(messagePlanet);
				}
				robot.execute(command);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CommandNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	public void sendToPlanet(String messageToPlanet) {
				 out.println(messageToPlanet);
	}

}

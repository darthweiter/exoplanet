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
			//Tritt in die Umlaufbahn
			//sendToPlanet("orbit:" + robot.getName());
			// Bekomme Planetendaten
			//messagePlanet = in.readLine();
			// sende Planetengröße zur Bodenstation mit PlanentenId um zu wissen von wlechem Planeten sie sind
			//sendToStation(messagePlanet + "|" + robot.getPlanetId());

			while (!(robot.getStatus() == Status.CRASHED)) {
				System.out.println("warte auf planet");
				messagePlanet = in.readLine();
				System.out.println("nachricht erhalten");
				System.out.println(messagePlanet);
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

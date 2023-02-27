package exoplanet.robot;

import com.fasterxml.jackson.core.JsonProcessingException;
import exoplanet.commands.error.CommandNotFoundException;
import exoplanet.parsing.JSONCommandParser;
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
				if(useJson) {
					messagePlanet = JSONCommandParser.parseJson(messagePlanet);
				}
				if(messagePlanet.split("\\|")[0].equals("moved:POSITION")) {
					robot.updatePosition(messagePlanet);
				} else if(messagePlanet.split("\\|")[0].equals("mvscaned:MEASURE")) {
					robot.updatePositionMVSCANED(messagePlanet);
				}
				System.out.println("send to Station");

				sendToStation(messagePlanet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CommandNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendToStation(String messageToStation) {
		robot.sendToStation(messageToStation);
	}

	public void sendToPlanet(String messageToPlanet) {

		 try {
			 if (useJson) {
				 out.println(JSONCommandParser.toJson(messageToPlanet));
			 } else {
				 out.println(messageToPlanet);
			 }
		 } catch (JsonProcessingException | CommandNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}

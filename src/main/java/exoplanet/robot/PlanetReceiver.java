package exoplanet.robot;

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
	
	public PlanetReceiver(Robot robot, String hostname, int port) {
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
		}
	}

	private void sendToStation(String messageToStation) {
		robot.sendToStation(messageToStation);
	}

	public void sendToPlanet(String messageToPlanet) {
		out.println(messageToPlanet);
	}

}

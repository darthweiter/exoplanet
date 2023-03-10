package exoplanet.groundstation;

import exoplanet.commands.ACommandClass;
import exoplanet.commands.Command;
import exoplanet.commands.CommandParser;
import exoplanet.commands.error.CommandNotFoundException;
import exoplanet.commands.model.DIRECTION;
import exoplanet.commands.receive.AReceiveCommand;
import exoplanet.commands.receive.ReceiveCommandCharged;
import exoplanet.commands.receive.ReceiveCommandError;
import exoplanet.commands.receive.ReceiveCommandInit;
import exoplanet.commands.receive.ReceiveCommandLanded;
import exoplanet.commands.receive.ReceiveCommandMoveScaned;
import exoplanet.commands.receive.ReceiveCommandMoved;
import exoplanet.commands.receive.ReceiveCommandPosition;
import exoplanet.commands.receive.ReceiveCommandRotated;
import exoplanet.commands.receive.ReceiveCommandScanned;
import exoplanet.commands.receive.ReceiveCommandStatus;
import exoplanet.robot.Robot;
import exoplanet.robot.Status;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class RoboterManagement extends Thread {

  private Bodenstation bs;
  private Socket robotSocket;
  private BufferedReader in;
  private PrintWriter out;
  private Robot robot;

  public RoboterManagement(Bodenstation bs, Socket socketRoboter, Robot robot) {
    this.bs = bs;
    this.robotSocket = socketRoboter;
    this.robot = robot;
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
    // Empfange und verarbeite Nachrichten vom Roboter
    try {
      while (true) {
        messageFromRobot = in.readLine();
        System.out.println("Nachricht vom Robot: " + messageFromRobot);

        ACommandClass command = CommandParser.parse(messageFromRobot);
        execute(command);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (CommandNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void execute(ACommandClass command) {
    if (command instanceof AReceiveCommand receiveCommand) {
      executeReceivedCommand(receiveCommand);
    }
  }

  // Verarbeite die Nachricht vom Roboter
  private void executeReceivedCommand(AReceiveCommand command) {
    switch (Command.valueOf(command.getCmd())) {
			case landed -> {
				ReceiveCommandLanded specificCommand = (ReceiveCommandLanded) command;
				bs.createRestRequest("POST", "http://localhost:12345/api/v1/messdaten",
						new Messdaten(robot.getPlanetId(), robot.getX(), robot.getY(), specificCommand.getMeasure().ground(),
								specificCommand.getMeasure().temp()));
        updateRobot();
        robot.setMeldungToStation(true);
        bs.ausgabe("Roboter ist erfolgreich gelandet");
			}
			case scaned -> {
				ReceiveCommandScanned specificCommand = (ReceiveCommandScanned) command;
				bs.createRestRequest("POST", "http://localhost:12345/api/v1/messdaten",
						new Messdaten(robot.getPlanetId(), getNewX(), getNewY(), specificCommand.getMeasure().ground(),
								specificCommand.getMeasure().temp()));
				bs.ausgabe("erfolgreich gescannt");
			}
			case rotated, crashed, pos -> {
				updateRobot();
				System.out.println("Roboter hat rotated, getpos ausgef??hrt oder ist gecrashed");
      }case moved -> {
        updateRobot();
        robot.setMeldungToStation(true);
        System.out.println("Roboter has moved.");
      }
			case mvscaned -> {
				ReceiveCommandMoveScaned specificCommand = (ReceiveCommandMoveScaned) command;
				bs.createRestRequest("POST", "http://localhost:12345/api/v1/messdaten",
						new Messdaten(robot.getPlanetId(), getNewX(), getNewY(), specificCommand.getMeasure().ground(),
								specificCommand.getMeasure().temp()));
				updateRobot();
        robot.setMeldungToStation(true);
				bs.ausgabe("Der Roboter hat sich bewegt und gescannt");
			} case error -> {
				ReceiveCommandError specificCommand = (ReceiveCommandError) command;
				bs.ausgabe("Error vom Planeten: " + specificCommand.getErrorMsg());
			}

			case init -> {
        ReceiveCommandInit specificCommand = (ReceiveCommandInit) command;
        bs.isPlanetKnown(specificCommand);
        bs.ausgabe("Roboter ist im Orbit");
      }

			case charged -> {
        //TODO advancedLevel;
		ReceiveCommandCharged specificCommand = (ReceiveCommandCharged) command;
		updateRobot();
		bs.ausgabe("Nachricht vom Aufladen: " +specificCommand.getStatus().message());
      }
      case status -> {
        //TODO advancedLevel;
//		ReceiveCommandStatus specificCommand = (ReceiveCommandStatus) command;
//		if(specificCommand.getAllStatusMessages().contains(Status.STUCK_IN_MUD.name())) {
//			status = Status.STUCK_IN_MUD;
//		}
//		sendToStation(command);
      }
    }
  }

	private void updateRobot() {
		bs.createRestRequest("PUT", "http://localhost:12345/api/v1/roboter/" + robot.getId(),
				robot);
	}

// Neue X Koordinate vom Roboter, wenn er sich bewegen w??rde
  public int getNewX() {
    if (robot.getDirection() == DIRECTION.EAST) {
      return robot.getX() + 1;
    } else if (robot.getDirection() == DIRECTION.WEST) {
      return robot.getX() - 1;
    }
    return robot.getX();

  }
//Neue Y Koordinate vom Roboter, wenn er sich bewegen w??rde
  public int getNewY() {
    if (robot.getDirection() == DIRECTION.NORTH) {
      return robot.getY() - 1;
    } else if (robot.getDirection() == DIRECTION.SOUTH) {
      return robot.getY() + 1;
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

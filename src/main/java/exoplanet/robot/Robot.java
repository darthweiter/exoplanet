package exoplanet.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.Command;
import exoplanet.commands.model.DIRECTION;
import exoplanet.commands.model.Position;
import exoplanet.commands.receive.AReceiveCommand;
import exoplanet.commands.receive.ReceiveCommandMoveScaned;
import exoplanet.commands.receive.ReceiveCommandMoved;
import exoplanet.commands.receive.ReceiveCommandPosition;
import exoplanet.commands.receive.ReceiveCommandRotated;
import exoplanet.commands.send.ASendCommand;
import exoplanet.commands.send.SendCommandLand;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Robot {

  @JsonProperty("id")
  private long id;
  @JsonProperty("pid")
  private long planetId;
  private int x;
  private int y;
  private String name;
  @JsonProperty("temperature")
  private double temperatur;
  private DIRECTION direction;
  private Status status;
  @JsonProperty("energy")
  private double energie;

  private boolean heater;
  private boolean cooler;


  private PlanetReceiver planetReceiver;
  private StationReceiver stationReceiver;

  private boolean useJson;

  public Robot() {

  }

  public Robot(String name, long planetId, String hostnameStation, int portStation,
      boolean useJson) {
    this.name = name;
    status = Status.WORKING;
    temperatur = 0;
    energie = 100;
    this.planetId = planetId;
    this.useJson = useJson;
  }

  public void connectToStation(String hostnameStation, int portStation) {
    stationReceiver = new StationReceiver(this, hostnameStation, portStation);
    stationReceiver.start();
  }

  public void connectToPlanet(String hostnamePlanet, int portPlanet) {
    planetReceiver = new PlanetReceiver(this, hostnamePlanet, portPlanet, useJson);
    planetReceiver.start();

  }

  public void execute(ACommandClass command) {
    if (command instanceof ASendCommand sendCommand) {
      executeSendCommand(sendCommand);
    } else if (command instanceof AReceiveCommand receiveCommand) {
      executeReceivedCommand(receiveCommand);
    }
  }

  private void executeSendCommand(ASendCommand command) {
    switch (Command.valueOf(command.getCmd())) {
      case orbit -> {
        connectToPlanet("localhost", 8150);
        sendToPlanet(command);
      }
      case land -> {
        SendCommandLand specificCommand = (SendCommandLand) command;
        updatePosition(specificCommand.getPosition());
        sendToPlanet(command);
      }
      case scan, move, mvscan, rotate, getpos -> {
        sendToPlanet(command);
      }
      case exit -> {
        sendToPlanet(command);
        status = Status.CRASHED;
        planetReceiver.interrupt();
      }
      case charge -> {
        //TODO advancedLevel;
//					SendCommandCharge specificCommand =(SendCommandCharge) command;
//					sendToPlanet(command);
      }
    }
  }

  private void executeReceivedCommand(AReceiveCommand command) {
    switch (Command.valueOf(command.getCmd())) {
      case init, landed, scaned, error -> {
        sendToStation(command);
      }
      case moved -> {
        ReceiveCommandMoved specificCommand = (ReceiveCommandMoved) command;
        updatePosition(specificCommand.getPosition());
        sendToStation(command);
      }
      case mvscaned -> {
        ReceiveCommandMoveScaned specificCommand = (ReceiveCommandMoveScaned) command;
        updatePosition(specificCommand.getPosition());
        sendToStation(command.toString());
      }
      case rotated -> {
        ReceiveCommandRotated specificCommand = (ReceiveCommandRotated) command;
        direction = specificCommand.getDirection();
        updatePosition(new Position(x, y, direction));
        sendToStation(command);
      }
      case crashed -> {
        status = Status.CRASHED;
        sendToStation(command);
      }
      case pos -> {
        ReceiveCommandPosition specificCommand = (ReceiveCommandPosition) command;
        updatePosition(specificCommand.getPosition());
        sendToStation(command);
      }
      case charged -> {
        //TODO advancedLevel;
//					sendToStation(command);
      }
      case status -> {
        //TODO advancedLevel;
//					ReceiveCommandStatus specificCommand = (ReceiveCommandStatus)command;
//					if(specificCommand.getAllStatusMessages().contains(Status.STUCK_IN_MUD.name())) {
//						status = Status.STUCK_IN_MUD;
//					}
//					sendToStation(command);
      }
    }
  }

  private void updatePosition(Position position) {
    x = position.x();
    y = position.y();
    direction = position.direction();
  }

  public void sendToStation(ACommandClass command) {
    stationReceiver.sendToStation(command.toString());
  }

  public void sendToStation(String message) {
    stationReceiver.sendToStation(message);
  }

  public void sendToPlanet(ACommandClass command) {
    if (useJson) {
      try {
        planetReceiver.sendToPlanet(command.toJson());
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      planetReceiver.sendToPlanet(command.toString());
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public double getTemperatur() {
    return temperatur;
  }

  public void setTemperatur(double temperatur) {
    this.temperatur = temperatur;
  }

  public DIRECTION getDirection() {
    return direction;
  }

  public void setDirection(DIRECTION direction) {
    this.direction = direction;
  }

	public long getPlanetId() {
		return planetId;
	}
	
	public void setPlanetId(long planetId) {
		this.planetId = planetId;
	}

	public long getId(){
		return id;
	}

	public void setId(long id){
		this.id = id;
	}
	
	public double getEnergie() {
		return energie;
	}
	
	public void setEnergie(double energie) {
		this.energie = energie;
	}
	

}

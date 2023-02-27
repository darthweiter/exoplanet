package exoplanet.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Robot {

	@JsonProperty("id")
	private int id;
	@JsonProperty("pid")
	private long planetId;
	@JsonProperty("x")
	private int x;
	@JsonProperty("y")
	private int y;
	@JsonProperty("name")
	private String name;
	@JsonProperty("temperature")
	private double temperatur;
	@JsonProperty("direction")
	private Direction direction;
	@JsonProperty("status")
	private Status status;
	private double energie;


	private PlanetReceiver planetReceiver;
	private StationReceiver stationReceiver;

	public Robot() {
		
	}
	
	public Robot(String name, int planetId, String hostnameStation, int portStation) {
		this.name = name;
		status = Status.WORKING;
		temperatur = 20;
		energie = 100;
		this.planetId = planetId;

		connectToStation(hostnameStation, portStation);
		
	}

	public void connectToStation(String hostnameStation, int portStation){
		stationReceiver = new StationReceiver(this, hostnameStation, portStation);
		stationReceiver.start();
	}
	
	public void connectToPlanet(String hostnamePlanet, int portPlanet) {
		planetReceiver = new PlanetReceiver(this, hostnamePlanet, portPlanet);
		planetReceiver.start();
		
	}

	public void move() {
		planetReceiver.sendToPlanet("move");
	}

	public void scan() {
		planetReceiver.sendToPlanet("scan");
	}
	
	public void mvscan() {
		planetReceiver.sendToPlanet("mvscan");
	}

	public void rotate(String rotation) {
		if(rotation.equalsIgnoreCase("left") && direction == Direction.NORTH) {
			setDirection(Direction.WEST);
		} else if (rotation.equalsIgnoreCase("left")  && direction == Direction.EAST) {
			setDirection(Direction.NORTH);
		} else if (rotation.equalsIgnoreCase("left")  && direction == Direction.SOUTH) {
			setDirection(Direction.SOUTH);
		}else if (rotation.equalsIgnoreCase("left")  && direction == Direction.WEST) {
			setDirection(Direction.EAST);
		}else if (rotation.equalsIgnoreCase("right")  && direction == Direction.NORTH) {
			setDirection(Direction.EAST);
		}else if (rotation.equalsIgnoreCase("right") && direction == Direction.EAST) {
			setDirection(Direction.SOUTH);
		}else if (rotation.equalsIgnoreCase("right") && direction == Direction.SOUTH) {
			setDirection(Direction.WEST);
		}else if (rotation.equalsIgnoreCase("right") && direction == Direction.WEST) {
			setDirection(Direction.NORTH);
		}
		planetReceiver.sendToPlanet("rotate:" + rotation);
	}
	
	public void sendToStation(String message) {
		stationReceiver.sendToStation(message);
	}
	
	public void sendToPlanet(String message) {
		planetReceiver.sendToPlanet(message);
	}

	public void land(int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		planetReceiver.sendToPlanet("land:POSITION|" + x + "|" + y + "|" + direction);
	}
	
	public void updatePosition(String messagePlanet) {
		String[] split = messagePlanet.split("\\|");
		setX(Integer.parseInt(split[1]));
		setY(Integer.parseInt(split[2]));
	}
	
	public void updatePositionMVSCANED(String messagePlanet) {
		String[] split = messagePlanet.split("\\|");
		setX(Integer.parseInt(split[3]));
		setY(Integer.parseInt(split[4]));
	}

	public void exit() {
		planetReceiver.sendToPlanet("exit");
		planetReceiver.interrupt();
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

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public long getPlanetId() {
		return planetId;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public double getEnergie() {
		return energie;
	}
	
	public void setEnergie(double energie) {
		this.energie = energie;
	}
	

}

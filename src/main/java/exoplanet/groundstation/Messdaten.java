package exoplanet.groundstation;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.model.GROUND;

public class Messdaten {

	@JsonProperty("pid")
	private long pid;
	@JsonProperty("x")
	private int x;
	@JsonProperty("y")
	private int y;
	@JsonProperty("ground")
	private GROUND ground;
	@JsonProperty("temperature")
	private double temperature;
	
	public Messdaten (){
		
	}
	public Messdaten(long pid, int x, int y, GROUND ground, double temperature) {
		this.pid = pid;
		this.x = x;
		this.y = y;
		this.ground = ground;
		this.temperature = temperature;
	}
	
	public long getPid() {
		return pid;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public GROUND getGround() {
		return ground;
	}
	
	public double getTemperature() {
		return temperature;
	}
}

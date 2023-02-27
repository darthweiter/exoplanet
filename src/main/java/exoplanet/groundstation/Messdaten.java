package exoplanet.groundstation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Messdaten {

	@JsonProperty("pid")
	private long pid;
	@JsonProperty("x")
	private int x;
	@JsonProperty("y")
	private int y;
	@JsonProperty("ground")
	private String ground;
	@JsonProperty("temperature")
	private double temperature;
	
	public Messdaten (){
		
	}
	public Messdaten(long pid, int x, int y, String ground, double temperature) {
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
	
	public String getGround() {
		return ground;
	}
	
	public double getTemperature() {
		return temperature;
	}
}

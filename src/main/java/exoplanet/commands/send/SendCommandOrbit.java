package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.parsing.JsonPropertyValue;

public class SendCommandOrbit extends ACommandClass {

  public final static String CMD_NAME = "orbit";

  @JsonProperty(JsonPropertyValue.NAME)
  private final String name;

  public SendCommandOrbit(String roboterName) {
    super(CMD_NAME);
    this.name = roboterName;
  }

  public String getName() {
    return name;
  }
}

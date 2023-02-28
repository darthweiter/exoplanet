package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.JsonPropertyValue;

public class SendCommandOrbit extends ASendCommand {

  public final static String CMD_NAME = Command.orbit.name();

  @JsonProperty(JsonPropertyValue.NAME)
  private final String robotName;

  public SendCommandOrbit(String roboterName) {
    super(CMD_NAME);
    this.robotName = roboterName;
  }

  @Override
  public String toString() {
    return CMD_NAME + ":" + robotName;
  }

  public String getRobotName() {
    return robotName;
  }
}

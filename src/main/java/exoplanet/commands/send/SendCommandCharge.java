package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.JsonPropertyValue;

public class SendCommandCharge extends ASendCommand {

  public final static String CMD_NAME = Command.charge.name();

  @JsonProperty(JsonPropertyValue.DURATION)

  private final int duration;

  public SendCommandCharge(int duration) {
    super(CMD_NAME);
    this.duration = duration;
  }

  public int getDuration() {
    return duration;
  }

  @Override
  public String toString() {
    return CMD_NAME + ":" + duration;
  }
}

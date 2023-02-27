package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.parsing.JsonPropertyValue;

public class SendCommandCharge extends ACommandClass {

  public final static String CMD_NAME = "charge";

  @JsonProperty(JsonPropertyValue.DURATION)

  private final int duration;

  public SendCommandCharge(int duration) {
    super(CMD_NAME);
    this.duration = duration;
  }

  public int getDuration() {
    return duration;
  }
}

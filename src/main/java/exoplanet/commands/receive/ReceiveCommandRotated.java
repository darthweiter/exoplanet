package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.DIRECTION;
import exoplanet.parsing.JsonPropertyValue;

public class ReceiveCommandRotated extends ACommandClass {

  public final static String CMD_NAME = "rotated";

  @JsonProperty(JsonPropertyValue.DIRECTION)
  private DIRECTION direction;

  public ReceiveCommandRotated() {
    super(CMD_NAME);
  }

  public ReceiveCommandRotated(DIRECTION direction) {
    super(CMD_NAME);
    this.direction = direction;
  }

  public DIRECTION getDirection() {
    return direction;
  }

  public void setDirection(DIRECTION direction) {
    this.direction = direction;
  }

  @Override
  public String toString() {
    return CMD_NAME+":"+ direction;
  }
}

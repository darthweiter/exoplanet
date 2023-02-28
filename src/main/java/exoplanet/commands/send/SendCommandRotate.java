package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.model.Rotation;
import exoplanet.commands.JsonPropertyValue;

public class SendCommandRotate extends ASendCommand {

  public final static String CMD_NAME = Command.rotate.name();
  @JsonProperty(JsonPropertyValue.ROTATION)
  private final Rotation rotation;

  public SendCommandRotate(Rotation rotation) {
    super(CMD_NAME);
    this.rotation = rotation;
  }

  public Rotation getRotation() {
    return rotation;
  }

  @Override
  public String toString() {
    return CMD_NAME + ":" + rotation;
  }
}

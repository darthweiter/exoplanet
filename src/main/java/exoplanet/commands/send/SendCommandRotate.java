package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.Rotation;
import exoplanet.parsing.JsonPropertyValue;

public class SendCommandRotate extends ACommandClass {

  public final static String CMD_NAME = "rotate";
  @JsonProperty(JsonPropertyValue.ROTATION)
  private final Rotation rotation;

  public SendCommandRotate(Rotation rotation) {
    super(CMD_NAME);
    this.rotation = rotation;
  }

  public Rotation getRotation() {
    return rotation;
  }
}

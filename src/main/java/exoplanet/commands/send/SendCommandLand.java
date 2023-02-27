package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.Position;
import exoplanet.parsing.JsonPropertyValue;

public class SendCommandLand extends ACommandClass {

  public final static String CMD_NAME = "land";
  @JsonProperty(JsonPropertyValue.POSITION)
  private final Position position;

  public SendCommandLand(Position position) {
    super(CMD_NAME);
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }
}

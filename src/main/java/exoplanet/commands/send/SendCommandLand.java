package exoplanet.commands.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.model.Position;
import exoplanet.commands.JsonPropertyValue;

public class SendCommandLand extends ASendCommand {

  public final static String CMD_NAME = Command.land.name();
  @JsonProperty(JsonPropertyValue.POSITION)
  private final Position position;

  public SendCommandLand(Position position) {
    super(CMD_NAME);
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }

  @Override
  public String toString() {
    return CMD_NAME + ":" + JsonPropertyValue.POSITION +"|" + position.x() + "|" + position.y() + "|" + position.direction();
  }
}

package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.model.Position;
import exoplanet.commands.JsonPropertyValue;

public class ReceiveCommandPosition extends AReceiveCommand {

  public final static String CMD_NAME = Command.pos.name();

  @JsonProperty(JsonPropertyValue.POSITION)
  private Position position;

  public ReceiveCommandPosition() {
    super(CMD_NAME);
  }

  public ReceiveCommandPosition(Position position) {
    super(CMD_NAME);
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  @Override
  public String toString() {
    return CMD_NAME+":"+JsonPropertyValue.POSITION+"|"+position.x()+"|"+position.y()+"|"+position.direction();
  }
}

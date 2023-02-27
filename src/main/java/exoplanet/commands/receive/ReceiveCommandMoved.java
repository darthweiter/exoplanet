package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.Position;
import exoplanet.parsing.JsonPropertyValue;

public class ReceiveCommandMoved extends ACommandClass {
  public final static String CMD_NAME = "moved";

  @JsonProperty(JsonPropertyValue.POSITION)
  private Position position;

  public ReceiveCommandMoved() {
    super(CMD_NAME);
  }

  public ReceiveCommandMoved(Position position) {
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
    //moved:POSITION|x|y|direction
    return CMD_NAME+":"+JsonPropertyValue.POSITION+"|"+position.x()+"|"+position.y()+"|"+position.direction();
  }
}

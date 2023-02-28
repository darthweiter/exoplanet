package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.model.Measure;
import exoplanet.commands.model.Position;
import exoplanet.commands.JsonPropertyValue;

public class ReceiveCommandMoveScaned extends AReceiveCommand {

  public final static String CMD_NAME = Command.mvscaned.name();

  @JsonProperty(JsonPropertyValue.MEASURE)
  private Measure measure;

  @JsonProperty(JsonPropertyValue.POSITION)
  private Position position;

  public ReceiveCommandMoveScaned() {
    super(CMD_NAME);
  }

  public ReceiveCommandMoveScaned(Measure measure, Position position) {
    super(CMD_NAME);
    this.measure = measure;
    this.position = position;
  }

  public Measure getMeasure() {
    return measure;
  }

  public Position getPosition() {
    return position;
  }

  public void setMeasure(Measure measure) {
    this.measure = measure;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  @Override
  public String toString() {
    //mvscaned:MEASURE|Ground|temp:POSITION|x|y|direction
    return CMD_NAME+":"+JsonPropertyValue.MEASURE+"|"+measure.ground()+"|"+measure.temp()+":"+JsonPropertyValue.POSITION+"|"+position.x()+"|"+position.y()+"|"+position.direction();
  }
}

package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.Measure;
import exoplanet.parsing.JsonPropertyValue;

public class ReceiveCommandLanded extends ACommandClass {

  public final static String CMD_NAME = "landed";
  @JsonProperty(JsonPropertyValue.MEASURE)
  private Measure measure;

  public ReceiveCommandLanded() {
    super(CMD_NAME);
  }

  public ReceiveCommandLanded(Measure measure) {
    super(CMD_NAME);
    this.measure = measure;
  }

  public Measure getMeasure() {
    return measure;
  }

  public void setMeasure(Measure measure) {
    this.measure = measure;
  }

  @Override
  public String toString() {
    //landed:MEASURE|ground|temp
    return CMD_NAME + ":" + JsonPropertyValue.MEASURE + "|" + measure.ground() + "|" + measure.temp();
  }
}

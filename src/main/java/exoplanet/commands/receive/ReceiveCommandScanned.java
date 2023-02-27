package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.Measure;
import exoplanet.parsing.JsonPropertyValue;

public class ReceiveCommandScanned extends ACommandClass {

  public final static String CMD_NAME = "scaned";
  @JsonProperty(JsonPropertyValue.MEASURE)
  private Measure measure;

  public ReceiveCommandScanned() {
    super(CMD_NAME);
  }

  public ReceiveCommandScanned(Measure measure) {
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
    //scaned:MEASURE|ground|temp
    return CMD_NAME + ":" + JsonPropertyValue.MEASURE + "|" + measure.ground() + "|" + measure.temp();
  }
}

package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.commands.model.Status;
import exoplanet.parsing.JsonPropertyValue;

public class ReceiveCommandCharged extends ACommandClass {

  public final static String CMD_NAME = "charged";

  @JsonProperty(JsonPropertyValue.STATUS)
  private Status status;

  public ReceiveCommandCharged() {
    super(CMD_NAME);
  }

  public ReceiveCommandCharged(Status status) {
    super(CMD_NAME);
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return CMD_NAME+":"+status.temp()+"|"+status.energy()+"|"+status.message();
  }
}

package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.model.Status;
import exoplanet.commands.JsonPropertyValue;
import java.util.Arrays;
import java.util.List;

public class ReceiveCommandStatus extends AReceiveCommand {

  public final static String CMD_NAME = Command.status.name();

  @JsonProperty(JsonPropertyValue.STATUS)
  private Status status;

  public ReceiveCommandStatus() {
    super(CMD_NAME);
  }

  public ReceiveCommandStatus(Status status) {
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

  public List<String> getAllStatusMessages() {
    return Arrays.asList(status.message().split("\\|"));
  }
}

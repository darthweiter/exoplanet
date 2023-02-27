package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.ACommandClass;
import exoplanet.parsing.JsonPropertyValue;

public class ReceiveCommandError extends ACommandClass {

  public final static String CMD_NAME = "error";
  @JsonProperty(JsonPropertyValue.ERROR)
  private String errorMsg;

  public ReceiveCommandError() {
    super(CMD_NAME);
  }

  public ReceiveCommandError(String errorMsg) {
    super(CMD_NAME);
    this.errorMsg = errorMsg;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  @Override
  public String toString() {
    return CMD_NAME+":"+errorMsg;
  }
}

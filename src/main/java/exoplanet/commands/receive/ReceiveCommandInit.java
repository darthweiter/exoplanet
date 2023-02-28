package exoplanet.commands.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.Command;
import exoplanet.commands.model.Size;
import exoplanet.commands.JsonPropertyValue;

public class ReceiveCommandInit extends AReceiveCommand {

  public final static String CMD_NAME = Command.init.name();

  @JsonProperty(JsonPropertyValue.SIZE)
  private Size size;

  public ReceiveCommandInit() {
    super(CMD_NAME);
  }

  public ReceiveCommandInit(Size size) {
    super(CMD_NAME);
    this.size = size;
  }

  @Override
  public String toString() {
    return CMD_NAME+":"+JsonPropertyValue.SIZE+"|"+size.width()+"|"+size.height();
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public Size getSize() {
    return size;
  }
}

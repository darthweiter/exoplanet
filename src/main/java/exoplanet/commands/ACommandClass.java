package exoplanet.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ACommandClass {
  @JsonProperty("CMD")
  protected final String cmd;

  public ACommandClass(String cmd) {
    this.cmd = cmd;
  }

  public String getCmd() {
    return cmd;
  }
}

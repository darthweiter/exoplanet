package exoplanet.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ACommandClass{

  protected static ObjectMapper mapper = new ObjectMapper();

  @JsonProperty("CMD")
  protected final String cmd;

  public ACommandClass(String cmd) {
    this.cmd = cmd;
  }

  public String getCmd() {
    return cmd;
  }

  public String toJson() throws JsonProcessingException {
    return mapper.writeValueAsString(this);
  }

  public abstract String toString();
}

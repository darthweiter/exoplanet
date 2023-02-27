package exoplanet.commands.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.parsing.JsonPropertyValue;

public record Measure(@JsonProperty(JsonPropertyValue.GROUND) GROUND ground, @JsonProperty(JsonPropertyValue.TEMP) float temp) {

}

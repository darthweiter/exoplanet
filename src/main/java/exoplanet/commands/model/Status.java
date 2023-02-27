package exoplanet.commands.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.parsing.JsonPropertyValue;

public record Status(@JsonProperty(JsonPropertyValue.TEMP) float temp, @JsonProperty(JsonPropertyValue.ENERGY) int energy, @JsonProperty(JsonPropertyValue.MESSAGE) String message) {

}

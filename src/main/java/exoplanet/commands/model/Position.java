package exoplanet.commands.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.parsing.JsonPropertyValue;

public record Position(@JsonProperty(JsonPropertyValue.X) int x, @JsonProperty(JsonPropertyValue.Y) int y, @JsonProperty(JsonPropertyValue.DIRECTION) DIRECTION direction) {
}

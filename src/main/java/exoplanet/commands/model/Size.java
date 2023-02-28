package exoplanet.commands.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import exoplanet.commands.JsonPropertyValue;

public record Size(@JsonProperty(JsonPropertyValue.WIDTH) int width, @JsonProperty(JsonPropertyValue.HEIGHT) int height) {

}

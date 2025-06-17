package dev.emberline.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ConfigLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static JsonNode loadConfig(String configFile) {
        try {
            return objectMapper.readTree(ConfigLoader.class.getResourceAsStream(configFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadConfig(String filename, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(loadConfig(filename), valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadConfig(JsonNode node, Class<T> valueType) {
        try {
            return new ObjectMapper().treeToValue(node, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

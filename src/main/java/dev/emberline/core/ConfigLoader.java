package dev.emberline.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class ConfigLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ParameterNamesModule())
            .findAndRegisterModules();

    public static JsonNode loadNode(String resourcePath) {
        try {
            return objectMapper.readTree(ConfigLoader.class.getResourceAsStream(resourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadConfig(String resourcePath, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(loadNode(resourcePath), valueType);
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

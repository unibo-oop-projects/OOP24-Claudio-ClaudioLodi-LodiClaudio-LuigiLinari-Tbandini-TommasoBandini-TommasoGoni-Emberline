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

    public static JsonNode loadNode(final String resourcePath) {
        try {
            return objectMapper.readTree(ConfigLoader.class.getResourceAsStream(resourcePath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadConfig(final String resourcePath, final Class<T> valueType) {
        try {
            return objectMapper.treeToValue(loadNode(resourcePath), valueType);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadConfig(final JsonNode node, final Class<T> valueType) {
        try {
            return new ObjectMapper().treeToValue(node, valueType);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

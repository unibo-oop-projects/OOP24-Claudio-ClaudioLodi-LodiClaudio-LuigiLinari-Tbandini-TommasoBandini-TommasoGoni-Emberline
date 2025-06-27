package dev.emberline.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

/**
 * The {@code ConfigLoader} class provides utility methods for loading configuration
 * files and converting them into objects or JsonNode representations. This class
 * utilizes Jackson's {@link ObjectMapper} for reading and deserializing configurations.
 * <p>
 * It supports loading JSON content from a resource path, and it offers methods to
 * transform the loaded content into desired data types.
 */
public final class ConfigLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new ParameterNamesModule())
            .findAndRegisterModules();

    private ConfigLoader() {

    }

    /**
     * Loads a JSON node from a given resource path.
     * The method reads the content of the file located at the specified resource path
     * and converts it into a {@link JsonNode} object using the Jackson library.
     * If an I/O error occurs during the process, a {@link RuntimeException} is thrown.
     *
     * @param resourcePath the path to the resource file, relative to the class's location.
     * @return a {@link JsonNode} representation of the JSON content from the resource file.
     * @throws RuntimeException if an error occurs while reading or parsing the resource.
     */
    public static JsonNode loadNode(final String resourcePath) {
        try {
            return OBJECT_MAPPER.readTree(ConfigLoader.class.getResourceAsStream(resourcePath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a configuration object from a JSON resource file located at the specified path
     * and converts it into an instance of the desired type.
     * The method utilizes Jackson's {@link ObjectMapper} to deserialize the JSON content
     * into the specified type.
     *
     * @param resourcePath the path to the JSON resource file, relative to the class's location.
     * @param valueType the {@link Class} object of the type to which the JSON content should be deserialized.
     * @param <T> the type of the configuration object to be returned.
     * @return an instance of type {@code T} populated with values from the loaded JSON content.
     * @throws RuntimeException if the JSON resource cannot be read or if deserialization fails.
     */
    public static <T> T loadConfig(final String resourcePath, final Class<T> valueType) {
        try {
            return OBJECT_MAPPER.treeToValue(loadNode(resourcePath), valueType);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes a given {@link JsonNode} into a specified object type using Jackson's {@link ObjectMapper}.
     *
     * @param node the {@link JsonNode} containing the JSON data to be deserialized.
     * @param valueType the {@link Class} object representing the type into which the JSON data should be deserialized.
     * @param <T> the type of the object to be returned.
     * @return an instance of type {@code T} populated with values from the given JSON node.
     * @throws RuntimeException if deserialization fails due to {@link JsonProcessingException}.
     */
    public static <T> T loadConfig(final JsonNode node, final Class<T> valueType) {
        try {
            return new ObjectMapper().treeToValue(node, valueType);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

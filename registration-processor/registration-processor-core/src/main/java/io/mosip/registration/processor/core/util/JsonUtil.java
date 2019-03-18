package io.mosip.registration.processor.core.util;
	
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * This class provides JSON utilites.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class JsonUtil {

	/**
	 * Instantiates a new json util.
	 */
	private JsonUtil() {

	}

	/**
	 * This method converts InputStream to JavaObject.
	 *
	 * @param stream            The stream that needs to be converted
	 * @param clazz            The class to which conversion is required
	 * @return The converted Java object
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws JsonSyntaxException the json syntax exception
	 * @throws JsonIOException the json IO exception
	 */
	public static Object inputStreamtoJavaObject(InputStream stream, Class<?> clazz)
			throws UnsupportedEncodingException {
		JsonParser jsonParser = new JsonParser();
		Gson gson = new Gson();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(new InputStreamReader(stream, "UTF-8"));
		try {
			return gson.fromJson(jsonObject, clazz);
		} catch (Exception e) {
			throw new UnsupportedEncodingException(PlatformErrorMessages.RPR_CMB_UNSUPPORTED_ENCODING.getMessage());
		}
	}

	/**
	 * Gets the JSON object.
	 *
	 * @param jsonObject
	 *            the json object
	 * @param key
	 *            the key
	 * @return the JSON object
	 */
	public static JSONObject getJSONObject(JSONObject jsonObject, Object key) {
		LinkedHashMap identity = (LinkedHashMap) jsonObject.get(key);
		return identity != null ? new JSONObject(identity) : null;
	}

	/**
	 * Gets the JSON array.
	 *
	 * @param jsonObject
	 *            the json object
	 * @param key
	 *            the key
	 * @return the JSON array
	 */
	public static JSONArray getJSONArray(JSONObject jsonObject, Object key) {
		ArrayList value = (ArrayList) jsonObject.get(key);
		JSONArray jsonArray = new JSONArray();
		jsonArray.addAll(value);

		return jsonArray;

	}

	/**
	 * Gets the JSON value.
	 *
	 * @param <T>
	 *            the generic type
	 * @param jsonObject
	 *            the json object
	 * @param key
	 *            the key
	 * @return the JSON value
	 */
	public static <T> T getJSONValue(JSONObject jsonObject, String key) {
		T value = (T) jsonObject.get(key);
		return value;
	}

	/**
	 * Gets the JSON object.
	 *
	 * @param jsonObject
	 *            the json object
	 * @param key
	 *            the key
	 * @return the JSON object
	 */
	public static JSONObject getJSONObjectFromArray(JSONArray jsonObject, int key) {
		LinkedHashMap identity = (LinkedHashMap) jsonObject.get(key);
		return identity != null ? new JSONObject(identity) : null;
	}

	public static <T> T objectMapperReadValue(String jsonString, Class<?> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return (T) objectMapper.readValue(jsonString, clazz);
	}

}

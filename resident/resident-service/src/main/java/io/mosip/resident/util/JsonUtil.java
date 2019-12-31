package io.mosip.resident.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.resident.dto.JsonValue;

/**
 * This class provides JSON utilites.
 *
 * @author Girish Yarru
 * @since 1.0
 */
public class JsonUtil {

	private static final String LANGUAGE = "language";
	private static final String VALUE = "value";
	private static ObjectMapper objectMapper = new ObjectMapper();

	private JsonUtil() {

	}

	/**
	 * This method returns the Json Object as value from identity.json
	 * object(JSONObject). jsonObject -> then identity demographic json object key
	 * -> demographic json label name EX:- demographicIdentity : { "identity" : {
	 * "fullName" : [ { "language" : "eng", "value" : "Taleev Aalam" }, {
	 * "language": "ara", "value" : "Taleev Aalam" } ] }
	 *
	 * method call :- getJSONObject(demographicIdentity,identity)
	 *
	 * @param jsonObject the json object
	 * @param key        the key
	 * @return the JSON object
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getJSONObject(JSONObject jsonObject, Object key) {
		LinkedHashMap<Object, Object> identity = null;
		if (jsonObject.get(key) instanceof LinkedHashMap) {
			identity = (LinkedHashMap<Object, Object>) jsonObject.get(key);
		}
		return identity != null ? new JSONObject(identity) : null;
	}

	/**
	 * This method returns JSONArray from JSONObject. argument 'jsonObject' ->
	 * demographic identity json as JSONObject. argument key -> label name of
	 * demographic identity json. Ex:- "identity" : { "fullName" : [ { "language" :
	 * "eng", "value" : "Taleev Aalam" }, { "language" : "ara", "value" : "Taleev
	 * Aalam" } ] }
	 *
	 * @param jsonObject the json object
	 * @param key        the key
	 * @return the JSON array
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONArray getJSONArray(JSONObject jsonObject, Object key) {
		ArrayList value = (ArrayList) jsonObject.get(key);
		if (value == null)
			return null;
		JSONArray jsonArray = new JSONArray();
		jsonArray.addAll(value);

		return jsonArray;

	}

	/**
	 * Gets the JSON value.
	 *
	 * @param            <T> the generic type
	 * @param jsonObject the json object
	 * @param key        the key
	 * @return the JSON value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getJSONValue(JSONObject jsonObject, String key) {
		T value = (T) jsonObject.get(key);
		return value;
	}

	/**
	 * Iterates the JSONArray and returns JSONObject for given index.
	 *
	 * @param jsonObject the json object
	 * @param key        the key
	 * @return the JSON object
	 */
	@SuppressWarnings("rawtypes")
	public static JSONObject getJSONObjectFromArray(JSONArray jsonObject, int key) {
		LinkedHashMap identity = (LinkedHashMap) jsonObject.get(key);
		return identity != null ? new JSONObject(identity) : null;
	}

	/**
	 * Object mapper read value. This method maps the jsonString to particular type
	 * 
	 * @param            <T> the generic type
	 * @param jsonString the json string
	 * @param clazz      the clazz
	 * @return the t
	 * @throws JsonParseException   the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readValue(String jsonString, Class<?> clazz) throws IOException {
		return (T) objectMapper.readValue(jsonString, clazz);
	}

	/**
	 * Gets the json values. Returns JsonValue[] java array for the particular key
	 * in demographic json.
	 * 
	 * @param identityKey the identity key
	 * @return the json values
	 * @throws ReflectiveOperationException
	 */
	public static JsonValue[] getJsonValues(JSONObject demographicIdentity, Object identityKey)
			throws ReflectiveOperationException {
		JSONArray demographicJsonNode = null;

		if (demographicIdentity != null)
			demographicJsonNode = JsonUtil.getJSONArray(demographicIdentity, identityKey);
		return (demographicJsonNode != null)
				? (JsonValue[]) mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode)
				: null;

	}

	/**
	 * Map json node to java object.
	 *
	 * @param                     <T> the generic type
	 * @param genericType         the generic type
	 * @param demographicJsonNode the demographic json node
	 * @return the t[]
	 * @throws ReflectiveOperationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode)
			throws ReflectiveOperationException {
		String language;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = JsonUtil.getJSONObjectFromArray(demographicJsonNode, i);
				if (objects != null) {
					language = (String) objects.get(LANGUAGE);
					value = (String) objects.get(VALUE);

					Field languageField = jsonNodeElement.getClass().getDeclaredField(LANGUAGE);
					languageField.setAccessible(true);
					languageField.set(jsonNodeElement, language);

					Field valueField = jsonNodeElement.getClass().getDeclaredField(VALUE);
					valueField.setAccessible(true);
					valueField.set(jsonNodeElement, value);

					javaObject[i] = jsonNodeElement;
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {

			throw e;

		} catch (NoSuchFieldException | SecurityException e) {

			throw e;

		}

		return javaObject;

	}

	public static String writeValueAsString(Object obj) throws IOException {
		return objectMapper.writeValueAsString(obj);
	}

}

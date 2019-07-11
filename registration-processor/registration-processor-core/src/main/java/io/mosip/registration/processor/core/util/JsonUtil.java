package io.mosip.registration.processor.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.util.exception.FieldNotFoundException;
import io.mosip.registration.processor.core.util.exception.InstantanceCreationException;

/**
 * This class provides JSON utilites.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class JsonUtil {

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/**
	 * Instantiates a new json util.
	 */
	private JsonUtil() {

	}

	/**
	 * This method converts InputStream to JavaObject.
	 *
	 * @param stream
	 *            The stream that needs to be converted
	 * @param clazz
	 *            The class to which conversion is required
	 * @return The converted Java object
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @throws JsonSyntaxException
	 *             the json syntax exception
	 * @throws JsonIOException
	 *             the json IO exception
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
	 * This method returns the Json Object as value from identity.json
	 * object(JSONObject). jsonObject -> then identity demographic json object key
	 * -> demographic json label name EX:- demographicIdentity : { "identity" : {
	 * "fullName" : [ { "language" : "eng", "value" : "Taleev Aalam" }, {
	 * "language": "ara", "value" : "Taleev Aalam" } ] }
	 *
	 * method call :- getJSONObject(demographicIdentity,identity)
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
	 * This method returns JSONArray from JSONObject. argument 'jsonObject' ->
	 * demographic identity json as JSONObject. argument key -> label name of
	 * demographic identity json. Ex:- "identity" : { "fullName" : [ { "language" :
	 * "eng", "value" : "Taleev Aalam" }, { "language" : "ara", "value" : "Taleev
	 * Aalam" } ] }
	 *
	 * @param jsonObject
	 *            the json object
	 * @param key
	 *            the key
	 * @return the JSON array
	 */
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
	 * Iterates the JSONArray and returns JSONObject for given index.
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

	/**
	 * Object mapper read value. This method maps the jsonString to particular type
	 * 
	 * @param <T>
	 *            the generic type
	 * @param jsonString
	 *            the json string
	 * @param clazz
	 *            the clazz
	 * @return the t
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T objectMapperReadValue(String jsonString, Class<?> clazz) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return (T) objectMapper.readValue(jsonString, clazz);
	}

	/**
	 * Gets the json values. Returns JsonValue[] java array for the particular key
	 * in demographic json.
	 * 
	 * @param identityKey
	 *            the identity key
	 * @return the json values
	 */
	public static JsonValue[] getJsonValues(JSONObject demographicIdentity, Object identityKey) {
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
	 * @param <T>
	 *            the generic type
	 * @param genericType
	 *            the generic type
	 * @param demographicJsonNode
	 *            the demographic json node
	 * @return the t[]
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
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

			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);

		} catch (NoSuchFieldException | SecurityException e) {

			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);

		}

		return javaObject;

	}

}

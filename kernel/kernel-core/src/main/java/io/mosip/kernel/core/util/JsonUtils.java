package io.mosip.kernel.core.util;

import java.io.File;

import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.constant.JsonUtilConstants;
import io.mosip.kernel.core.util.exception.JsonGenerationException;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.core.util.exception.JsonProcessingException;

/**
 * This class contains methods used for operations on JSON type data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class JsonUtils {

	private JsonUtils() {

	}

	/**
	 * This function converts a java object and stores the processed JSON in a file
	 * 
	 * @param className
	 *            object of the class to be converted to JSON File
	 * @param location
	 *            location where to store the generated JSON file
	 * @return true if file is successfully generated,false if file is not generated
	 * @throws JsonGenerationException
	 *             when JSON is not properly generated
	 * @throws JsonMappingException
	 *             when JSON is not properly mapped
	 * @throws IOException
	 *             when file is not found
	 */
	public static boolean javaObjectToJsonFile(Object className, String location)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		File file = new File(location);
		try {
			objectMapper.writeValue(file, className);
		} catch (com.fasterxml.jackson.core.JsonGenerationException e) {
			throw new JsonGenerationException(JsonUtilConstants.MOSIP_JSON_GENERATION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_GENERATION_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (com.fasterxml.jackson.databind.JsonMappingException e) {
			throw new JsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}
		return (file.exists());

	}

	/**
	 * This function converts the java object and returns a JSON String
	 * 
	 * @param className
	 *            object of the class to be converted to JSON File
	 * @return generated JSON String
	 * @throws JsonProcessingException
	 *             when JSON is not properly processed
	 */
	public static String javaObjectToJsonString(Object className) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String outputJson = null;

		try {
			outputJson = objectMapper.writeValueAsString(className);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			throw new JsonProcessingException(JsonUtilConstants.MOSIP_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PROCESSING_EXCEPTION.getErrorMessage(), e.getCause());
		}

		return (outputJson);

	}

	/**
	 * This method converts the JSON String input and maps it to the java object
	 * 
	 * @param className
	 *            class name to which the JSON String is to be mapped
	 * @param jsonString
	 *            input JSON String(always in double quotes) (eg."{color=Black,
	 *            type=FIAT}")
	 * @return class object with the JSON string parsed to object
	 * @throws JsonParseException
	 *             when JSON is not properly parsed
	 * @throws JsonMappingException
	 *             when JSON is not properly mapped
	 * @throws IOException
	 *             when location is not found
	 */
	public static Object jsonStringToJavaObject(Class<?> className, String jsonString)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		Object returnObject = null;

		try {
			returnObject = objectMapper.readValue(jsonString, className);
		} catch ( com.fasterxml.jackson.core.JsonParseException e) {
			throw new JsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (com.fasterxml.jackson.databind.JsonMappingException e) {
			throw new JsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}

		return returnObject;

	}

	/**
	 * This method converts the JSON file input and maps it to the java object
	 * 
	 * @param className
	 *            class name to which the JSON file is to be mapped
	 * @param fileLocation
	 *            location of the JSON file
	 * @return JSON mapped object
	 * @throws JsonParseException
	 *             when JSON is not properly parsed
	 * @throws JsonMappingException
	 *             when JSON is not properly mapped
	 * @throws IOException
	 *             when file is not found
	 */
	public static Object jsonFileToJavaObject(Class<?> className, String fileLocation)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		Object returnObject = null;
		try {
			returnObject = objectMapper.readValue(new File(fileLocation), className);
		} catch ( com.fasterxml.jackson.core.JsonParseException e) {
			throw new JsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (com.fasterxml.jackson.databind.JsonMappingException e) {
			throw new JsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}
		return returnObject;
	}

	/**
	 * This function returns the value associated with the label of the input JSON
	 * 
	 * @param jsonString
	 *            input JSON String(always in double quotes) (eg."{color=Black,
	 *            type=FIAT}")
	 * @param key
	 *            label of the JSON String whose value is to be retrieved
	 * @return value of the corresponding key input
	 * @throws IOException
	 *             when file is not found
	 */
	public static String jsonToJacksonJson(String jsonString, String key) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(jsonString);
		} catch (java.io.IOException e) {
			throw new IOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}
		return (jsonNode.get(key).asText());
	}

	/**
	 * This method converts a JSON String containing multiple JSON and stores them
	 * in a java list
	 * 
	 * @param jsonArray
	 *            input String containing array of JSON string(always in double
	 *            quotes) (eg."[{color=Black, type=BMW}, {color=Red, type=FIAT}]")
	 * @return list of JSON string
	 * @throws JsonParseException
	 *             when JSON is not properly parsed
	 * @throws JsonMappingException
	 *             when JSON is not properly mapped
	 * @throws IOException
	 *             when file is not found
	 */
	public static List<Object> jsonStringToJavaList(String jsonArray)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		List<Object> javaList = null;
		try {
			javaList = objectMapper.readValue(jsonArray, new TypeReference<List<Object>>() {
			});
		} catch ( com.fasterxml.jackson.core.JsonParseException e) {
			throw new JsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (com.fasterxml.jackson.databind.JsonMappingException e) {
			throw new JsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}
		return javaList;
	}

	/**
	 * This method converts a JSON String containing multiple JSON and stores them
	 * in a java Map
	 * 
	 * @param jsonString
	 *            input String containing array of JSON string(always in double
	 *            quotes) (eg."[{color=Black, type=BMW}, {color=Red, type=FIAT}]")
	 * @return java map containing JSON inputs
	 * @throws JsonParseException
	 *             when JSON is not properly parsed
	 * @throws JsonMappingException
	 *             when JSON is not properly mapped
	 * @throws IOException
	 *             when file is not found
	 */
	public static Map<String, Object> jsonStringToJavaMap(String jsonString)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		Map<String, Object> javaMap = null;
		try {
			javaMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
			});
		} catch ( com.fasterxml.jackson.core.JsonParseException e) {
			throw new JsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (com.fasterxml.jackson.databind.JsonMappingException e) {
			throw new JsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (java.io.IOException e) {
			throw new IOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}
		return javaMap;
	}

}
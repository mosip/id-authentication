package org.mosip.kernel.core.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.mosip.kernel.core.util.constant.JsonUtilConstants;
import org.mosip.kernel.core.util.exception.MosipIOException;
import org.mosip.kernel.core.util.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.util.exception.MosipJsonMappingException;
import org.mosip.kernel.core.util.exception.MosipJsonParseException;
import org.mosip.kernel.core.util.exception.MosipJsonProcessingException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
	 * @throws MosipJsonGenerationException
	 *             when JSON is not properly generated
	 * @throws MosipJsonMappingException
	 *             when JSON is not properly mapped
	 * @throws MosipIOException
	 *             when file is not found
	 */
	public static boolean javaObjectToJsonFile(Object className, String location)
			throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		File file = new File(location);
		try {
			objectMapper.writeValue(file, className);
		} catch (JsonGenerationException e) {
			throw new MosipJsonGenerationException(JsonUtilConstants.MOSIP_JSON_GENERATION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_GENERATION_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (JsonMappingException e) {
			throw new MosipJsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (IOException e) {
			throw new MosipIOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipJsonProcessingException
	 *             when JSON is not properly processed
	 */
	public static String javaObjectToJsonString(Object className) throws MosipJsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String outputJson = null;

		try {
			outputJson = objectMapper.writeValueAsString(className);
		} catch (JsonProcessingException e) {
			throw new MosipJsonProcessingException(JsonUtilConstants.MOSIP_JSON_PROCESSING_EXCEPTION.getErrorCode(),
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
	 * @throws MosipJsonParseException
	 *             when JSON is not properly parsed
	 * @throws MosipJsonMappingException
	 *             when JSON is not properly mapped
	 * @throws MosipIOException
	 *             when location is not found
	 */
	public static Object jsonStringToJavaObject(Class<?> className, String jsonString)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		Object returnObject = null;

		try {
			returnObject = objectMapper.readValue(jsonString, className);
		} catch (JsonParseException e) {
			throw new MosipJsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (JsonMappingException e) {
			throw new MosipJsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (IOException e) {
			throw new MosipIOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipJsonParseException
	 *             when JSON is not properly parsed
	 * @throws MosipJsonMappingException
	 *             when JSON is not properly mapped
	 * @throws MosipIOException
	 *             when file is not found
	 */
	public static Object jsonFileToJavaObject(Class<?> className, String fileLocation)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		Object returnObject = null;
		try {
			returnObject = objectMapper.readValue(new File(fileLocation), className);
		} catch (JsonParseException e) {
			throw new MosipJsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (JsonMappingException e) {
			throw new MosipJsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (IOException e) {
			throw new MosipIOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipIOException
	 *             when file is not found
	 */
	public static String jsonToJacksonJson(String jsonString, String key) throws MosipIOException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(jsonString);
		} catch (IOException e) {
			throw new MosipIOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipJsonParseException
	 *             when JSON is not properly parsed
	 * @throws MosipJsonMappingException
	 *             when JSON is not properly mapped
	 * @throws MosipIOException
	 *             when file is not found
	 */
	public static List<Object> jsonStringToJavaList(String jsonArray)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		List<Object> javaList = null;
		try {
			javaList = objectMapper.readValue(jsonArray, new TypeReference<List<Object>>() {
			});
		} catch (JsonParseException e) {
			throw new MosipJsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (JsonMappingException e) {
			throw new MosipJsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (IOException e) {
			throw new MosipIOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
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
	 * @throws MosipJsonParseException
	 *             when JSON is not properly parsed
	 * @throws MosipJsonMappingException
	 *             when JSON is not properly mapped
	 * @throws MosipIOException
	 *             when file is not found
	 */
	public static Map<String, Object> jsonStringToJavaMap(String jsonString)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		Map<String, Object> javaMap = null;
		try {
			javaMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonParseException e) {
			throw new MosipJsonParseException(JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_PARSE_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (JsonMappingException e) {
			throw new MosipJsonMappingException(JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_JSON_MAPPING_ERROR_CODE.getErrorMessage(), e.getCause());
		} catch (IOException e) {
			throw new MosipIOException(JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorCode(),
					JsonUtilConstants.MOSIP_IO_EXCEPTION_ERROR_CODE.getErrorMessage(), e.getCause());
		}
		return javaMap;
	}

}
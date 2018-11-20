package io.mosip.registration.processor.core.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * This class provides JSON utilites
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class JsonUtil {

	private JsonUtil() {

	}

	/**
	 * This method converts InputStream to JavaObject
	 * 
	 * @param stream
	 *            The stream that needs to be converted
	 * @param clazz
	 *            The class to which conversion is required
	 * @return The converted Java object
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws UnsupportedEncodingException
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

}

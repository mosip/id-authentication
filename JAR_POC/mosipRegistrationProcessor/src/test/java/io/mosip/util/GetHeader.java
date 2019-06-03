package io.mosip.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetHeader {

	public static HashMap<String, String> getHeader(JSONObject object) throws JsonParseException, JsonMappingException, IOException {
		HashMap<String,String> result =
		        new ObjectMapper().readValue(object.toString(), HashMap.class);
		return result;
	}
}

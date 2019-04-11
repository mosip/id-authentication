package io.mosip.preregistration.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.mosip.service.BaseTestCase;

public class PreRegistrationUtil 
{
	/**
	 * Declaration of all variables
	 **/
	
	static JSONObject request;
	static String folder = "preReg";
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	
	/*
	 * We configure the jsonProvider using Configuration builder.
	 */
	
	public Configuration setConfigIntialize()
	{
		Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
				.mappingProvider(new JacksonMappingProvider()).build();
		return config;
		
	}
	
	
	
	/*
	 * Generic method for dynamically change the request values in json file 
	 * 
	 */
	
	
	
	public ObjectNode dynamicJsonRequest(String jsonPathTraverse,String jsonSetVal,String readFilePath,String writeFilePath) {
		
		String yourActualJSONString = null;
		ObjectNode newJson = null;
		Configuration config = setConfigIntialize();
		
		
		
		try {
			yourActualJSONString = new String(Files.readAllBytes(Paths.get(readFilePath)), StandardCharsets.UTF_8);
		    newJson=JsonPath.using(config).parse(yourActualJSONString).set(jsonPathTraverse,jsonSetVal).json();
			FileWriter writer = new FileWriter(new File(writeFilePath));
			writer.append(newJson.toString());
			writer.flush();
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return newJson;
		
	}
	
	
	/*
	 * Generic method to fetch the dynamic request json
	 * 
	 */

	public JSONObject requestJson(String filepath) {

		String configPath = "src/test/resources/" + folder + "/" + filepath;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();

		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return request;

	}
	
	
	/*
	 * Function to generate the random created by data
	 * 
	 */
	public static int createdBy() {
		Random rand = new Random();
		int num = rand.nextInt(9000000) + 1000000000;
		return num;

	}

	
	/**
	 * Method for converting JSON object into HashMap
	 * @param object
	 * @return
	 */
	public static Map<String, Object> toMap(JSONObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keySet().iterator();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}
	/**
	 * Converting JSON object into Map
	 * @param json
	 * @return
	 */
	public static Map<String, Object> jsonToMap(JSONObject json) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		if (json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}
	/**
	 * Method for converting JSONArray into List
	 * @param array
	 * @return
	 */
	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}


	
	/*
	 * Generic method to compare Values
	 * 
	 */
	public void compareValues(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
			logger.info("values are equal");
		} catch (Exception e) {
			logger.info("values are not equal");
		}
	}
	
	
	
	
}

package io.mosip.authentication.testdata;

import java.io.BufferedWriter; 
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;
import io.mosip.authentication.testdata.keywords.KernelKeywordUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;
import io.mosip.authentication.testdata.keywords.PreRegKeywordUtil;
import io.mosip.authentication.testdata.keywords.RegKeywordUtil;

/**
 * Precondtion json file according to the input and mapping provided in test
 * data yml file
 * 
 * @author Vignesh
 */
public class Precondtion {
	
	private static Logger logger = Logger.getLogger(Precondtion.class);

	/**
	 * Method will return updated json field value , it will set property of json
	 * according to the json mapping provided in test data yml file
	 * 
	 * @param inputFilePath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @param propFileName
	 * @return
	 */
	public Map<String, String> parseAndWriteTestDataJsonFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(inputFilePath)), StandardCharsets.UTF_8), Object.class);
			fieldvalue = getObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to
																									// add
			for (Entry<String, String> map : fieldvalue.entrySet()) {
				if (map.getValue().contains("LONG:")) {
					String value = map.getValue().replace("LONG:", "");
					PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
							Long.parseLong(value));
				} else if (map.getValue().contains("DOUBLE:")) {
					String value = map.getValue().replace("DOUBLE:", "");
					PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
							Double.parseDouble(value));
				} else if (map.getValue().contains("BOOLEAN:")) {
					String value = map.getValue();
					if (value.contains("true"))
						PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
								true);
					if (value.contains("false"))
						PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
								false);
				} else
					PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
							map.getValue());
			}
			mapper.writeValue(new FileOutputStream(outputFilePath), jsonObj);
			String outputJson = new String(Files.readAllBytes(Paths.get(outputFilePath)), StandardCharsets.UTF_8);
			// Replacing the version in request
			outputJson = outputJson.replace("$version$", RunConfig.getAuthVersion());
			outputJson = outputJson.replaceAll("$version$", RunConfig.getAuthVersion());
			if (outputJson.contains("$REMOVE$"))
				outputJson = removeObject(new JSONObject(outputJson));
			writeFile(outputFilePath, outputJson);
			logger.info("Updated json file location: " + outputJson.toString());
			logger.info("Updated json file content: " + toPrettyFormat(outputJson.toString()));
			return fieldvalue;
		} catch (Exception e) {
			logger.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return fieldvalue;
		}
	}
	
	public Map<String, String> parseAndWritePropertyFile(String auditMappingPath,Map<String, String> fieldvalue,
			String outputFilePath) {
		try {
			fieldvalue = getObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to add
			Map<String, String> auditTxnValue = new HashMap<String, String>();
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				String orgKey = getFieldHierarchy(auditMappingPath).get(entry.getKey()).toString();
				auditTxnValue.put(orgKey, entry.getValue());
			}
			Properties prop = new Properties();
			OutputStream output = new FileOutputStream(outputFilePath);
				for (Entry<String, String> entry : auditTxnValue.entrySet()) {
					prop.setProperty(entry.getKey(), entry.getValue());
				}
				prop.store(output, null);
			return auditTxnValue;
		} catch (Exception e) {
			logger.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * This method is to remove objects where ever REMOVE keyword is provided in
	 * test data
	 * 
	 * @param object
	 * @return
	 */
	public String removeObject(JSONObject object) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				String finalarrayContent = "";
				for (int i = 0; i < array.length(); ++i) {
					String arrayContent = removeObject(new JSONObject(array.get(i).toString()), finalarrayContent);
					if (!arrayContent.equals("{}"))
						finalarrayContent = finalarrayContent + "," + arrayContent;
				}
				finalarrayContent = finalarrayContent.substring(1, finalarrayContent.length());
				object.put(key, new JSONArray("[" + finalarrayContent + "]"));
			} else if (value instanceof JSONObject) {
				String objectContent = removeObject(new JSONObject(value.toString()));
				object.put(key, new JSONObject(objectContent));
			}
			if (value.toString().equals("$REMOVE$")) {
				object.remove(key);
				keysItr = object.keys();
			}
		}
		return object.toString();
	}
	
	private String removeObject(JSONObject object,String tempArrayContent) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				for (int i = 0; i < array.length(); ++i) {
					String arrayContent = removeObject(new JSONObject(array.get(i).toString()));
					object.put(key, new JSONArray("[" + arrayContent + "]"));					
				}
			} else if (value instanceof JSONObject) {
				String objectContent = removeObject(new JSONObject(value.toString()));
				object.put(key, new JSONObject(objectContent));
			}
			if (value.toString().equals("$REMOVE$")) {
				object.remove(key);
				keysItr = object.keys();
			}
		}
		return object.toString();
	}
	
	/**
	 * Get json mapper from the mapping properties file
	 * 
	 * @param propFileName
	 * @return
	 */
	public Properties getFieldHierarchy(String propFileName) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propFileName);
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public String toPrettyFormat(String jsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonString);
		return gson.toJson(je);
	}
	
	/**
	 * Write file with UTF8 charset to support local language character
	 * 
	 * @param filePath
	 * @param content
	 * @return
	 */
	public boolean writeFile(String filePath, String content) {
		try {
			Path path = Paths.get(filePath);
			Charset charset = Charset.forName("UTF-8");
			BufferedWriter writer = Files.newBufferedWriter(path,StandardCharsets.UTF_8);
			writer.write(content);
            writer.flush();
            writer.close();           
			return true;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	public KeywordUtil getObject(String moduleName) {
		KeywordUtil objKeywordUtil = null;
		if (moduleName.equalsIgnoreCase("ida"))
			objKeywordUtil = new IdaKeywordUtil();
		else if (moduleName.equalsIgnoreCase("prereg"))
			objKeywordUtil = new PreRegKeywordUtil();
		else if (moduleName.equalsIgnoreCase("kernel"))
			objKeywordUtil = new KernelKeywordUtil();
		else if (moduleName.equalsIgnoreCase("reg"))
			objKeywordUtil = new RegKeywordUtil();
		return objKeywordUtil;
	}

}


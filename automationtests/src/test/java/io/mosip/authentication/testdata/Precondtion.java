package io.mosip.authentication.testdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.OrderedJSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.idRepository.fw.util.IdRepoTestsUtil;
import io.mosip.authentication.testdata.keywords.IdRepoKeywordUtil;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;

/**
 * Precondtion json file according to the input and mapping provided in test
 * data yml file
 * 
 * @author Vignesh
 */
public class Precondtion {
	
	private static final Logger PRECON_LOGGER = Logger.getLogger(Precondtion.class);

	/**
	 * Method will return updated json field value , it will set property of json
	 * according to the json mapping provided in test data yml file
	 * 
	 * @param inputFilePath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @param propFileNameversio
	 * @return map
	 */
	public static Map<String, String> parseAndWriteTestDataJsonFile(String inputFilePath, Map<String, String> fieldvalue,
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
					PropertyUtils.setProperty(jsonObj, AuthTestsUtil.getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
							Long.parseLong(value));
				} else if (map.getValue().contains("DOUBLE:")) {
					String value = map.getValue().replace("DOUBLE:", "");
					PropertyUtils.setProperty(jsonObj, AuthTestsUtil.getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
							Double.parseDouble(value));
				} else if (map.getValue().contains("BOOLEAN:")) {
					String value = map.getValue();
					if (value.contains("true"))
						PropertyUtils.setProperty(jsonObj, AuthTestsUtil.getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
								true);
					if (value.contains("false"))
						PropertyUtils.setProperty(jsonObj, AuthTestsUtil.getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
								false);
				} else
					PropertyUtils.setProperty(jsonObj, AuthTestsUtil.getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
							map.getValue());
			}
			mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			mapper.writeValue(new FileOutputStream(outputFilePath), jsonObj);
			String outputJson = new String(Files.readAllBytes(Paths.get(outputFilePath)), StandardCharsets.UTF_8);
			// Replacing the version in request
			outputJson = outputJson.replace("$version$", RunConfigUtil.objRunConfig.getAuthVersion());
			outputJson = outputJson.replaceAll("$version$", RunConfigUtil.objRunConfig.getAuthVersion());
			outputJson = outputJson.replace("$idrepoVersion$", RunConfigUtil.objRunConfig.getIdRepoVersion());
			outputJson = outputJson.replaceAll("$idrepoVersion$", RunConfigUtil.objRunConfig.getIdRepoVersion());
			if (outputJson.contains("$REMOVE$"))
				outputJson = removeObject(new JSONObject(outputJson));
			outputJson=JsonPrecondtion.toPrettyFormat(outputJson);
			FileUtil.writeFile(outputFilePath, outputJson);
			PRECON_LOGGER.info("Updated json file content: " + JsonPrecondtion.toPrettyFormat(outputJson.toString()));
			return fieldvalue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured in precondtion message: " + e.getMessage());
			Reporter.log("Exception Occured in precondtion message: " + e.getMessage());
			return fieldvalue;
		}
	}
	
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param auditMappingPath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWritePropertyFile(String auditMappingPath,Map<String, String> fieldvalue,
			String outputFilePath) {
		try {
			fieldvalue = getObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to add
			Map<String, String> auditTxnValue = new HashMap<String, String>();
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				String orgKey = AuthTestsUtil.getPropertyFromFilePath(auditMappingPath).get(entry.getKey()).toString();
				auditTxnValue.put(orgKey, entry.getValue());
			}
			Properties prop = new Properties();
			OutputStream output = new FileOutputStream(outputFilePath);
				for (Entry<String, String> entry : auditTxnValue.entrySet()) {
					prop.setProperty(entry.getKey(), entry.getValue());
				}
				prop.store(output, "UTF-8");
			return auditTxnValue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
	}
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param emailMappingPath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWriteEmailNotificationPropertyFile(String emailMappingPath,
			Map<String, String> fieldvalue, String outputFilePath) {
		try {
			fieldvalue = getObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to
																									// add
			Map<String, String> emailTemplatevalue = new HashMap<String, String>();
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				String key = entry.getKey().toString();
				if (key.matches("email.template.*")) {
					String[] templates = entry.getValue().split(Pattern.quote("|"));
					emailTemplatevalue.put(templates[0], templates[1]);
				} else if (entry.getKey().toString().contains("email.otp")) {
					emailTemplatevalue.put(entry.getKey(), entry.getValue());
				}
			}
			Properties prop = new Properties();
			for (Entry<String, String> entry : emailTemplatevalue.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF-8"), null);
			return emailTemplatevalue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Method update the property file and return map of property
	 * 
	 * @param fieldvalue
	 * @param outputFilePath
	 * @return map
	 */
	public static Map<String, String> parseAndWritePropertyFile(Map<String, String> fieldvalue, String outputFilePath) {
		try {
			fieldvalue = getObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);// New Code . Need to
																									// add
			Properties prop = new Properties();
			if (!new File(outputFilePath).exists())
				new File(outputFilePath).getParentFile().mkdirs();
			FileOutputStream output = new FileOutputStream(outputFilePath);
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
			output.close();
			output.flush();
			return fieldvalue;
		} catch (Exception e) {
			PRECON_LOGGER.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * This method is to remove objects where ever REMOVE keyword is provided in
	 * test data
	 * 
	 * @param object
	 * @return string
	 */
	public static String removeObject(JSONObject object) {
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				String finalarrayContent = "";
				for (int i = 0; i < array.length(); ++i) {
					if(!array.toString().contains("{") && !array.toString().contains("}"))
					{
						Set<String> arr = new HashSet<String>();
						for (int k = 0; k < array.length(); k++)
						{
							arr.add(array.getString(k));
						}
						finalarrayContent=removObjectFromArray(arr);
					}
					else
					{
					String arrayContent = removeObject(new JSONObject(array.get(i).toString()), finalarrayContent);
					if (!arrayContent.equals("{}"))
						finalarrayContent = finalarrayContent + "," + arrayContent;
					}
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
	
	private static String removObjectFromArray(Set<String> content) {
		String array = "[";
		for (String str : content) {
			if (!str.contains("$REMOVE$"))
				array = array + '"' + str + '"' + ",";
		}
		array = array.substring(0, array.length() - 1);
		array = array + "]";
		return array;
	}
	public static boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        // edited, to include @Arthur's comment
	        // e.g. in case JSONArray is valid as well...
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
	/**
	 * The method remove Object from Json array
	 * 
	 * @param object
	 * @param tempArrayContent
	 * @return string
	 */
	private static String removeObject(JSONObject object, String tempArrayContent) {
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
	 * The method return object of KeywordUtil
	 * 
	 * @param moduleName
	 * @return
	 */
	public static KeywordUtil getObject(String moduleName) {
		KeywordUtil objKeywordUtil = null;
		if (moduleName.equalsIgnoreCase("ida"))
			objKeywordUtil = new IdaKeywordUtil();
		else if (moduleName.equalsIgnoreCase("idrepo"))
			objKeywordUtil = new IdRepoKeywordUtil();
		return objKeywordUtil;
	}

}

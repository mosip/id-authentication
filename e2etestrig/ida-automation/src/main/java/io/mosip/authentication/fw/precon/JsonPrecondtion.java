package io.mosip.authentication.fw.precon;

import static io.mosip.authentication.fw.util.AuthTestsUtil.*; 
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.testng.Reporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.EncryptDecrptUtil;
import io.mosip.authentication.fw.util.ReportUtil;
 
/**
 * The class perform precondtion the json message such as read, write and update the json message
 * 
 * @author Vignesh
 *
 */
public class JsonPrecondtion{
	
	private static final Logger JSONPRECONDATION_LOGGER = Logger.getLogger(JsonPrecondtion.class);
	private static final String XMLROOT="idrepo";
	private static final String XMLPREFIX="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<";
	
	/**
	 * Update and write the json message according to the field inputs
	 * 
	 * @param inputFilePath - Input JSON file path
	 * @param fieldvalue - Map<Fieldname, inputData>
	 * @param outputFilePath - Ouput Json file path
	 * @return boolean - true or false
	 */
	public static boolean parseAndwriteJsonFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(
					new String(Files.readAllBytes(Paths.get(inputFilePath)), StandardCharsets.UTF_8), Object.class);
			for (Entry<String, String> map : fieldvalue.entrySet()) {
				if (map.getValue().contains("LONG:")) {
					String value = map.getValue().replace("LONG:", "");
					PropertyUtils.setProperty(jsonObj, getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
							Long.parseLong(value));
				} else if (map.getValue().contains("DOUBLE:")) {
					String value = map.getValue().replace("DOUBLE:", "");
					PropertyUtils.setProperty(jsonObj, getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
							Double.parseDouble(value));
				} else if (map.getValue().contains("BOOLEAN:")) {
					String value = map.getValue();
					if (value.contains("true"))
						PropertyUtils.setProperty(jsonObj,
								getPropertyFromFilePath(propFileName).getProperty(map.getKey()), true);
					if (value.contains("false"))
						PropertyUtils.setProperty(jsonObj,
								getPropertyFromFilePath(propFileName).getProperty(map.getKey()), false);
				} else
					PropertyUtils.setProperty(jsonObj, getPropertyFromFilePath(propFileName).getProperty(map.getKey()),
							map.getValue());
			}
			mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			mapper.writeValue(new FileOutputStream(outputFilePath), jsonObj);
			String outputJson = new String(Files.readAllBytes(Paths.get(outputFilePath)), StandardCharsets.UTF_8);
			FileUtil.writeFile(outputFilePath, outputJson);
			JSONPRECONDATION_LOGGER.info("Updated json file content: " + toPrettyFormat(outputJson.toString()));
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(outputJson.toString()) + "</pre>");
			return true;
		} catch (Exception e) {
			JSONPRECONDATION_LOGGER.error("Exception Occured in message precondtion: " + e.getMessage());
			Reporter.log("Exception Occured in message precondtion: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * The method get the value for the field hierarchy from json file
	 * 
	 * @param inputFilePath - Json file path
	 * @param mappingFileName - mapping file path of json file
	 * @param mappingFieldName - field name to get value from json
	 * @return string - value or text for the field name from json
	 */
	public static String getValueFromJson(String inputFilePath, String mappingFileName, String mappingFieldName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(new String(Files.readAllBytes(Paths.get(inputFilePath)), "UTF-8"),
					Object.class);
			return PropertyUtils
					.getProperty(jsonObj, getPropertyFromFilePath(mappingFileName).getProperty(mappingFieldName))
					.toString();
		} catch (Exception exception) {
			JSONPRECONDATION_LOGGER
					.error("Exception Occured in retrieving the value from json file: " + exception.getMessage());
			return exception.toString();
		}
	}
	
	/**
	 * The method get the value for the field hierarchy from json message or content
	 * 
	 * @param jsonContent - Json Message or content
	 * @param fieldMapper - Field Hierarchy (Example: response.status)
	 * @return string - value for the field hierarchy from json content or message
	 */
	public static String getValueFromJson(String jsonContent, String fieldMapper) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonContent, Object.class);
			return PropertyUtils.getProperty(jsonObj, fieldMapper).toString();
		} catch (Exception expection) {
			JSONPRECONDATION_LOGGER
					.error("Exception Occured in retrieving the value from json file: " + expection.getMessage());
			return expection.toString();
		}
	}
	
	/**
	 * The method get the value from the json using mapping and field name
	 * 
	 * @param jsonContent - Json content or message
	 * @param mappingFilePath - Json mapping file path
	 * @param fieldName - Json FieldName
	 * @return string - value from the json for the field name
	 */
	public static String getValueFromJsonUsingMapping(String jsonContent, String mappingFilePath, String fieldName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonContent, Object.class);
			return PropertyUtils.getProperty(jsonObj,
					AuthTestsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(fieldName)).toString();
		} catch (Exception exp) {
			JSONPRECONDATION_LOGGER
					.error("Exception Occured in retrieving the value from json file: " + exp.getMessage());
			return exp.toString();
		}
	}
	
	/**
	 * The method get the value for all the field name
	 * 
	 * @param jsonFilePath - Json file path
	 * @param map          - field name as key and field hierarchy as value
	 * @return map - field hierarcht as key and its json field value or text as
	 *         value
	 */
	public static Map<String, String> getJsonFieldValue(String jsonFilePath, Map<String, String> map) {
		Map<String, String> returnMap = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			returnMap = new HashMap<String, String>();
			Object jsonObj = mapper.readValue(new String(Files.readAllBytes(Paths.get(jsonFilePath)), "UTF-8"),
					Object.class);
			for (Entry<String, String> entry : map.entrySet()) {
				if (PropertyUtils.getProperty(jsonObj, entry.getValue()) != null)
					returnMap.put(entry.getValue(), PropertyUtils.getProperty(jsonObj, entry.getValue()).toString());
				else
					returnMap.put(entry.getValue(), "null");
			}
		} catch (Exception exp) {
			JSONPRECONDATION_LOGGER.error("Exception occured in getting the value from json: " + exp.toString());
		}
		return returnMap;
	}
	

	private List<String> pathList;
    private String json;
    public JsonPrecondtion() {}
    public JsonPrecondtion(String json) {
        this.json = json;
        this.pathList = new ArrayList<String>();
        setJsonPaths(this.json);
    }

    /**
     * The method return list of path
     * 
     * @param filePath
     * @return Map, Key as JsonFieldName and Value as JsonFieldPath
     */
    public Map<String,String> getPathList(String filePath) {
    	return modifyList();
    }

    /**
     * Set Json Path from Json Content
     * 
     * @param json
     */
    private void setJsonPaths(String json) {
        this.pathList = new ArrayList<String>();
        JSONObject object = new JSONObject(json);
        String jsonPath = "$";
        if(json != JSONObject.NULL) {
            readObject(object, jsonPath);
        }   
    }
    
    /**
     * The method to read json object
     * 
     * @param Json object
     * @param jsonPath
     */
	private void readObject(JSONObject object, String jsonPath) {
		Iterator<String> keysItr = object.keys();
		String parentPath = jsonPath;
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);
			jsonPath = parentPath + "." + key;
			if (value instanceof JSONArray) {
				readArray((JSONArray) value, jsonPath);
			} else if (value instanceof JSONObject) {
				readObject((JSONObject) value, jsonPath);
			} else { // is a value
				this.pathList.add(jsonPath);
			}
		}
	}

	/**
	 * The method read Json array
	 * 
	 * @param Json array
	 * @param jsonPath
	 */
    private void readArray(JSONArray array, String jsonPath) {   
        String parentPath = jsonPath;
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);    
            String tempPath=parentPath.substring(parentPath.lastIndexOf(".")+1, parentPath.length());
            String tempparentPath=parentPath.substring(0,parentPath.lastIndexOf(".")+1);
            jsonPath=tempparentPath+"("+tempPath+")"+"["+i+"]";
            if(value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            } else if(value instanceof JSONObject) {                
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                this.pathList.add(jsonPath);
            }       
        }
    }
    
    /**
     * The method to modify list of Json Field name
     * 
     * @return map, key as json fieldName and value as json object path
     */
	private Map<String, String> modifyList() {
		Map<String, String> mappingDic = new HashMap<String, String>();
		for (String str : this.pathList) {
			String value = str.replace("$.", "");
			String key = "";
			if (value.contains(".")) {
				String[] list = new String[20];
				list = value.split(Pattern.quote("."));
				if (list[list.length - 2].contains("[")) {
					key = key
							+ list[list.length - 1].replace("(", "").replace(")", "").replace("[", "").replace("]", "");
					key = key
							+ list[list.length - 2].replace("(", "").replace(")", "").replace("[", "").replace("]", "");
				} else
					key = list[list.length - 1];
			} else
				key = value;
			mappingDic.put(key, value);
		}
		return mappingDic;
	}
	
	/**
	 * Get the JSON data formated in HTML
	 */ 
	public String getHtmlData( String strJsonData ) {
	    return jsonToHtml( new JSONObject( strJsonData ) );
	}

	/**
	 * convert json Data to structured Html text
	 * 
	 * @param json
	 * @return string
	 */
	private String jsonToHtml(Object obj) {
		StringBuilder html = new StringBuilder();
		try {
			if (obj instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) obj;
				String[] keys = JSONObject.getNames(jsonObject);
				html.append("<div class=\"json_object\">");
				if (keys.length > 0) {
					for (String key : keys) {
						html.append("<div><span class=\"json_key\">").append(key).append("</span> : ");
						Object val = jsonObject.get(key);
						html.append(jsonToHtml(val));
						html.append("</div>");
					}
				}
				html.append("</div>");
			} else if (obj instanceof JSONArray) {
				JSONArray array = (JSONArray) obj;
				for (int i = 0; i < array.length(); i++) {
					html.append(jsonToHtml(array.get(i)));
				}
			} else {
				html.append(obj);
			}
		} catch (JSONException e) {
			return e.getLocalizedMessage();
		}
		return html.toString();
	}
	
	/**
	 * The method to display json in structure format
	 * 
	 * @param jsonString
	 * @return String
	 */
	public static String toPrettyFormat(String jsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonString);
		return gson.toJson(je);
	}
	
	/**
	 * The method to convert json content to xml from json content
	 * 
	 * @param Json content
	 * @return String
	 */
	public static String convertJsonContentToXml(String content) {
		return XMLPREFIX + XMLROOT + ">" + XML.toString(new JSONObject(content)) + "</" + XMLROOT + ">";
	}
	
	public static String parseAndReturnJsonContent(String inputContent,String inputValueToSet, String mapping) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(inputContent, Object.class);
				if (inputValueToSet.contains("LONG:")) {
					String value = inputValueToSet.replace("LONG:", "");
					PropertyUtils.setProperty(jsonObj, mapping,
							Long.parseLong(value));
				} else if (inputValueToSet.contains("DOUBLE:")) {
					String value = inputValueToSet.replace("DOUBLE:", "");
					PropertyUtils.setProperty(jsonObj, mapping,
							Double.parseDouble(value));
				} else if (inputValueToSet.contains("BOOLEAN:")) {
					String value = inputValueToSet;
					if (value.contains("true"))
						PropertyUtils.setProperty(jsonObj,
								mapping, true);
					if (value.contains("false"))
						PropertyUtils.setProperty(jsonObj,
								mapping, false);
				} else
					PropertyUtils.setProperty(jsonObj, mapping,
							inputValueToSet);
		    return mapper.writeValueAsString(jsonObj);
			//return jsonObj.toString();
		} catch (Exception e) {
			JSONPRECONDATION_LOGGER.error("Exception Occured in message precondtion: " + e.getMessage());
			Reporter.log("Exception Occured in message precondtion: " + e.getMessage());
			return e.getMessage();
		}
	}
	
	public static String getJsonInOrder(String jsonContent) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonContent, Object.class);
			mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			return mapper.writeValueAsString(jsonObj);
		} catch (Exception e) {
			JSONPRECONDATION_LOGGER.error("Exception " + e.getMessage());
			return e.getMessage();
		}
	}
	
	public static Map<String, Object> jsonToMap(JSONObject o) {
		Map<String, Object> map = new LinkedHashMap<>();
		// https://github.com/jwtk/jjwt/issues/380: use .keys() and *not* .keySet() for
		// Android compatibility:
		Iterator<String> iterator = o.keys();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = o.get(key);
			value = convertIfNecessary(value);
			map.put(key, value);
		}
		return map;
	}
	
	private static Object convertIfNecessary(Object v) {
        Object value = v;
        if (JSONObject.NULL.equals(value)) {
            value = null;
        } else if (value instanceof JSONArray) {
            value = jsonArrayToList((JSONArray) value);
        } else if (value instanceof JSONObject) {
            value = jsonToMap((JSONObject) value);
        }
        return value;
    }
	
	private static List<Object> jsonArrayToList(JSONArray a) {
        int length = a.length();
        List<Object> list = new ArrayList<>(length);
        // https://github.com/jwtk/jjwt/issues/380: use a.get(i) and *not* a.toList() for Android compatibility:
        for( int i = 0; i < length; i++) {
            Object value = a.get(i);
            value = convertIfNecessary(value);
            list.add(value);
        }
        return list;
    }
	
	/**
	 * The method get the value for the field hierarchy from json message or content
	 * 
	 * @param jsonContent - Json Message or content
	 * @param fieldMapper - Field Hierarchy (Example: response.status)
	 * @return string - value for the field hierarchy from json content or message
	 */
	public static String getJsonValueFromJson(String jsonContent, String fieldMapper) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonContent, Object.class);
			return mapper.writeValueAsString(PropertyUtils.getProperty(jsonObj, fieldMapper));
		} catch (Exception expection) {
			JSONPRECONDATION_LOGGER
					.error("Exception Occured in retrieving the value from json file: " + expection.getMessage());
			return expection.toString();
		}
	}
	
}

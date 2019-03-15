package io.mosip.authentication.fw.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
 
/**
 * Perform precondtion the json message such as read, write and update the json message
 * 
 * @author Vignesh
 *
 */
public class JsonPrecondtion {
	
	private static Logger logger = Logger.getLogger(JsonPrecondtion.class);
	private ReportUtil objReportUtil = new ReportUtil();
	private FileUtil objFileUtil = new FileUtil();
	
	/**
	 * Update and write the json message according to the field inputs
	 * 
	 * @param inputFilePath , Input JSON file path
	 * @param fieldvalue, Map<Fieldname, inputData>
	 * @param outputFilePath
	 * @return true or false - status
	 */
	public boolean parseAndwriteJsonFile(String inputFilePath, Map<String, String> fieldvalue, String outputFilePath,
			String propFileName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(new String(Files.readAllBytes(Paths.get(inputFilePath)),StandardCharsets.UTF_8), Object.class);
			for (Entry<String, String> map : fieldvalue.entrySet()) {
				if (map.getValue().contains("LONG:"))
				{
					String value=map.getValue().replace("LONG:", "");
					PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
							Long.parseLong(value));
				}
				else if (map.getValue().contains("DOUBLE:")) {
					String value = map.getValue().replace("DOUBLE:", "");
					PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
							Double.parseDouble(value));
				}else if (map.getValue().contains("BOOLEAN:")) {
					String value = map.getValue();
					if (value.contains("true"))
						PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
								true);
					if (value.contains("false"))
						PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
								false);
				}
				else
					PropertyUtils.setProperty(jsonObj, getFieldHierarchy(propFileName).getProperty(map.getKey()),
							map.getValue());
			}
			mapper.writeValue(new FileOutputStream(outputFilePath), jsonObj);
			String outputJson = new String(Files.readAllBytes(Paths.get(outputFilePath)),StandardCharsets.UTF_8);
			objFileUtil.writeFile(outputFilePath, outputJson);
			logger.info("Updated json file location: " + outputJson.toString());
			logger.info("Updated json file content: " +  toPrettyFormat(outputJson.toString()));
			Reporter.log("<pre>" + objReportUtil.getTextAreaJsonMsgHtml(outputJson.toString())+"</pre>");
			return true;
		} catch (Exception e) {
			logger.error("Exception Occured: " + e.getMessage());
			Reporter.log("Exception Occured: " + e.getMessage());
			return false;
		}
	}
	
	public String getValueFromJson(String inputFilePath,String mappingFileName,String mappingFieldName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(new String(Files.readAllBytes(Paths.get(inputFilePath)),"UTF-8"), Object.class);
			return PropertyUtils.getProperty(jsonObj, getFieldHierarchy(mappingFileName).getProperty(mappingFieldName)).toString();
		} catch (Exception e) {
			Reporter.log("Exception Occured: " + e.getMessage());
			return e.toString();
		}
	}
	
	public String getValueFromJson(String jsonContent,String fieldMapper) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonContent, Object.class);
			return PropertyUtils.getProperty(jsonObj,fieldMapper).toString();
		} catch (Exception e) {
			Reporter.log("Exception Occured: " + e.getMessage());
			return e.toString();
		}
	}
	
	public String getValueFromJsonUsingMapping(String jsonContent,String mappingFilePath,String fieldName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonContent, Object.class);
			return PropertyUtils.getProperty(jsonObj,getFieldHierarchy(mappingFilePath).getProperty(fieldName)).toString();
		} catch (Exception e) {
			Reporter.log("Exception Occured: " + e.getMessage());
			return e.toString();
		}
	}
	
	public Map<String, String> getJsonFieldValue(String jsonFilePath, Map<String, String> map) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> returnMap = new HashMap<String, String>();
			Object jsonObj = mapper.readValue(new String(Files.readAllBytes(Paths.get(jsonFilePath)),"UTF-8"), Object.class);
			for (Entry<String, String> entry : map.entrySet()) {
				if (PropertyUtils.getProperty(jsonObj, entry.getValue()) != null)
					returnMap.put(entry.getValue(), PropertyUtils.getProperty(jsonObj, entry.getValue()).toString());
				else
					returnMap.put(entry.getValue(), "null");
			}
			return returnMap;
		} catch (Exception e) {
			logger.error("Exception: " + e.toString());
			return null;
		}
	}
	
	
	public Properties getFieldHierarchy(String propFileName) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propFileName);
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.info(e);
			return null;
		}
	}
	

	private List<String> pathList;
    private String json;
    public JsonPrecondtion() {}
    public JsonPrecondtion(String json) {
        this.json = json;
        this.pathList = new ArrayList<String>();
        setJsonPaths(json);
    }

    public Map<String,String> getPathList(String filePath) {
    	return modifyList();
    }

    private void setJsonPaths(String json) {
        this.pathList = new ArrayList<String>();
        JSONObject object = new JSONObject(json);
        String jsonPath = "$";
        if(json != JSONObject.NULL) {
            readObject(object, jsonPath);
        }   
    }

    
    
    private void readObject(JSONObject object, String jsonPath) {
        Iterator<String> keysItr = object.keys();
        String parentPath = jsonPath;
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            jsonPath = parentPath + "." + key;
            if(value instanceof JSONArray) {            
                readArray((JSONArray) value, jsonPath);
            }
            else if(value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                this.pathList.add(jsonPath);    
            }          
        }  
    }

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
						// recursive call
						html.append(jsonToHtml(val));
						// close the div
						html.append("</div>");
					}
				}
				html.append("</div>");
			} else if (obj instanceof JSONArray) {
				JSONArray array = (JSONArray) obj;
				for (int i = 0; i < array.length(); i++) {
					// recursive call
					html.append(jsonToHtml(array.get(i)));
				}
			} else {
				// print the value
				html.append(obj);
			}
		} catch (JSONException e) {
			return e.getLocalizedMessage();
		}
		return html.toString();
	}
	
	public String toPrettyFormat(String jsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonString);
		return gson.toJson(je);
	}
	
}

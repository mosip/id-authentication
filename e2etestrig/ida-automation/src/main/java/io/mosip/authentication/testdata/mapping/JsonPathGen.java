package io.mosip.authentication.testdata.mapping;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is to generate jsonpath/object mapper from json file in a
 * properties file
 * 
 * @author Vignesh
 *
 */
public class JsonPathGen {

	private static final Logger JSONPATHGEN_LOGGER = Logger.getLogger(JsonPathGen.class);
	private List<String> pathList;
	private String json;

	/**
	 * The method set json object mapper path for the input json content
	 * 
	 * @param json
	 */
	public JsonPathGen(String json) {
		this.json = json;
		this.pathList = new ArrayList<String>();
		setJsonPaths(this.json);
	}

	/**
	 * Method get all json path list
	 * 
	 * @param filePath
	 * @return map
	 */
	public Map<String, String> getPathList(String filePath) {
		return modifyList();
	}

	/**
	 * The method set json path for input json
	 * 
	 * @param json
	 */
	private void setJsonPaths(String json) {
		this.pathList = new ArrayList<String>();
		JSONObject object = new JSONObject(json);
		String jsonPath = "$";
		if (json != JSONObject.NULL) {
			readObject(object, jsonPath);
		}
	}

	/**
	 * The method read object for json input
	 * 
	 * @param object
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
	 * The method read json array
	 * 
	 * @param array
	 * @param jsonPath
	 */
	private void readArray(JSONArray array, String jsonPath) {
		String parentPath = jsonPath;
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			String tempPath = parentPath.substring(parentPath.lastIndexOf(".") + 1, parentPath.length());
			String tempparentPath = parentPath.substring(0, parentPath.lastIndexOf(".") + 1);
			jsonPath = tempparentPath + "(" + tempPath + ")" + "[" + i + "]";
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
	 * The method modify list of Json path
	 * 
	 * @return map
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
	 * Method generate json mapping in property file
	 * 
	 * @param filePath
	 */
	public void generateJsonMappingDic(String filePath) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(filePath);
			for (Entry<String, String> entry : getPathList(filePath).entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
		} catch (Exception e) {
			JSONPATHGEN_LOGGER.error(e.getMessage());
		}
	}

}

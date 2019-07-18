package io.mosip.authentication.fw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.dto.RidDto;
import io.mosip.authentication.fw.dto.UinDto;

public class RIDUtil extends RunConfigUtil{

	private static final Logger ridLogger = Logger.getLogger(RIDUtil.class);
	/**
	 * The method get UIN number using keyword from property file
	 * 
	 * @param keyword
	 * @return UIN number
	 */
	public static String getRidNumber(String keyword) {
		if (keyword.equals("$RID$")) {
			String key = getRandomRIDKey();
			return key;
		} else if (keyword.contains("WITH")){
			keyword=keyword.replace("$", "");
			String keys[]=keyword.split(Pattern.quote(":"));
			return getRidForUin(UINUtil.getUINKey(keys[2]));		
		}
		return "NoLoadedRIDFound";
	}
	
	/**
	 * The method return random UIN from property file
	 * 
	 * @return Random UIN
	 */
	public static String getRandomRIDKey() {
		getRidPropertyValue(getRidPropertyPath());
		UINUtil.getUinPropertyValue(getUinPropertyPath());
		Object[] randomKeys = RidDto.getRidData().keySet().toArray();
		int count = 1;
		while (count > 0) {
			Object key = randomKeys[new Random().nextInt(randomKeys.length)];
			if (UinDto.getUinData().get(RidDto.getRidData().get(key)).contains("valid")) {
				return key.toString();
			}
			count++;
			if(count>50)
				return "NoLoadedRIDFound";
		}
		return "NoLoadedRIDFound";
	}
	/**
	 * The method get UIN property value from property file
	 * 
	 * @param path
	 */
	protected static void getRidPropertyValue(String path) {
		Properties prop = AuthTestsUtil.getPropertyFromRelativeFilePath(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		RidDto.setRidData(map);
	}
	
	public static String getRidForUin(String uin) {
		getRidPropertyValue(getRidPropertyPath());
		for (Entry<String, String> map : RidDto.getRidData().entrySet()) {
			if (map.getValue().equals(uin))
				return map.getKey();
		}
		return "NoLoadedRidFound";
	}
	
}

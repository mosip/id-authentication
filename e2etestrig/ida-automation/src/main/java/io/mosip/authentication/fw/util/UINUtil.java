package io.mosip.authentication.fw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;

import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.dto.UinStaticPinDto;
import io.mosip.authentication.fw.dto.VidDto;

public class UINUtil extends RunConfigUtil{
	
	/**
	 * The method return random UIN from property file
	 * 
	 * @return Random UIN
	 */
	public static String getRandomUINKey() {
		getUinPropertyValue(getUinPropertyPath());
		int count = 1;
		while (count > 0) {
			Object[] randomKeys = UinDto.getUinData().keySet().toArray();
			Object key = randomKeys[new Random().nextInt(randomKeys.length)];
			if (UinDto.getUinData().get(key).toString().contains("valid")) {
				return key.toString();
			}
			count++;
		}
		return "NoUINFound";
	}
	/**
	 * The method get UIN number using keyword from property file
	 * 
	 * @param keyword
	 * @return UIN number
	 */
	public static String getUinNumber(String keyword) {
		if (keyword.contains("EVEN")) {
			int count = 1;
			while (count > 0) {
				String key = getRandomUINKey();
				String lastNumberAsString = key.substring(key.length() - 1, key.length());
				int lastNum = Integer.parseInt(lastNumberAsString);
				if (lastNum % 2 == 0)
					return key;
				else
					count++;
			}
		} else if (keyword.contains("ODD")) {
			int count = 1;
			while (count > 0) {
				String key = getRandomUINKey();
				String lastNumberAsString = key.substring(key.length() - 1, key.length());
				int lastNum = Integer.parseInt(lastNumberAsString);
				if (lastNum % 2 != 0)
					return key;
				else
					count++;
			}
		} else if (keyword.equals("$UIN$")) {
			String key = getRandomUINKey();
			return key;
		} else {
			keyword = keyword.replace("$", "");
			String keys[] = keyword.split(":");
			String keywrdToFind = keys[2];
			return getUINKey(keywrdToFind);
		}
		return "NoLoadedUINFound";
	}
	/**
	 * The method get static pin for UIN
	 * 
	 * @return static pin
	 */
	public static String getRandomStaticPinUINKey() {
		getStaticPinUinPropertyValue(getStaticPinUinPropertyPath());
		Object[] randomKeys = UinStaticPinDto.getUinStaticPin().keySet().toArray();
		Object key = randomKeys[new Random().nextInt(randomKeys.length)];
		return key.toString();
	}
	/**
	 * The method get UIN using keyword from property file
	 * 
	 * @param keywordToFind
	 * @return UIN
	 */
	static String getUINKey(String keywordToFind) {
		getUinPropertyValue(getUinPropertyPath());
		for (Entry<String, String> entry : UinDto.getUinData().entrySet()) {
			if (entry.getValue().contains(keywordToFind))
				return entry.getKey();
		}
		return "NoLoadedUINFound";
	}
	
	/**
	 * The method get UIN property value from property file
	 * 
	 * @param path
	 */
	protected static void getUinPropertyValue(String path) {
		Properties prop = AuthTestsUtil.getPropertyFromRelativeFilePath(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		UinDto.setUinData(map);
	}
	/**
	 * The method get static pin for UIN property value
	 * 
	 * @param path
	 */
	public static void getStaticPinUinPropertyValue(String path) {
		Properties prop = AuthTestsUtil.getPropertyFromRelativeFilePath(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		UinStaticPinDto.setUinStaticPin(map);
	}
	
	/**
	 * The method get VID for vidKey
	 * 
	 * @param uin
	 * @return VID
	 */
	public static String getUinForVid(String vid) {
		VIDUtil.getVidPropertyValue(getVidPropertyPath());
		for (Entry<String, String> entry : VidDto.getVid().entrySet()) {
			if (entry.getValue().contains(vid))
				return entry.getKey();
		}
		return "NoLoadedVIDFound";
	}

}

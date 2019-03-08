package io.mosip.authentication.fw.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.dto.TokenIdDto;
import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.dto.UinStaticPinDto;
import io.mosip.authentication.fw.dto.VidDto;
import io.mosip.authentication.fw.dto.VidStaticPinDto;

public class UinVidNumberUtil {
	private static Logger logger = Logger.getLogger(UinVidNumberUtil.class);
	
	public String getUinPropertyPath() {
		return "ida/" + RunConfig.getTestDataFolderName() + "/RunConfig/uin.properties";
	}

	public String getStaticPinUinPropertyPath() {
		return "ida/" + RunConfig.getTestDataFolderName() + "/RunConfig/uinStaticPin.properties";
	}

	public String getVidPropertyPath() {
		return "ida/" + RunConfig.getTestDataFolderName() + "/RunConfig/vid.properties";
	}

	public String getStaticPinVidPropertyPath() {
		return "ida/" + RunConfig.getTestDataFolderName() + "/RunConfig/vidStaticPin.properties";
	}
	
	public String getTokenIdPropertyPath() {
		return "ida/" + RunConfig.getTestDataFolderName() + "/RunConfig/tokenId.properties";
	}
	
	public String getTokenId(String uin, String tspid) {
		getTokenIdPropertyValue(getTokenIdPropertyPath());
		if (TokenIdDto.getTokenId().containsKey(uin + "." + tspid))
			return TokenIdDto.getTokenId().get(uin + "." + tspid);
		else
			return "TOKENID:"+uin + "." + tspid;
	}
	
	public String getRandomUINKey() {
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
	
	public String getUinNumber(String keyword) {
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
		}
		else if (keyword.contains("ODD")) {
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
		}
		else if (keyword.equals("$UIN$")) {
			String key = getRandomUINKey();
			return key;
		}
		else
		{
			keyword=keyword.replace("$", "");
			String keys[] = keyword.split(":");
			String keywrdToFind=keys[2];
			return getUINKey(keywrdToFind);
		}
		return "NoLoadedUINFound";
	}
	
	public String getRandomStaticPinUINKey() {
		getStaticPinUinPropertyValue(getStaticPinUinPropertyPath());
		Object[] randomKeys = UinStaticPinDto.getUinStaticPin().keySet().toArray();
		Object key = randomKeys[new Random().nextInt(randomKeys.length)];
		return key.toString();
	}
	
	public String getRandomStaticPinVIDKey() {
		getStaticPinVidPropertyValue(getStaticPinVidPropertyPath());
		Object[] randomKeys = VidStaticPinDto.getVidStaticPin().keySet().toArray();
		Object key = randomKeys[new Random().nextInt(randomKeys.length)];
		return key.toString();
	}
	
	public String getRandomVidKey() {
		getVidPropertyValue(getVidPropertyPath());
		Object[] randomKeys = VidDto.getVid().keySet().toArray();
		Object key = randomKeys[new Random().nextInt(randomKeys.length)];
		return key.toString();
	}
	
	private String getUINKey(String keywordToFind) {
		getUinPropertyValue(getUinPropertyPath());
		for(Entry<String,String> entry: UinDto.getUinData().entrySet())
		{
			if(entry.getValue().contains(keywordToFind))
				return entry.getKey();
		}
		return "NoLoadedUINFound";
	}
	
	protected void getUinPropertyValue(String path) {
		Properties prop = getPropertyData(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		UinDto.setUinData(map);
	}
	
	public void getStaticPinUinPropertyValue(String path) {
		Properties prop = getPropertyData(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		UinStaticPinDto.setUinStaticPin(map);
	}
	
	public void getStaticPinVidPropertyValue(String path) {
		Properties prop = getPropertyData(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		VidStaticPinDto.setVidStaticPin(map);
	}
	
	protected void getVidPropertyValue(String path) {
		Properties prop = getPropertyData(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		VidDto.setVid(map);
	}
	
	public void getTokenIdPropertyValue(String path) {
		Properties prop = getPropertyData(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		TokenIdDto.setTokenId(map);
	}
	
	private Properties getPropertyData(String path) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(
					RunConfig.getUserDirectory() + RunConfig.getSrcPath() + path);
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.error("Exception occured in fetching the uin number from property file " + e.getMessage());
			return prop;
		}
	}

}

package io.mosip.authentication.fw.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import io.mosip.authentication.fw.dto.TokenIdDto;
import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.dto.UinStaticPinDto;
import io.mosip.authentication.fw.dto.VidDto;
import io.mosip.authentication.fw.dto.VidStaticPinDto;
import io.mosip.authentication.idRepository.fw.util.IdRepoRunConfig;
import io.mosip.authentication.testdata.keywords.KeywordUtil;

/**
 * The class perform picking up UIN,VID,TokenID,PartnerID,LicenseKey,StaticPin
 * 
 * @author Vignesh
 *
 */
public class RunConfigUtil {
	
	/**
	 * The method get UIN property file path
	 * 
	 * @return string, property file path
	 */
	public static String getUinPropertyPath() {
		return RunConfigUtil.objRunConfig.getModuleFolderName()+"/" + RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/uin.properties";
	}
	/**
	 * The method get static pin UIN property path
	 * 
	 * @return string, property file path
	 */
	public static String getStaticPinUinPropertyPath() {
		return RunConfigUtil.objRunConfig.getModuleFolderName()+"/" + RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/uinStaticPin.properties";
	}
	/**
	 * The method return VID property file path
	 * 
	 * @return string, property file path
	 */
	public static String getVidPropertyPath() {
		return RunConfigUtil.objRunConfig.getModuleFolderName()+"/" + RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/vid.properties";
	}
	/**
	 * The method get static pin VID property file path
	 * 
	 * @return string, property file path
	 */
	public static String getStaticPinVidPropertyPath() {
		return RunConfigUtil.objRunConfig.getModuleFolderName()+"/" + RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/vidStaticPin.properties";
	}
	/**
	 * The method get tokenId property file path
	 * 
	 * @return string, property file path
	 */
	public static String getTokenIdPropertyPath() {
		return RunConfigUtil.objRunConfig.getModuleFolderName()+"/" + RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/static-tokenId.properties";
	}
	/**
	 * The method get partnerID and Misp License key value property file path
	 * 
	 * @return string, property file path
	 */
	public static String getPartnerIDMispLKPropertyPath() {
		return RunConfigUtil.objRunConfig.getModuleFolderName()+"/" + RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/parter-license-id.properties";
	}
	/**
	 * The method get partnerID and License key value for the key
	 * 
	 * @param key
	 * @return string, value of partner ID and License key
	 */
	public static String getPartnerIDMispLKValue(String key) {
		return AuthTestsUtil.getPropertyFromRelativeFilePath(getPartnerIDMispLKPropertyPath()).get(key).toString();
	}
	/**
	 * The method get token ID for UIN and PartnerID
	 * 
	 * @param uin
	 * @param partnerID
	 * @return tokenID
	 */
	public static String getTokenId(String uin, String partnerID) {		
		getTokenIdPropertyValue(getTokenIdPropertyPath());
		if (TokenIdDto.getTokenId().containsKey(uin + "." + partnerID))
			return TokenIdDto.getTokenId().get(uin + "." + partnerID);
		else
			return "TOKENID:"+uin + "." + partnerID;
	}
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
	 * The method get static pin for VID
	 * 
	 * @return static pin
	 */
	public static String getRandomStaticPinVIDKey() {
		getStaticPinVidPropertyValue(getStaticPinVidPropertyPath());
		Object[] randomKeys = VidStaticPinDto.getVidStaticPin().keySet().toArray();
		Object key = randomKeys[new Random().nextInt(randomKeys.length)];
		return key.toString();
	}
	/**
	 * The method get random VID from property file
	 * 
	 * @return VID number
	 */
	public static String getRandomVidKey() {
		getVidPropertyValue(getVidPropertyPath());
		Object[] randomKeys = VidDto.getVid().values().toArray();
		for (int i = 0; i < randomKeys.length; i++) {
			String uin = getRandomUINKey();
			if (VidDto.getVid().get(uin).contains(".ACTIVE") && VidDto.getVid().get(uin).contains(".Perpetual")) {
				return VidDto.getVid().get(uin).toString().split(Pattern.quote("."))[0];
			}
		}
		return "NoVIDLoaded";
	}
	/**
	 * The method get VID for UIN
	 * 
	 * @param uin
	 * @return VID
	 */
	public static String getVidKey(String uin) {
		getVidPropertyValue(getVidPropertyPath());
		for (Entry<String, String> entry : VidDto.getVid().entrySet()) {
			if (entry.getKey().contains(uin))
				return entry.getValue().split(Pattern.quote("."))[0];
		}
		return "NoLoadedVIDFound";
	}
	/**
	 * The method get UIN using keyword from property file
	 * 
	 * @param keywordToFind
	 * @return UIN
	 */
	private static String getUINKey(String keywordToFind) {
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
	 * The method get static pin VID property value
	 * 
	 * @param path
	 */
	public static void getStaticPinVidPropertyValue(String path) {
		Properties prop = AuthTestsUtil.getPropertyFromRelativeFilePath(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		VidStaticPinDto.setVidStaticPin(map);
	}
	/**
	 * The method get Vid property value 
	 * 
	 * @param path
	 */
	protected static void getVidPropertyValue(String path) {
		Properties prop = AuthTestsUtil.getPropertyFromRelativeFilePath(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		VidDto.setVid(map);
	}
	/**
	 * The methof get tokenID property value
	 * 
	 * @param path
	 */
	public static void getTokenIdPropertyValue(String path) {
		Properties prop = AuthTestsUtil.getPropertyFromRelativeFilePath(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		TokenIdDto.setTokenId(map);
	}
	
	public static RunConfig objRunConfig;

	/**
	 * The method get object of runtime module config
	 * 
	 * @param moduleObject
	 */
	public static void getRunConfigObject(String module) {
		if (module.equals("ida"))
			objRunConfig = new IdaRunConfig();
		else if (module.equals("idrepo"))
			objRunConfig = new IdRepoRunConfig();
		
	}
	
	/**
	 * The method get environment
	 * 
	 * @return environment such as qa or int or dev or dev-int
	 */
	public static String getRunEvironment() {
		return System.getProperty("env.user");
	}
	
	/**
	 * The method get random VID from property file for Temporary and Perpetual type
	 * 
	 * @param testCaseName
	 * @return VID number
	 */
	public static String getVidKeyForVIDUpdate(String testCaseName) {
		getVidPropertyValue(getVidPropertyPath());
		int count = 1;
		while (count > 0) {
			Object[] randomKeys = VidDto.getVid().values().toArray();
			Object key = randomKeys[new Random().nextInt(randomKeys.length)];
			if (testCaseName.contains("Temporary") && key.toString().contains("Temporary") && key.toString().contains(".ACTIVE")) {
				count++;
				return key.toString();
			} else if (testCaseName.contains("Perpetual") && key.toString().contains("Perpetual") && key.toString().contains(".ACTIVE")) {
				count++;
				getUinPropertyValue(getUinPropertyPath());
				if(UinDto.getUinData().get(getUinForVid(key.toString().split(Pattern.quote("."))[0])).contains("valid"))
					return key.toString();
			}else if (testCaseName.contains("Deactivated") && key.toString().contains("Perpetual") && key.toString().contains(".ACTIVE")) {
				count++;
				return key.toString();
			}
		}
		return "NoVIDFound";
	}
	
	/**
	 * Get test type of execution such as smoke, regression or funtional etc
	 * 
	 * @return testLevel or testType
	 */
	public static String getTestLevel() {
		return System.getProperty("env.testLevel");
	}
	
	/**
	 * The method get VID for vidKey
	 * 
	 * @param uin
	 * @return VID
	 */
	public static String getVidForvidkey(String vidKeyword) {
		getVidPropertyValue(getVidPropertyPath());
		for (Entry<String, String> entry : VidDto.getVid().entrySet()) {
			if (entry.getValue().contains(vidKeyword))
				return entry.getValue().split(Pattern.quote("."))[0];
		}
		return "NoLoadedVIDFound";
	}
	
	/**
	 * The method get VID for vidKey
	 * 
	 * @param uin
	 * @return VID
	 */
	public static String getUinForVid(String vid) {
		getVidPropertyValue(getVidPropertyPath());
		for (Entry<String, String> entry : VidDto.getVid().entrySet()) {
			if (entry.getValue().contains(vid))
				return entry.getKey();
		}
		return "NoLoadedVIDFound";
	}
	
	public static String getLinuxMavenEnvVariableKey() {
		return AuthTestsUtil.getPropertyFromFilePath(new File("./src/test/resources/ida/TestData/RunConfig/envRunConfig.properties").getAbsolutePath()).get("linuxMavenEnvVarKey").toString();
	}
}

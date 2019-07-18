package io.mosip.authentication.fw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.aventstack.extentreports.utils.StringUtil;

import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.dto.VidDto;
import io.mosip.authentication.fw.dto.VidStaticPinDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;
import io.restassured.response.Response;

public class VIDUtil extends RunConfigUtil{
	private static final Logger vidLogger = Logger.getLogger(VIDUtil.class);
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
			String uin = UINUtil.getRandomUINKey();
			if (VidDto.getVid().containsKey(uin)) {
				if (VidDto.getVid().get(uin).contains(".ACTIVE")) {
					if (VidDto.getVid().get(uin).toLowerCase().contains(".Temporary".toLowerCase())
							&& !VidDto.getVid().get(uin).endsWith(".0")) {
						String vid = VidDto.getVid().get(uin).toString().split(Pattern.quote("."))[0];
						String type = VidDto.getVid().get(uin).toString().split(Pattern.quote("."))[1];
						String status = VidDto.getVid().get(uin).toString().split(Pattern.quote("."))[2];
						Map<String, String> tempMap = new HashMap<String, String>();					
						if(isVidExpiredOrUsed(vid)) {
							vid=regenerateVID(uin,vid,type);
							tempMap.put(uin, vid + "." + type + "." + status + "." + RunConfigUtil.getVidUsageCount());
							updateVIDPropertyFile(tempMap);
							vidLogger.info("Regenerated VID:"+tempMap);
						}
						else if (isVidInvalidated(vid))
						{
							vid=generateVID(uin, type);
							tempMap.put(uin, vid + "." + type + "." + status + "." + RunConfigUtil.getVidUsageCount());
							updateVIDPropertyFile(tempMap);
							vidLogger.info("Generated new VID:"+tempMap);
						}
						else {
						int usageCount = Integer
								.parseInt(VidDto.getVid().get(uin).toString().split(Pattern.quote("."))[3].toString());
						tempMap.put(uin, vid + "." + type + "." + status + "." + (usageCount - 1));
						updateVIDPropertyFile(tempMap);
						}
						return vid;
					} else if (VidDto.getVid().get(uin).toLowerCase().contains(".Perpetual".toLowerCase()))
						return VidDto.getVid().get(uin).toString().split(Pattern.quote("."))[0];
				}
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
				UINUtil.getUinPropertyValue(getUinPropertyPath());
				if(UinDto.getUinData().get(UINUtil.getUinForVid(key.toString().split(Pattern.quote("."))[0])).contains("valid"))
					return key.toString();
			}else if (testCaseName.contains("Deactivated") && key.toString().contains("Perpetual") && key.toString().contains(".ACTIVE")) {
				count++;
				return key.toString();
			}
			if(count>100)
				return "1234567890123456.DUMMY.DUMMY";
		}
		return "NoVIDFound";
	}
	
	public static boolean isVidExpiredOrUsed(String vid)
	{
		String url=RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()+RunConfigUtil.objRunConfig.getIdRepoRetrieveUINByVIDPath();
		url=url.replace("$vid$", vid);
		String cookieValue= AuthTestsUtil.getAuthorizationCookie(AuthTestsUtil.getCookieRequestFilePath(),RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()+RunConfigUtil.objRunConfig.getClientidsecretkey(),AuthTestsUtil.AUTHORIZATHION_COOKIENAME);
		Response response=RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, AuthTestsUtil.AUTHORIZATHION_COOKIENAME, cookieValue);
		if(response.asString().contains("EXPIRED") || response.asString().contains("USED"))
			return true;
		else
			return false;
	}
	
	public static String regenerateVID(String uin,String vid,String type) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoRegenerateVID();
		url = url.replace("$vid$", vid);
		String cookieValue = AuthTestsUtil.getAuthorizationCookie(AuthTestsUtil.getCookieRequestFilePath(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AuthTestsUtil.AUTHORIZATHION_COOKIENAME);
		Response response = RestClient.postRequestWithCookie(url, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, AuthTestsUtil.AUTHORIZATHION_COOKIENAME, cookieValue);
		String newVid=JsonPrecondtion.getValueFromJson(response.asString(), "response.VID").toString();
		if(StringUtils.isNumeric(newVid))
			return newVid;
		else
			return generateVID(uin, type);
	}
	
	public static void regenerateVID() {
		getVidPropertyValue(getVidPropertyPath());
		for (Entry<String, String> entry : VidDto.getVid().entrySet()) {
			String newVID = "";
			Map<String, String> tempMap = new HashMap<String, String>();
			String uin = entry.getKey();
			String vid = entry.getValue().toString().split(Pattern.quote("."))[0];
			String type = entry.getValue().toString().split(Pattern.quote("."))[1];
			String status = entry.getValue().toString().split(Pattern.quote("."))[2];
			int usageCount = 0;
			if (type.equalsIgnoreCase("Temporary") && status.equalsIgnoreCase("ACTIVE")) {
				usageCount = Integer.parseInt(entry.getValue().toString().split(Pattern.quote("."))[3]);
				if (isVidExpiredOrUsed(vid)) {
					newVID = regenerateVID(uin,vid,type);
					if(!StringUtils.isNumeric(newVID))
						newVID=generateVID(uin, type);
					tempMap.put(uin, newVID + "." + type + "." + status + "."+RunConfigUtil.getVidUsageCount());
					updateVIDPropertyFile(tempMap);
					vidLogger.info("RegenerestedVID: " + tempMap);
				}
				else if(isVidInvalidated(vid))
				{
					newVID=generateVID(uin, type);
					tempMap.put(uin, newVID + "." + type + "." + status + "."+RunConfigUtil.getVidUsageCount());
					updateVIDPropertyFile(tempMap);
					vidLogger.info("Generated New VID: " + tempMap);
				}
				else if (usageCount == 0) {
					usageCount = Integer.parseInt(entry.getValue().toString().split(Pattern.quote("."))[3]);
					newVID = regenerateVID(uin,vid,type);
					tempMap.put(uin, newVID + "." + type + "." + status + "."+RunConfigUtil.getVidUsageCount());
					updateVIDPropertyFile(tempMap);
					vidLogger.info("RegenerestedVID: " + tempMap);
				}
			}

		}
	}
	
	public static boolean updateVIDPropertyFile(Map<String, String> tempMap) {
		try {
			AuthTestsUtil.updateMappingDic(RunConfigUtil.getResourcePath() + "ida/TestData/RunConfig/vid.properties",
					tempMap);
			AuthTestsUtil.updateMappingDic(
					RunConfigUtil.getResourcePath() + "idRepository/TestData/RunConfig/vid.properties", tempMap);
			return true;
		} catch (Exception e) {
			vidLogger.info("Exception in updating the VID property file : ");
			return false;
		}
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
	
	public static boolean isVidInvalidated(String vid) {
		String url = RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoRetrieveUINByVIDPath();
		url = url.replace("$vid$", vid);
		String cookieValue = AuthTestsUtil.getAuthorizationCookie(AuthTestsUtil.getCookieRequestFilePath(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AuthTestsUtil.AUTHORIZATHION_COOKIENAME);
		Response response = RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				AuthTestsUtil.AUTHORIZATHION_COOKIENAME, cookieValue);
		if (response.asString().contains("INVALIDATED"))
			return true;
		else
			return false;
	}
	
	public static String generateVID(String uin, String type) {
		String cookieValue = AuthTestsUtil.getAuthorizationCookie(
				AuthTestsUtil.getCookieRequestFilePathForUinGenerator(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AuthTestsUtil.AUTHORIZATHION_COOKIENAME);
		String url = RunConfigUtil.objRunConfig.getEndPointUrl()
				+ RunConfigUtil.objRunConfig.getIdRepoCreateVIDRecordPath();
		String updateUinInJsonRequest = JsonPrecondtion.parseAndReturnJsonContent(
				AuthTestsUtil.getVidRequestContentTemplate().toString(), "LONG:" + uin, "request.UIN".toString());
		String updateTimeStamp=JsonPrecondtion.parseAndReturnJsonContent(updateUinInJsonRequest, IdaKeywordUtil.generateTimeStampWithZTimeZone(),
				"requesttime".toString());
		String content = JsonPrecondtion.parseAndReturnJsonContent(updateTimeStamp, type,
				"request.vidType".toString());
		String responseContent = AuthTestsUtil.postRequestAndGetResponseForVIDGeneration(content, url,
				AuthTestsUtil.AUTHORIZATHION_COOKIENAME, cookieValue).toString();
		return JsonPrecondtion.getValueFromJson(responseContent, "response.VID");
	}

}

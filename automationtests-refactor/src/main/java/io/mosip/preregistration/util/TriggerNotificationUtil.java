package io.mosip.preregistration.util;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.BeforeClass;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.preregistration.service.PreRegistrationApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.restassured.response.Response;
/**
 * Class is to perform Notification Service smoke and regression test operations
 * 
 * @author Lavanya R
 * @since 1.0.0
 */
public class TriggerNotificationUtil {

	/**
	 * Declaration of all variables
	 **/
	
	PreRegistrationUtil preregUtil = new PreRegistrationUtil();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	ApplicationLibrary appLib = new ApplicationLibrary();
	PreRegistrationApplicationLibrary applnLib = new PreRegistrationApplicationLibrary();
	String testSuite = "";
	JSONObject request;
	Response response;
	String value = null;
	JSONObject object = null;
	File file;
	
	/**
	 * Fetching the details from property files
	 **/
	String notification_URI = preregUtil.fetchPreregProp().get("preReg_NotifyURI");
	String triggerNotificationFilePath = preregUtil.fetchPreregProp().get("notificationFilePath");
	String triggerNotificationReqName = preregUtil.fetchPreregProp().get("req.notify");
	String docFilePath = preregUtil.fetchPreregProp().get("docFilePath");
	String langCodeKey = preregUtil.fetchPreregProp().get("langCode.key");
	String configPath= preregUtil.fetchPreregProp().get("configPath");
	String notificationSmokeTestFilePath= preregUtil.fetchPreregProp().get("notificationSmokeTestFilePath");
	String fileKeyName = preregUtil.fetchPreregProp().get("req.fileName");
	String validDocFilePath=preregUtil.fetchPreregProp().get("validDocFilePath");
	String fileName=preregUtil.fetchPreregProp().get("fileName");
	
	
	
	
	/**
	 * The method perform TriggerNotification API Smoke and Regression test operation
	 *  
	 * @param endpoint
	 * @param request
	 * @param cookie
	 * @param fileName
	 * @return Response
	 */
	public Response TriggerNotification(String endpoint, JSONObject request, String cookie, String testCaseFileName) {

		testSuite = triggerNotificationFilePath + testCaseFileName;
		
		//This condition can be used during integration scenarios which can be called by passing null value for request
		if (request == null) {
			fileName = notificationSmokeTestFilePath;
			testSuite = triggerNotificationFilePath + testCaseFileName;
			request = preregUtil.requestJson(testSuite,"request");
			request.put("requesttime", preregUtil.getCurrentDate());

		}
		file = new File(configPath+validDocFilePath+fileName);
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				value = (String) object.get(langCodeKey);
				object.remove(langCodeKey);
			}
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(langCodeKey, value);
		map.put(triggerNotificationReqName, request.toJSONString());
		
		
		response = appLib.postWithFileFormParams(notification_URI, map, file, fileKeyName, cookie);
		
		
		return response;
	}

	

}

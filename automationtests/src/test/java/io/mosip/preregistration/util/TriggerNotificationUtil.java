package io.mosip.preregistration.util;

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.BeforeClass;

import io.mosip.preregistration.service.PreRegistrationApplicationLibrary;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class TriggerNotificationUtil {

	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String testSuite = "";
	JSONObject request;
	Response response;
	PreRegistrationUtil preregUtil = new PreRegistrationUtil();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	PreRegistrationApplicationLibrary applnLib = new PreRegistrationApplicationLibrary();
	String	notification_URI = preregUtil.fetchPreregProp().get("preReg_NotifyURI");
	String triggerNotificationFilePath = preregUtil.fetchPreregProp().get("notificationFilePath");
	String docFilePath = preregUtil.fetchPreregProp().get("documentFilePath");
	String langCodeKey = preregUtil.fetchPreregProp().get("langCode.key");

	/**
	 * Generic method to Trigger Notification
	 * 
	 */

	public Response TriggerNotification(String fileName) {
		testSuite = triggerNotificationFilePath+fileName;
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + docFilePath);
       
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String value = null;
		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				value = (String) object.get(langCodeKey);
				object.remove(langCodeKey);
			}
		}
		request.put("requesttime", PreRegistrationLibrary.getCurrentDate());
		response = applnLib.postFileAndJsonParam(notification_URI, request, file, langCodeKey, value);

		return response;
	}

	
}

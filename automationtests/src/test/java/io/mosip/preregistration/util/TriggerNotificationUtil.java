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
import io.restassured.response.Response;

public class TriggerNotificationUtil {

	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String testSuite = "";
	JSONObject request;
	Response response;
	String notification_URI;
	PreRegistrationApplicationLibrary applnLib = new PreRegistrationApplicationLibrary();
	PreRegistrationUtil preregUtil = new PreRegistrationUtil();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	String triggerNotificationFilePath;
	String docFilePath;
	String langCodeKey;

	/**
	 * Generic method to Trigger Notification
	 * 
	 */

	public Response TriggerNotification() {
		testSuite = triggerNotificationFilePath;
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
		request.put("requesttime", preregUtil.getCurrentDate());
		response = applnLib.putFileAndJsonParam(notification_URI, request, file, langCodeKey, value);

		return response;
	}

	/**
	 * Fetching the values from property files
	 * 
	 */

	@BeforeClass
	public void PreRegistrationResourceIntialize() {
		notification_URI = preregUtil.fetchPreregProp().get("preReg_NotifyURI");
		triggerNotificationFilePath = preregUtil.fetchPreregProp().get("notificationFilePath");
		docFilePath = preregUtil.fetchPreregProp().get("documentFilePath");
		langCodeKey = preregUtil.fetchPreregProp().get("langCode.key");
	}
}

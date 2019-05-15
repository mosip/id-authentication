package io.mosip.preregistration.util;

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.BeforeClass;

import io.mosip.preregistration.service.PreRegistrationApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class QRCodeUtil 
{

	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String testSuite = "";
	JSONObject request;
	Response response;
	String qrCode_URI;
	PreRegistrationApplicationLibrary applnLib = new PreRegistrationApplicationLibrary();
	PreRegistrationUtil preregUtil=new PreRegistrationUtil();
	String qrCodeFilePath;
	Logger logger = Logger.getLogger(BaseTestCase.class);
	
	/**
	 * Generic method to QR Code
	 * 
	 */

	public Response QRCode() {
		testSuite = qrCodeFilePath;
		String configPath = "src/test/resources/" + folder + "/" + testSuite;

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

		request.put("requesttime", preregUtil.getCurrentDate());
		response = applnLib.postRequest(request, qrCode_URI);

		return response;
	}

	/**
	 * Fetching the values from property files
	 * 
	 */
	
	
	@BeforeClass
	public void PreRegistrationResourceIntialize() 
	{
	qrCode_URI = preregUtil.fetchPreregProp().get("preReg_QRCodeURI");
	qrCodeFilePath=preregUtil.fetchPreregProp().get("qrCodeFilePath");
	}
}

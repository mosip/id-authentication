package io.mosip.preregistration.util;

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;
/**
 * Util Class is to perform Generate QR code service smoke and regression test operations
 * 
 * @author Lavanya R
 * @since 1.0.0
 */
public class QRCodeUtil 
{

	/**
	 * Declaration of all variables
	 **/
	
	String testSuite = "";
	JSONObject request;
	Response response;
	ApplicationLibrary appLib = new ApplicationLibrary();
	PreRegistrationUtil preregUtil=new PreRegistrationUtil();
	PreRegistrationLibrary lib=new PreRegistrationLibrary();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	
	

	
	/**
	 * The method perform QR Code API Smoke and Regression test operation
	 *  
	 * @param endpoint
	 * @param request
	 * @param cookie
	 * @return Response
	 */
	
	public Response QRCode(String endpoint,JSONObject request,String cookie) {
		
		if(request==null)
		{
		testSuite =preregUtil.fetchPreregProp().get("qrCodeFilePath");
		request=preregUtil.requestJson(testSuite);
		request.put("requesttime", lib.getCurrentDate());
		
		}
		
		response = appLib.postWithJson(endpoint, request, cookie);

		return response;
	}

	
}
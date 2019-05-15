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

public class SyncMasterDataUtil 
{

	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String testSuite = "";
	JSONObject request;
	Response response;
	String syncMasterData_URI;
	PreRegistrationApplicationLibrary applnLib = new PreRegistrationApplicationLibrary();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	PreRegistrationUtil preregUtil=new PreRegistrationUtil();
	String syncMasterDataFilePath;

	/**
	 * Generic method for synchronize booking slots availability table with master data
	 * 
	 */

	public Response syncMasterData() {
		Response syncMasterDataRes = null;
		try {

			syncMasterDataRes = applnLib.postRequestWithoutBody(syncMasterData_URI);
		} catch (Exception e) {
			logger.info(e);
		}

		return syncMasterDataRes;
	}

	/**
	 * Fetching the values from property files
	 * 
	 */
	
	
	@BeforeClass
	public void PreRegistrationResourceIntialize() 
	{
		syncMasterData_URI = preregUtil.fetchPreregProp().get("preReg_SyncMasterDataURI");
	}
}

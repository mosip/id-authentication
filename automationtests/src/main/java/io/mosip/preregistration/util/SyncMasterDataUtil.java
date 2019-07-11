package io.mosip.preregistration.util;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.preregistration.service.PreRegistrationApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;
/**
 * Util Class is to perform Sync Master Data service smoke and regression test operations
 * 
 * @author Lavanya R
 * @since 1.0.0
 */
public class SyncMasterDataUtil 
{

	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String testSuite = "";
	JSONObject request;
	Response response;
	PreRegistrationApplicationLibrary applnLib = new PreRegistrationApplicationLibrary();
	ApplicationLibrary appLib=new ApplicationLibrary();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	PreRegistrationUtil preregUtil=new PreRegistrationUtil();
	String syncMasterDataFilePath;
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();

	String syncMasterData_URI= preregUtil.fetchPreregProp().get("preReg_SyncMasterDataURI");
	
	/**
	 * The method perform Sync Master Data API Smoke and Regression test operation
	 *  
	 * @param endpoint
	 * @param cookie
	 * @return Response
	 */

	public Response syncMasterData(String endpoint,String cookie) {
		Response syncMasterDataRes = null;
		try {
			cookie=preRegLib.preRegAdminToken();
			syncMasterDataRes = appLib.getWithoutParams(endpoint, cookie);
			
		} catch (Exception e) {
			logger.info(e);
		}

		return syncMasterDataRes;
	}
		
}
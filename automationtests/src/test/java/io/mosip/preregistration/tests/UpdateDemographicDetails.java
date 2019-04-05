package io.mosip.preregistration.tests;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class UpdateDemographicDetails extends BaseTestCase implements ITest{
	Logger logger = Logger.getLogger(BatchJob.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	protected static String testCaseName = "";
	static String folder = "preReg";
	private static CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applnLib = new ApplicationLibrary();

	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();
	
	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}
	@Test
	public void updateDemographicDetailsOfPendingAppointmentApplication()
	{
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		JSONObject object = null;
		/**\
		 * object.put("dateOfBirth", "12345^");
				createRequest.replace(key, object);
		 */
		for (Object key : createRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) createRequest.get(key);
				for ( Object key1: object.keySet()) {
					if (key.equals("demographicDetails")) {
						JSONObject demographicDetailsobject = (JSONObject) object.get(key);
						if (key.equals("identity")) {
							JSONObject identity = (JSONObject) demographicDetailsobject.get(key);
							object.put("dateOfBirth", "12345^");
						}
					}
					
				}
				
			}
			
	}
		
	}
	
	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		System.out.println("method name:" + result.getMethod().getMethodName());
	}

}

package io.mosip.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.mosip.util.GetHeader;
import io.restassured.response.Response;


/**
 * 
 * @author M1047227
 *
 */
public class IntegMethods extends BaseTestCase {
	public final static String Reg_Proc_PacketLanding_URI="/packetreceiver/v0.1/registration-processor/packet-receiver/registrationpackets";
	public final static String Reg_Proc_Get_URI="/registrationstatus/v0.1/registration-processor/registration-status/registrationstatus";
	final static String folder="regProc/IntegrationScenarios";
	JSONParser parser=new JSONParser();
	ApplicationLibrary applnMethods=new ApplicationLibrary();
	Properties prop=new Properties();
	private static Logger logger = Logger.getLogger(IntegMethods.class);
	List<String> innerKeys= new ArrayList<String>();
	List<String> outerKeys=new ArrayList<String>();
	AssertResponses assertResponses=new AssertResponses();
	/**
	 * 
	 * @param testCase
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * This method takes testCaseName as a parameter and reads the request/response file inside that folder
	 * It asserts the response and returns the response.
	 */
	public Response syncList(String testCase) throws FileNotFoundException, IOException, ParseException {
		String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
		prop.load(new FileReader(new File(propertyFilePath)));
		JSONObject actualRequest=null;
		JSONObject expectedResponse=null;
		String component="SyncList";
		String configPath= "src/test/resources/" + folder+"/"+testCase+"/SyncPacket";
		System.out.println(configPath);
		File file=new File(configPath);
		File[] folder=file.listFiles();
		for(File f:folder) {
			if(f.getName().toLowerCase().contains("request")) {
				actualRequest=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
			else if(f.getName().toLowerCase().contains("response")) {
				expectedResponse=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
		}
		try {
			System.out.println(prop.getProperty("syncListApi"));
			Response actualResponse=applnMethods.postRequest(actualRequest, prop.getProperty("syncListApi"));
			logger.info("Expected Response is :: "+ expectedResponse.toJSONString());
			logger.info("Actual Response is :: "+ actualResponse.asString());
			outerKeys.add("responseTimestamp");
			boolean status=AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
			Assert.assertTrue(status);
			return actualResponse;
		}catch(AssertionError | ClassCastException err) {
			err.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param syncResponse
	 * @param testCase
	 * @return
	 * @throws ParseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * This methods takes response and testCase name as the parameters and reads the files and uploads them to packetReceiver
	 * Asserts the response and returns it
	 */
	public Response UploadPacket(Response syncResponse,String testCase) throws ParseException, FileNotFoundException, IOException {
		String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
		prop.load(new FileReader(new File(propertyFilePath)));
		Response actualResponse=null;
		JSONObject expectedResponse=null;
		String component="PacketReceiver";
		String configPath= "src/test/resources/" + folder+"/"+testCase+"/UploadPacket";
		File file=new File(configPath);
		File[] folder=file.listFiles();
		JSONObject response=(JSONObject) parser.parse(syncResponse.asString());
		JSONArray responseArray=(JSONArray) response.get("response");
		JSONObject responseObject=(JSONObject) responseArray.get(0);
		String status=responseObject.get("status").toString();
		if(status.equals("SUCCESS")) {
			for(File f: folder) {
				if(f.getName().toLowerCase().contains(responseObject.get("registrationId").toString())) {
					actualResponse=applnMethods.putMultipartFile(f, prop.getProperty("packetReceiverApi"));
				}
				else if(f.getName().toLowerCase().contains("response")) {
					expectedResponse=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				}
			}
			try {
				outerKeys.add("responseTimestamp");
				boolean assertStatus=AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
				Assert.assertTrue(assertStatus);
				return actualResponse;
			}catch(AssertionError err) {
				Assert.fail();
				err.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 
	 * @param testCase
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void getStatus(String testCase) throws FileNotFoundException, IOException, ParseException {
		Response actualResponse=null;
		String component="GetRequest";
		JSONObject actualRequest=null;
		JSONObject expectedResponse=null;
		String configPath= "src/test/resources/" + folder+"/"+testCase+"/GetStatus";
		File file=new File(configPath);
		File[] folder=file.listFiles();
		for(File f:folder) {
			if(f.getName().toLowerCase().contains("request")) {
				actualRequest=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
			else if(f.getName().toLowerCase().contains("response")) {
				expectedResponse=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
		}
		actualResponse=applnMethods.getRequest( prop.getProperty("packetStatusApi"),actualRequest);
		outerKeys.add("responseTimestamp");
		boolean assertStatus=AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
		Assert.assertTrue(assertStatus);
		try {
		}catch(AssertionError err) {
			err.printStackTrace();
		}
	}
}

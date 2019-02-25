package io.mosip.regProc.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.GetHeader;
import io.restassured.response.Response;


/**
 * 
 * @author M1047227
 *
 */
public class IntegMethods extends BaseTestCase {
	public final static String Reg_Proc_Sync_URI="/registrationstatus/v0.1/registration-processor/registration-status/sync";
	public final static String Reg_Proc_PacketLanding_URI="/packetreceiver/v0.1/registration-processor/packet-receiver/registrationpackets";
	public final static String Reg_Proc_Get_URI="/registrationstatus/v0.1/registration-processor/registration-status/registrationstatus";
	final static String folder="regProc/IntegrationScenarios";
	JSONParser parser=new JSONParser();
	ApplicationLibrary applnMethods=new ApplicationLibrary();
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
		JSONArray actualRequest=null;
		JSONArray expectedResponse=null;
		
		String component="SyncList";
		String configPath= "src/test/resources/" + folder+"/"+component+"/"+testCase;
		File file=new File(configPath);
		File[] folder=file.listFiles();
		for(File f:folder) {
			if(f.getName().toLowerCase().contains("request")) {
				actualRequest=(JSONArray) new JSONParser().parse(new FileReader(f.getPath()));
			}
			else if(f.getName().toLowerCase().contains("response")) {
				expectedResponse=(JSONArray) new JSONParser().parse(new FileReader(f.getPath()));
			}
		}
		try {
			Response restAssuredResponse=applnMethods.postRequest(actualRequest, Reg_Proc_Sync_URI);
			JSONArray actualResponse=(JSONArray) parser.parse(restAssuredResponse.asString());
			Assert.assertTrue(Arrays.asList(actualResponse).equals(Arrays.asList(expectedResponse)));
			return restAssuredResponse;
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
		Response apiResponse=null;
		JSONObject expectedResponse=null;
		String component="PacketReceiver";
		String configPath= "src/test/resources/" + folder+"/"+component+"/"+testCase;
		File file=new File(configPath);
		File[] folder=file.listFiles();
		JSONArray response=(JSONArray) parser.parse(syncResponse.asString());
		JSONObject responseObject=(JSONObject) response.get(0);
		String status=responseObject.get("status").toString();
		if(status.equals("SUCCESS")) {
			for(File f: folder) {
				if(f.getName().toLowerCase().contains(responseObject.get("registrationId").toString())) {
					apiResponse=applnMethods.putMultipartFile(f,Reg_Proc_PacketLanding_URI);
				}
				else if(f.getName().toLowerCase().contains("response")) {
					expectedResponse=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				}
			}
			try {
				JSONObject actualResponse=(JSONObject) apiResponse;
				Assert.assertTrue(expectedResponse.get("message").equals(actualResponse.get("message")));
				return apiResponse;
			}catch(ClassCastException err) {
				String actualResponse=apiResponse.asString();
				Assert.assertTrue(actualResponse.equals(expectedResponse.get("message").toString()));
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
		JSONArray expectedResponse=null;
		String configPath="src/test/resources/" + folder+"/"+component+"/"+testCase;
		File file=new File(configPath);
		File[] folder=file.listFiles();
		for(File f:folder) {
			if(f.getName().toLowerCase().contains("request")) {
				actualRequest=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
			else if(f.getName().toLowerCase().contains("response")) {
				expectedResponse=(JSONArray) new JSONParser().parse(new FileReader(f.getPath()));
			}
		}
		actualResponse=applnMethods.getRequest(Reg_Proc_Get_URI, GetHeader.getHeader(actualRequest));
		try {
			Assert.assertTrue(Arrays.asList(actualResponse).equals(Arrays.asList(expectedResponse)));
		}catch(AssertionError err) {
			err.printStackTrace();
		}
	}

}

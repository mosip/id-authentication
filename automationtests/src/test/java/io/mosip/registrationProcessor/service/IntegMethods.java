package io.mosip.registrationProcessor.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import io.mosip.dbaccess.RegProcDBCleanUp;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.EncrypterDecrypter;
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
	private final String encrypterURL="https://int.mosip.io/v1/cryptomanager/encrypt";
	String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
	String registrationID="";
	JSONParser parser=new JSONParser();
	ApplicationLibrary applnMethods=new ApplicationLibrary();
	Properties prop=new Properties();
	private static Logger logger = Logger.getLogger(IntegMethods.class);
	List<String> innerKeys= new ArrayList<String>();
	List<String> outerKeys=new ArrayList<String>();
	AssertResponses assertResponses=new AssertResponses();
	RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
	EncryptData encryptData=new EncryptData();
	SoftAssert softAssert=new SoftAssert();
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
	public boolean syncList(File packet) throws FileNotFoundException, IOException, ParseException {
		RegistrationPacketSyncDTO registrationPacketSyncDto=null;;
		try {
			registrationPacketSyncDto = encryptData.createSyncRequest(packet);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();
		JSONObject requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);
		String center_machine_refID=regId.substring(0,5)+"_"+regId.substring(5, 10);
		Response resp=applnMethods.postRequestToDecrypt(requestToEncrypt, encrypterURL);
		String encryptedData = resp.jsonPath().get("response.data").toString();
		LocalDateTime timeStamp=null;
		try {
			timeStamp = encryptData.getTime(regId);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response actualResponse = applnMethods.regProcSync(encryptedData,prop.getProperty("syncListApi"),center_machine_refID,
				timeStamp.toString()+"Z");
		int status=actualResponse.statusCode();
		try {
		Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.sync"));
		Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
		Assert.assertTrue(actualResponse.jsonPath().get("response[0].status").equals("SUCCESS"));
		Assert.assertTrue(actualResponse.jsonPath().get("response[0].registrationId").equals(regId));
		return true;
		}catch (AssertionError e) {
			return false;
		}
		
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
	public boolean UploadPacket(File packet) throws ParseException, FileNotFoundException, IOException {
		Response actualResponse=applnMethods.regProcPacketUpload(packet, prop.getProperty("packetReceiverApi"));
		try {
		Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.sync"));
		Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
		Assert.assertTrue(actualResponse.jsonPath().get("response.status").equals("Packet is in PACKET_RECEIVED status"));
		return true;
		}catch (AssertionError e) {
		return false;
		}
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
		JSONObject requestToBeSent=null;
		String configPath= "src/test/resources/" + folder+"/"+testCase+"/GetStatus";
		File file=new File(configPath);
		File[] folder=file.listFiles();
		for(File f:folder) {
			if(f.getName().toLowerCase().contains("request")) {
				actualRequest=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				requestToBeSent=(JSONObject) actualRequest.get("request");
			}
			else if(f.getName().toLowerCase().contains("response")) {
				expectedResponse=(JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
		}
		actualResponse=applnMethods.regProcGetRequest(prop.getProperty("packetStatusApi"),actualRequest);
		outerKeys.add("responsetime");
		boolean assertStatus=AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
		//clearFromDB(registrationID);
		Assert.assertTrue(assertStatus);
		
		try {
		}catch(AssertionError err) {
			err.printStackTrace();
		}
	}
	public void clearFromDB(String regID) {
		cleanUp.prepareQueryList(regID);
	}
}

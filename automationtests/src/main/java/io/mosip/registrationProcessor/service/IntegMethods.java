package io.mosip.registrationProcessor.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import io.mosip.dbaccess.RegProcDBCleanUp;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.registrationProcessor.util.TweakRegProcPackets;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.EncrypterDecrypter;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;
import net.lingala.zip4j.exception.ZipException;


/**
 * 
 * @author M1047227
 *
 */
public class IntegMethods extends BaseTestCase {
	public final static String Reg_Proc_PacketLanding_URI="/packetreceiver/v0.1/registration-processor/packet-receiver/registrationpackets";
	public final static String Reg_Proc_Get_URI="/registrationstatus/v0.1/registration-processor/registration-status/registrationstatus";
	final static String folder="regProc/IntegrationScenarios";
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	String propertyFilePath=apiRequests.getResourcePath()+"config/registrationProcessorAPI.properties";
	String registrationID="";
	JSONParser parser=new JSONParser();
	ApplicationLibrary applnMethods=new ApplicationLibrary();
	Properties prop=new Properties();
	private static Logger logger = Logger.getLogger(IntegMethods.class);
	EncrypterDecrypter encryptDecrypt=new EncrypterDecrypter();
	List<String> innerKeys= new ArrayList<String>();
	List<String> outerKeys=new ArrayList<String>();
	AssertResponses assertResponses=new AssertResponses();
	RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
	EncryptData encryptData=new EncryptData();
	SoftAssert softAssert=new SoftAssert();
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	String validToken="";
	TweakRegProcPackets tweakPackets=new TweakRegProcPackets();
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
	}
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
		validToken = getToken("syncTokenGenerationFilePath");
		boolean tokenStatus=apiRequests.validateToken(validToken);
		while(!tokenStatus) {
			validToken = getToken("syncTokenGenerationFilePath");
			tokenStatus=apiRequests.validateToken(validToken);
		} 
		RegistrationPacketSyncDTO registrationPacketSyncDto=null;;
		try {
			registrationPacketSyncDto = encryptData.createSyncRequest(packet,"NEW"); 
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		String regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();
		JSONObject requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);
		String center_machine_refID=regId.substring(0,5)+"_"+regId.substring(5, 10);
		Response resp=apiRequests.postRequestToDecrypt(encrypterURL,requestToEncrypt,MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,validToken); 

		//Response resp=applnMethods.postRequestToDecrypt(requestToEncrypt, encrypterURL);
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
		Response actualResponse = apiRequests.regProcSyncRequest(prop.getProperty("syncListApi"),encryptedData,center_machine_refID,
				timeStamp.toString()+"Z", MediaType.APPLICATION_JSON,validToken);
		int status=actualResponse.statusCode();
		try {
			Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.sync"));
			Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
			Assert.assertTrue(actualResponse.jsonPath().get("response[0].status").equals("SUCCESS"));
			Assert.assertTrue(actualResponse.jsonPath().get("response[0].registrationId").equals(regId));
			validToken="";
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

		validToken= getToken("syncTokenGenerationFilePath");
		boolean tokenStatus=apiRequests.validateToken(validToken);
		while(!tokenStatus) {
			validToken = getToken("syncTokenGenerationFilePath");
			tokenStatus=apiRequests.validateToken(validToken);
		} 
		Response actualResponse=apiRequests.regProcPacketUpload(packet, prop.getProperty("packetReceiverApi"),validToken);
		try {
			Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.sync"));
			Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
			//Assert.assertTrue(actualResponse.jsonPath().get("response.status").equals("Packet is in PACKET_RECEIVED status"));
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
		validToken = getToken("syncTokenGenerationFilePath");
		Response actualResponse=null; 
		String component="GetRequest";
		JSONObject actualRequest=null;
		JSONObject expectedResponse=null;
		JSONObject requestToBeSent=null;
		String configPath= apiRequests.getResourcePath() + folder+"/"+testCase+"/GetStatus";
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
		actualResponse=apiRequests.regProcGetRequest(prop.getProperty("packetStatusApi"),actualRequest,validToken);
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
	String centerId ="";
	String machineId="";
	String regId ="";
	public File decryptPacket(File file) {
		File decryptedPacket=null;
		JSONObject cryptographicRequest=encryptDecrypt.generateCryptographicData(file);
		centerId = file.getName().substring(0, 5);
		machineId = file.getName().substring(5, 10);
		try {
			regId = generateRegId(centerId, machineId,file.getName().substring(0,file.getName().lastIndexOf(".")));
		} catch (java.text.ParseException e1) {
			logger.error("Could Not Generate regId",e1);
		}
		try {
			decryptedPacket=encryptDecrypt.decryptFile(cryptographicRequest, file.getParent(), file.getName());
			System.out.println(decryptedPacket);
		} catch (IOException | ZipException | ParseException e) {
			logger.error("Could Not Decrypt The Packets",e);
		}
		return decryptedPacket;
	}
	public void updateRegId(File decryptedPacket) {
		JSONObject metaInfo = null;
		for (File info : decryptedPacket.listFiles()) {
			byte[] checkSum = null;
			String str = "";
			if (info.getName().toLowerCase().equals("packet_meta_info.json")) {
				try {
					FileReader metaFileReader = new FileReader(info.getPath());
					metaInfo = (JSONObject) new JSONParser().parse(metaFileReader);
					metaFileReader.close();
				} catch (IOException | ParseException e) {
					logger.error("Could not find the packet_meta_info.json", e);
				}
				JSONObject identity = (JSONObject) metaInfo.get("identity");
				JSONArray metaData = (JSONArray) identity.get("metaData");
				JSONArray updatedData = tweakPackets.updateRegId(metaData, regId);
				metaInfo.put("identity", identity);
				try (FileWriter updatedFile = new FileWriter(info.getAbsolutePath())) {
					try {
						updatedFile.write(metaInfo.toString());
						updatedFile.close();
					} catch (IOException e) {
						logger.error("Could not update the packet_meta_info.json as file was not found", e);
					}
					logger.info("Successfully updated json object to file...!!");

				} catch (IOException e1) {
					logger.error("Could not find the packet_meta_info.json", e1);
				}

			}

		}
	}
	public File updateCheckSum(File decryptedPacket) {
		byte[] checkSum = null;
		String str = "";
		try {
			checkSum = encryptDecrypt.generateCheckSum(decryptedPacket.listFiles());
			str = new String(checkSum, StandardCharsets.UTF_8);
		} catch (IOException | ParseException e) {
			logger.error("Could Not Update The CheckSum",e);
		}
		for(File info:decryptedPacket.listFiles()) {
			if (info.getName().equals("packet_data_hash.txt")) {
				PrintWriter writer=null;
				try {
					writer = new PrintWriter(info);
				} catch (FileNotFoundException e1) {
					logger.error("Could not Update Checksum",e1);
				}
				writer.print("");
				writer.print(str);
				writer.close();

			}
		}
		return decryptedPacket;
	}
	public void encryptFile(File decryptedFile) {
		File temporaryFile=new File(decryptedFile.getParent());
		temporaryFile.getParent();
		try {
			encryptDecrypt.encryptFile(decryptedFile,temporaryFile.getParent(),
					temporaryFile.getParent() + "/generatedPacket" , regId);
		} catch (ZipException | IOException e) {
			logger.error("Could Not Encrypt The File",e);
		}
	}
	public void destroyTempFiles(File decryptedFile) {
		try {
			encryptDecrypt.destroyFiles(decryptedFile.getParent());
		} catch (IOException e) {
			logger.info("Junk Files still exist",e);
		}
	}
	public String generateRegId(String centerId,String machineId,String regId) throws java.text.ParseException {
		String regID="";

		String packetCreatedDateTime = regId.substring(regId.length() - 14);

		int number = 10000 + new Random().nextInt(90000);
		String randomNumber = String.valueOf(number);

		regID = centerId + machineId + randomNumber + packetCreatedDateTime.toString();

		return regID;
	}

	public boolean asssignment(JSONObject requestJson) throws ParseException, FileNotFoundException, IOException {

		validToken= getToken("getStatusTokenGenerationFilePath");
		boolean tokenStatus=apiRequests.validateToken(validToken);
		while(!tokenStatus) {
			validToken = getToken("getStatusTokenGenerationFilePath");
			tokenStatus=apiRequests.validateToken(validToken);
		} 
		Response actualResponse=apiRequests.regProcPostRequest(prop.getProperty("assignmentApi"),requestJson,MediaType.APPLICATION_JSON,validToken);
		try {
			Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.assignment"));
			Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
			//Assert.assertTrue(actualResponse.jsonPath().get("response.status").equals("Packet is in PACKET_RECEIVED status"));
			return true;
		}catch (AssertionError e) {
			return false;
		}
	}

	public void decision(String testCase) throws ParseException, FileNotFoundException, IOException {
		validToken = getToken("getStatusTokenGenerationFilePath");
		Response actualResponse=null; 
		String component="Decision";
		JSONObject actualRequest=null;
		JSONObject expectedResponse=null;
		JSONObject requestToBeSent=null;
		String configPath= apiRequests.getResourcePath() + folder+"/"+testCase+"/Decision";
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
		actualResponse = apiRequests.regProcPostRequest(prop.getProperty("decisionApi"),actualRequest,MediaType.APPLICATION_JSON,validToken);
		outerKeys.add("responsetime");
		boolean assertStatus=AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
		//clearFromDB(registrationID);
		Assert.assertTrue(assertStatus);

		try {
		}catch(AssertionError err) {
			err.printStackTrace();
		}
	}
}

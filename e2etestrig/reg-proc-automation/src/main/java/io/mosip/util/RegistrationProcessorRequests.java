package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import io.mosip.dto.RegistrationPacketSyncDTO;
import io.mosip.entity.TokenGenerationEntity;
import io.mosip.service.BaseTest;
import io.restassured.response.Response;

public class RegistrationProcessorRequests extends BaseTest {
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	String token="";
	Properties prop=new Properties();
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	EncryptData encryptData=new EncryptData();
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	public boolean syncRequest(File packet) {
		RegistrationPacketSyncDTO registrationPacketSyncDto=null;
		token=getToken("syncTokenGenerationFilePath");
		try {
			registrationPacketSyncDto = encryptData.createSyncRequest(packet);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();
		JSONObject requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);
		String center_machine_refID=regId.substring(0,5)+"_"+regId.substring(5, 10);
		Response resp=apiRequests.postRequestToDecrypt(ApplnURI+encrypterURL,requestToEncrypt,MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,token);
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
		Response actualResponse = apiRequests.regProcSyncRequest(ApplnURI+prop.getProperty("syncListApi"),encryptedData,center_machine_refID,
				timeStamp.toString()+"Z",MediaType.APPLICATION_JSON,token);
		int status=actualResponse.statusCode();
		try {
		Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.sync"));
		Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
		Assert.assertTrue(actualResponse.jsonPath().get("response[0].status").equals("SUCCESS"));
		Assert.assertTrue(actualResponse.jsonPath().get("response[0].registrationId").equals(regId));
		return true;
		}catch (NullPointerException|AssertionError|IllegalArgumentException e) {
			return false;
		}
	}
	public boolean UploadPacket(File packet) throws ParseException, FileNotFoundException, IOException {
		
		Response actualResponse=apiRequests.regProcPacketUpload(packet,ApplnURI+prop.getProperty("packetReceiverApi"),token);
		System.out.println(actualResponse.asString());
		try {
		Assert.assertTrue(actualResponse.jsonPath().get("id").equals("mosip.registration.sync"));
		Assert.assertTrue(actualResponse.jsonPath().get("version").equals("1.0"));
		Assert.assertTrue(actualResponse.jsonPath().get("response.status").equals("Packet is in PACKET_RECEIVED status"));
		return true;
		}catch (AssertionError|IllegalArgumentException|NullPointerException e) {
		return false;
		}
	}
	
	public void checkDb(String regId) {
		
	}
}

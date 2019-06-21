package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.IInvokedMethod;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dbaccess.RegProcDBCleanUp;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.BaseTestCase;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

public class SecurityTests extends BaseTestCase implements ITest{
	protected static String testCaseName = "";

	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	StageValidationMethods apiRequest=new StageValidationMethods();
	Properties api=new Properties();
	EncryptData encryptData=new EncryptData();
	static String moduleName="RegProc";
	static String apiName="SecurityTests";
	RegistrationPacketSyncDTO registrationPacketSyncDto=null;
	JSONObject requestToEncrypt=null;
	JSONObject getRequest=null;
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	File packet=null;
	String regId="";
	String centre_machine_refId="";
	String validToken="";
	String adminAuthToken="";
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	@BeforeClass
	public void getValidPacketPath() {
		validToken=getToken("syncTokenGenerationFilePath");
		adminAuthToken=getToken("getStatusTokenGenerationFilePath");
		String propertyFilePath=System.getProperty("user.dir")+"/"+"src/config/registrationProcessorAPI.properties";
		FileReader apiReader=null;
		Properties folderPath = new Properties();
		try {
			apiReader=new FileReader(new File(propertyFilePath));
			api.load(apiReader);
			FileReader reader=new FileReader(new File(System.getProperty("user.dir") + "/src/config/folderPaths.properties"));
			folderPath.load(reader);
			reader.close();
			apiReader.close();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File file=new File(System.getProperty("user.dir")+"/"+folderPath.getProperty("pathForValidIntegration"));
		File[] listOfFiles=file.listFiles();
		for(File f:listOfFiles) {
			if(f.getName().contains(".zip")) {
				packet=f;
				try {
					registrationPacketSyncDto=encryptData.createSyncRequest(f,"NEW");
					regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();
					centre_machine_refId=regId.substring(0,5)+"_"+regId.substring(5, 10);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);
			} else if(f.getName().equals("GetStatus")) {
				for(File request:f.listFiles()) {
					if(request.getName().toLowerCase().contains("request")) {
						try {
							getRequest = (JSONObject) new JSONParser().parse(new FileReader(request.getPath()));
						} catch (IOException | org.json.simple.parser.ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	@Test(priority=1)
	public void syncRequestWithValidToken() {
	
	
		Response res=apiRequests.postRequestToDecrypt(encrypterURL, requestToEncrypt, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, validToken);
		String encryptedData = res.jsonPath().get("response.data").toString();
		LocalDateTime timeStamp=null;
		try {
			timeStamp = encryptData.getTime(regId);
		} catch (ParseException | NullPointerException |IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=apiRequests.regProcSyncRequest(api.getProperty("syncListApi"), encryptedData, centre_machine_refId, timeStamp.toString()+"Z", MediaType.APPLICATION_JSON, validToken);
		
		Assert.assertTrue(response.jsonPath().get("response[0].status").equals("SUCCESS"));
	}
	@Test (priority=2)
	public void syncRequestWithInvalidToken() {
		
		Response res=apiRequests.postRequestToDecrypt(encrypterURL, requestToEncrypt, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, validToken);
		String encryptedData = res.jsonPath().get("response.data").toString();
		LocalDateTime timeStamp=null;
		try {
			timeStamp = encryptData.getTime(regId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=apiRequests.regProcSyncRequest(api.getProperty("syncListApi"), encryptedData, centre_machine_refId, timeStamp.toString()+"Z", MediaType.APPLICATION_JSON, validToken+"ABC");
		
		Assert.assertTrue(response.jsonPath().get("errors[0].errorMessage").equals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted."));
	}
	@Test(priority=3)
	public void packetUploadWithValidToken() {
	Response res=apiRequests.regProcPacketUpload(packet, api.getProperty("packetReceiverApi"), validToken);
	//cleanUp.prepareQueryList(packet.getName().substring(0,packet.getName().lastIndexOf(".")));
	Assert.assertTrue(res.statusCode()==200);
	}
	@Test(priority=4)
	public void packetUploadWithInValidToken() {
		Response res=apiRequests.regProcPacketUpload(packet, api.getProperty("packetReceiverApi"), validToken+"ABC");
		
		Assert.assertTrue(res.jsonPath().get("errors[0].message").equals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted."));
	}
	@Test(priority=5)
	public void packetStatusWithValidToken() {
		Response res=apiRequests.regProcGetRequest(api.getProperty("packetStatusApi"),getRequest, adminAuthToken);
		System.out.println(res.asString());
	}
	@Test(priority=6)
	public void packetStatusWithInvalidToken() {
		Response res=apiRequests.regProcGetRequest(api.getProperty("packetStatusApi"),getRequest, authToken);
	}
/*	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(ITestContext ctx){
		String methodName=ctx.getName();
		testCaseName =moduleName+"_"+apiName+"_"+ methodName;
	}*/

	/**
	 * This method is used for generating report
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		
		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			testCaseName =moduleName+"_"+apiName+"_"+ baseTestMethod.getMethodName();
			f.setAccessible(true);
			f.set(baseTestMethod, SecurityTests.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method "+e);
		}
	}
	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}

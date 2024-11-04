package io.mosip.testrig.apirig.auth.testscripts;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.testrig.apirig.auth.utils.IdAuthConfigManager;
import io.mosip.testrig.apirig.auth.utils.IdAuthenticationUtil;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.EncryptionDecrptionUtil;
import io.mosip.testrig.apirig.utils.FileUtil;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.restassured.response.Response;

public class OtpAuthNew extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(OtpAuthNew.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;
	
	private EncryptionDecrptionUtil encryptDecryptUtil = new EncryptionDecrptionUtil();

	@BeforeClass
	public static void setLogLevel() {
		if (IdAuthConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	/**
	 * get current testcaseName
	 */
	@Override
	public String getTestName() {
		return testCaseName;
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		isInternal = Boolean.parseBoolean(context.getCurrentXmlTest().getLocalParameters().get("isInternal"));
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}
	
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {		
		testCaseName = testCaseDTO.getTestCaseName(); 
		testCaseName = IdAuthenticationUtil.isTestCaseValidForExecution(testCaseDTO);
		
		String partnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/" + PartnerRegistration.apiKey;
		String ekycPartnerKeyURL = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.ekycPartnerId + "/" + PartnerRegistration.kycApiKey;
		
		if(testCaseDTO.getEndPoint().contains("$partnerKeyURL$"))
			
		{
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$partnerKeyURL$", partnerKeyUrl));
			PartnerRegistration.appendEkycOrRp = "rp-";
		}
		if(testCaseDTO.getEndPoint().contains("$ekycPartnerKeyURL$"))
		{
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$ekycPartnerKeyURL$", ekycPartnerKeyURL));
			PartnerRegistration.appendEkycOrRp = "ekyc-";
		}
		
		if (testCaseDTO.getEndPoint().contains("$UpdatedPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$UpdatedPartnerKeyURL$",
					PartnerRegistration.updatedpartnerKeyUrl));
		}
		JSONObject req = new JSONObject(testCaseDTO.getInput());
		String otpChannel="";
		String otpRequest = null, sendOtpReqTemplate = null, sendOtpEndPoint = null, otpIdentyEnryptRequestPath = null;
		if(req.has("otpChannel")) {
			 otpChannel = req.get("otpChannel").toString();
		}
		if(req.has("sendOtp")) {
			otpRequest = req.get("sendOtp").toString();
			req.remove("sendOtp");
		}
		JSONObject otpReqJson = new JSONObject(otpRequest);
		sendOtpReqTemplate = otpReqJson.getString("sendOtpReqTemplate");
		otpReqJson.remove("sendOtpReqTemplate");
		sendOtpEndPoint = otpReqJson.getString("sendOtpEndPoint");
		
		
		otpReqJson.remove("sendOtpEndPoint");
		otpIdentyEnryptRequestPath = otpReqJson.getString("otpIdentyEnryptRequestPath");
		otpReqJson.remove("otpIdentyEnryptRequestPath");
		
		if(sendOtpEndPoint.contains("$partnerKeyURL$"))
		{
			sendOtpEndPoint= sendOtpEndPoint.replace("$partnerKeyURL$", partnerKeyUrl);
		}
		if(sendOtpEndPoint.contains("$ekycPartnerKeyURL$"))
		{
			sendOtpEndPoint= sendOtpEndPoint.replace("$ekycPartnerKeyURL$", ekycPartnerKeyURL);
		}
		
		Response otpResponse = null;
		if(isInternal)
			otpResponse = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + sendOtpEndPoint, getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), COOKIENAME, "resident", testCaseDTO.getTestCaseName());
		else
			otpResponse = postRequestWithAuthHeaderAndSignature(ApplnURI + sendOtpEndPoint, getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), testCaseDTO.getTestCaseName());
		
		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null, sendOtpResTemplate = null;
		if(res.has("sendOtpResp")) {
			sendOtpResp = res.get("sendOtpResp").toString();
			res.remove("sendOtpResp");
		}
		JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
		sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
		sendOtpRespJson.remove("sendOtpResTemplate");
		Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil
				.doJsonOutputValidation(otpResponse.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate));
		//Need to uncomment line 126 to 129
		//Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));
		
		//if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
			//throw new AdminTestException("Failed at Send OTP output validation");
		
		//String id = getAutoGeneratedFieldValue(otpRequest, testCaseName);
		
		String otpIdentyEnryptRequest = FileUtil.readInput(getResourcePath()+otpIdentyEnryptRequestPath);
		otpChannel = inputJsonKeyWordHandeler(otpChannel, testCaseName);
		otpIdentyEnryptRequest = updateTimestampOtp(otpIdentyEnryptRequest, otpChannel, testCaseName);
		
		Map<String, String> bioAuthTempMap = (isInternal)
				? encryptDecryptUtil.getInternalEncryptSessionKeyValue(otpIdentyEnryptRequest)
				: encryptDecryptUtil.getEncryptSessionKeyValue(otpIdentyEnryptRequest);
		String authRequest = getJsonFromTemplate(req.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of OTP auth request ******************");
		Reporter.log("<b><u>Modification of otp auth request</u></b>");
//		System.out.println("authRequest = " + authRequest);
		authRequest = modifyRequest(authRequest, bioAuthTempMap, getResourcePath()+props.getProperty("idaMappingPath"));
//		System.out.println("modifyRequest = " + authRequest);
		testCaseDTO.setInput(authRequest);
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);
				
		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");		
		
		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest, COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
		

	}

	/**
	 * The method ser current test name to result
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public static void authTestTearDown() {
		logger.info("Terminating authpartner demo application...");
	}
}

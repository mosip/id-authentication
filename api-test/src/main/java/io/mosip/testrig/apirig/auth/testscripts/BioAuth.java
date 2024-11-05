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
import io.mosip.testrig.apirig.utils.BioDataUtility;
import io.mosip.testrig.apirig.utils.EncryptionDecrptionUtil;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.restassured.response.Response;

public class BioAuth extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(BioAuth.class);
	protected String testCaseName = "";
	public Response response = null;
	public Response newResponse = null;
	public boolean isInternal = false;
	
	private EncryptionDecrptionUtil encryptDecryptUtil = new EncryptionDecrptionUtil();
	private BioDataUtility bioDataUtil = new BioDataUtility();

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
//		String ekycPartnerId = getPartnerIdFromPartnerURL(ekycPartnerKeyUrl);
//		String mispLicKey ="";
//		String kycApiKey = KeyCloakUserAndAPIKeyGeneration.createKCUserAndGetAPIKeyForKyc();
//		mispLicKey = MispPartnerAndLicenseKeyGeneration.getAndUploadCertificatesAndGenerateMispLicKey();
		
	
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
		JSONObject request = new JSONObject(testCaseDTO.getInput());
		String identityRequest = null, identityRequestTemplate = null, identityRequestEncUrl = null;
		if (request.has("identityRequest")) {
			identityRequest = request.get("identityRequest").toString();
			request.remove("identityRequest");
		}
		identityRequest = buildIdentityRequest(identityRequest);
		
		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequestEncUrl = identityReqJson.getString("identityRequestEncUrl");
		identityReqJson.remove("identityRequestEncUrl");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);

		String encryptedIdentityReq = null;
		try {
			encryptedIdentityReq = bioDataUtil.constractBioIdentityRequest(identityRequest,
					getResourcePath() + props.getProperty("bioValueEncryptionTemplate"), testCaseName, isInternal);
			System.out.println("encryptedIdentityReq = " + encryptedIdentityReq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> bioAuthTempMap = (isInternal)
				? encryptDecryptUtil.getInternalEncryptSessionKeyValue(encryptedIdentityReq)
				: encryptDecryptUtil.getEncryptSessionKeyValue(encryptedIdentityReq);
		// storeValue(bioAuthTempMap);
		String authRequest = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of bio auth request ******************");
		Reporter.log("<b><u>Modification of bio auth request</u></b>");
		
		logger.info("authRequest is = " + authRequest);
		logger.info("bioAuthTempMap is = " + bioAuthTempMap);
		authRequest = modifyRequest(authRequest, bioAuthTempMap,
				getResourcePath() + props.getProperty("idaMappingPath"));
		logger.info("authRequestTemp is = " + authRequest);
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);
		// storeValue(authRequest,"authRequest");

		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");

		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest,
				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
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
	}
}

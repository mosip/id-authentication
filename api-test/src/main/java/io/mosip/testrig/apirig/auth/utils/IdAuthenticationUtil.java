package io.mosip.testrig.apirig.auth.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.testng.SkipException;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.JWKKeyUtil;
import io.mosip.testrig.apirig.utils.KeycloakUserManager;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.SkipTestCaseHandler;

public class IdAuthenticationUtil extends AdminTestUtil {

	private static final Logger logger = Logger.getLogger(IdAuthenticationUtil.class);
	public static String genRid1 = "27847" + generateRandomNumberString(10);
	public static String randomString = generateRandomNumberString(6) + generateRandomNumberString(3);

	public static void setLogLevel() {
		if (IdAuthConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	public static String isTestCaseValidForExecution(TestCaseDTO testCaseDTO) {
		String testCaseName = testCaseDTO.getTestCaseName();
		
		int indexof = testCaseName.indexOf("_");
		String modifiedTestCaseName = testCaseName.substring(indexof + 1);

		addTestCaseDetailsToMap(modifiedTestCaseName, testCaseDTO.getUniqueIdentifier());

		if (MosipTestRunner.skipAll == true) {
			throw new SkipException(GlobalConstants.PRE_REQUISITE_FAILED_MESSAGE);
		}

		if (SkipTestCaseHandler.isTestCaseInSkippedList(testCaseName)) {
			throw new SkipException(GlobalConstants.KNOWN_ISSUES);
		}
		
		JSONArray individualBiometricsArray = new JSONArray(
				getValueFromAuthActuator("json-property", "individualBiometrics"));
		
		String individualBiometrics = individualBiometricsArray.getString(0);

		if (testCaseName.startsWith("auth_")
				&& (testCaseName.contains("_BioAuth_") || testCaseName.contains("_EkycBio_")
						|| testCaseName.contains("_MultiFactorAuth_") || testCaseName.contains("_DemoAuth")
						|| testCaseName.contains("_EkycDemo_"))
				&& (!isElementPresent(globalRequiredFields, individualBiometrics))) {
			throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
		} else if (testCaseName.startsWith("auth_")
				&& ((testCaseName.contains("_DeactivateUINs_")) || (testCaseName.contains("PublishDraft_")))
				&& (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
						&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid"))) {
			throw new SkipException(GlobalConstants.VID_FEATURE_NOT_SUPPORTED);
		} else if (testCaseName.startsWith("auth_")
				&& (testCaseName.contains("_AuthLock_") || testCaseName.contains("_AuthUnLock_"))
				&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.RESIDENT))) {
			throw new SkipException(GlobalConstants.SERVICE_NOT_DEPLOYED_MESSAGE);
		} else if (testCaseName.startsWith("auth_")
				&& (testCaseName.contains("_BlockHotlistAPI_") || testCaseName.contains("_HotlistAPI_")
						|| testCaseName.contains("_BlockPartnerId_")
						|| testCaseName.contains("_OTP_Auth_With_blocked_misp_Pos")
						|| testCaseName.contains("_OTP_Auth_With_blocked_partnerid_Pos"))
				&& (ConfigManager.isInServiceNotDeployedList(GlobalConstants.HOTLIST))) {
			throw new SkipException(GlobalConstants.SERVICE_NOT_DEPLOYED_MESSAGE);
		}

		return testCaseName;
	}
	
	protected static final String OIDCJWK3 = "oidcJWK3";
	protected static boolean triggerESignetKeyGen12 = true;

	private static void settriggerESignetKeyGen12(boolean value) {
		triggerESignetKeyGen12 = value;
	}

	private static boolean gettriggerESignetKeyGen12() {
		return triggerESignetKeyGen12;
	}
	
	public static String inputStringKeyWordHandeler(String jsonString, String testCaseName) {
		
		
		if (jsonString.contains(GlobalConstants.TIMESTAMP)) {
			jsonString = replaceKeywordValue(jsonString, GlobalConstants.TIMESTAMP, generateCurrentUTCTimeStamp());
		}
		
		if (jsonString.contains("$DATETIME$")) {
			jsonString = replaceKeywordValue(jsonString, "$DATETIME$", generateCurrentUTCTimeStamp());
		}
		
		if (jsonString.contains(IDAConstants.MODULENAME)) {
			jsonString = replaceKeywordWithValue(jsonString, IDAConstants.MODULENAME, BaseTestCase.certsForModule);
		}
		
		if (jsonString.contains("$POLICYID_FOR_DELEGATED$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$POLICYID_FOR_DELEGATED$", policyId);
		}
		
		if (jsonString.contains("$PARTNER_ID_FOR_DELEGATED$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$PARTNER_ID_FOR_DELEGATED$", PartnerRegistration.partnerId);
		}
		
		if (jsonString.contains(IDAConstants.TRANSACTION_ID))
			jsonString = replaceKeywordWithValue(jsonString, IDAConstants.TRANSACTION_ID, TRANSACTION_ID);
		
		if (jsonString.contains("$RID1$")) {
			jsonString = replaceKeywordValue(jsonString, "$RID1$", genRid1);
		}
		
		if (testCaseName.contains("auth_GenerateApiKey_")) {
			KeycloakUserManager.createKeyCloakUsers(genPartnerName, genPartnerEmail, "AUTH_PARTNER");
		}

		if (jsonString.contains("$ID:")) {
			jsonString = replaceIdWithAutogeneratedId(jsonString, "$ID:");
		}
		
		if (jsonString.contains("$IDPREDIRECTURI$")) {
			jsonString = replaceKeywordValue(jsonString, "$IDPREDIRECTURI$",
					ApplnURI.replace(IDAConstants.API_INTERNAL, "healthservices") + "/userprofile");
		}

		if (jsonString.contains("$OIDCJWKKEY3$")) {
			String jwkKey = "";
			if (gettriggerESignetKeyGen12()) {
				jwkKey = JWKKeyUtil.generateAndCacheJWKKey(OIDCJWK3);
				settriggerESignetKeyGen12(false);
			} else {
				jwkKey = JWKKeyUtil.getJWKKey(OIDCJWK3);
			}
			jsonString = replaceKeywordValue(jsonString, "$OIDCJWKKEY3$", jwkKey);
		}
		
		return jsonString;
	}
	
	public static String sanitizeCertificateField(String json) {
	    String certField = "\"certData\": \"";
	    int certStart = json.indexOf(certField);
	    if (certStart == -1) return json; // certData not present

	    int startQuote = certStart + certField.length();
	    int endQuote = json.indexOf("\"", startQuote);

	    // Handle multiline certificate: find true closing quote
	    boolean escaped = false;
	    for (int i = startQuote; i < json.length(); i++) {
	        char c = json.charAt(i);
	        if (c == '\\') {
	            escaped = !escaped;
	        } else if (c == '"' && !escaped) {
	            endQuote = i;
	            break;
	        } else {
	            escaped = false;
	        }
	    }

	    String certValue = json.substring(startQuote, endQuote);

	    // Escape newlines inside certData
	    String escapedCertValue = certValue.replace("\n", "\\n").replace("\r", "");

	    // Replace the original certData with escaped one
	    return json.substring(0, startQuote) + escapedCertValue + json.substring(endQuote);
	}
	
	public static String replaceKeywordValue(String jsonString, String keyword, String value) {
		if (value != null && !value.isEmpty())
			return jsonString.replace(keyword, value);
		else {
			if (keyword.contains("$ID:"))
				throw new SkipException("Marking testcase as skipped as required field is empty " + keyword
						+ " please check the results of testcase: " + getTestCaseIDFromKeyword(keyword));
			else
				throw new SkipException("Marking testcase as skipped as required field is empty " + keyword);

		}
	}
	
}
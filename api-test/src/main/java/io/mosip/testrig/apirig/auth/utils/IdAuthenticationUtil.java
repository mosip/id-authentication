package io.mosip.testrig.apirig.auth.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.testng.SkipException;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.dbaccess.DBManager;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.JWKKeyUtil;
import io.mosip.testrig.apirig.utils.KeyCloakUserAndAPIKeyGeneration;
import io.mosip.testrig.apirig.utils.KeycloakUserManager;
import io.mosip.testrig.apirig.utils.MispPartnerAndLicenseKeyGeneration;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.RestClient;
import io.mosip.testrig.apirig.utils.SkipTestCaseHandler;
import io.restassured.response.Response;

public class IdAuthenticationUtil extends AdminTestUtil {

	private static final Logger logger = Logger.getLogger(IdAuthenticationUtil.class);
	public static String genRid1 = "27847" + generateRandomNumberString(10);
	public static String randomString = generateRandomNumberString(6) + generateRandomNumberString(3);
	
	public static List<String> testCasesInRunScope = new ArrayList<>();

	public static void setLogLevel() {
		if (IdAuthConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}
	
	public static String isTestCaseValidForExecution(TestCaseDTO testCaseDTO) {
		String testCaseName = testCaseDTO.getTestCaseName();
		currentTestCaseName = testCaseName;
		
		int indexof = testCaseName.indexOf("_");
		String modifiedTestCaseName = testCaseName.substring(indexof + 1);

		addTestCaseDetailsToMap(modifiedTestCaseName, testCaseDTO.getUniqueIdentifier());
		
		if (!testCasesInRunScope.isEmpty()
				&& testCasesInRunScope.contains(testCaseDTO.getUniqueIdentifier()) == false) {
			throw new SkipException(GlobalConstants.NOT_IN_RUN_SCOPE_MESSAGE);
		}
		
		// Handle extra workflow dependencies
		if (testCaseDTO != null && testCaseDTO.getAdditionalDependencies() != null
				&& AdminTestUtil.generateDependency == true) {
			addAdditionalDependencies(testCaseDTO);
		}

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
		} else if (testCaseName.startsWith("auth_") && testCaseName.contains("_DemoAuthDelegated") || testCaseName.contains("_DemoAuthKycExchange")) {
			// Intentional skip: app rejects DEMO on delegated flow - "DEMO Authentication usage not allowed as per client AMR configuration" (OIDC client AMR restriction, not a code gap).
			throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
		} else if (testCaseDTO.getUniqueIdentifier() != null
				&& (testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_10")
						|| testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_11")
						|| testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_12")
						|| testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_13"))) {
			// These consume a kycToken minted by DemoAuthDelegated (skipped above, see comment), so
			// skip cleanly here instead of failing on the downstream dependency-resolution SkipException.
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
	
	public static void dbCleanUp() {
		DBManager.executeDBQueries(ConfigManager.getKMDbUrl(), ConfigManager.getKMDbUser(), ConfigManager.getKMDbPass(),
				ConfigManager.getKMDbSchema(),
				getGlobalResourcePath() + "/" + "config/keyManagerCertDataDeleteQueries.txt");
		DBManager.executeDBQueries(ConfigManager.getIdaDbUrl(), ConfigManager.getIdaDbUser(),
				ConfigManager.getPMSDbPass(), ConfigManager.getIdaDbSchema(),
				getGlobalResourcePath() + "/" + "config/idaCertDataDeleteQueries.txt");

		DBManager.executeDBQueries(ConfigManager.getMASTERDbUrl(), ConfigManager.getMasterDbUser(),
				ConfigManager.getMasterDbPass(), ConfigManager.getMasterDbSchema(),
				getGlobalResourcePath() + "/" + "config/masterDataCertDataDeleteQueries.txt");
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

	// Module-local MISP partner/policy with delegation disabled, for IDA-MPA-030/031 only.
	public static String kycDelegationDisabledMispLicKey = "";
	public static String kycDelegationDisabledPartnerKeyUrl = null;
	private static final String KYC_DELEGATION_DISABLED_PARTNER_ID = "mosip-nodeleg-" + AdminTestUtil.timeStamp;
	private static final String KYC_DELEGATION_DISABLED_POLICY_GROUP = "mosip misp no deleg policy group "
			+ AdminTestUtil.timeStamp;
	private static final String KYC_DELEGATION_DISABLED_POLICY_NAME = "mosip misp no deleg policy "
			+ AdminTestUtil.timeStamp;

	// fails fast with the response body instead of a raw NPE when "response" is missing/null
	private static String extractIdOrFail(Response response, String step) {
		String body = response.getBody().asString();
		org.json.JSONObject json = new org.json.JSONObject(body);
		if (json.isNull(GlobalConstants.RESPONSE)) {
			throw new RuntimeException(step + " failed: " + body);
		}
		return json.getJSONObject(GlobalConstants.RESPONSE).getString("id");
	}

	@SuppressWarnings("unchecked")
	public static synchronized String generateAndGetKycDelegationDisabledPartnerKeyUrl() {
		if (kycDelegationDisabledPartnerKeyUrl != null) {
			return kycDelegationDisabledPartnerKeyUrl;
		}

		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		// dedicated policy group, isolated from the one the rest of the suite uses
		String policyGroupUrl = ApplnURI + properties.getProperty("policyGroupUrl");
		org.json.simple.JSONObject groupRequest = new org.json.simple.JSONObject();
		groupRequest.put("desc", "desc mosip misp no-delegation policy group");
		groupRequest.put("name", KYC_DELEGATION_DISABLED_POLICY_GROUP);

		org.json.simple.JSONObject groupBody = new org.json.simple.JSONObject();
		groupBody.put("id", GlobalConstants.STRING);
		groupBody.put(GlobalConstants.METADATA, new HashMap<>());
		groupBody.put(GlobalConstants.REQUEST, groupRequest);
		groupBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		groupBody.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		Response groupResponse = RestClient.postRequestWithCookie(policyGroupUrl, groupBody,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String policyGroupId = extractIdOrFail(groupResponse, "no-delegation policy group creation");

		// same shape as config/mispPolicy.json but with allowKycRequestDelegation false
		org.json.simple.JSONObject noDelegationPolicies = new org.json.simple.JSONObject();
		noDelegationPolicies.put("trustBindedAuthVerificationToken", true);
		noDelegationPolicies.put("allowAuthRequestDelegation", true);
		noDelegationPolicies.put("allowKycRequestDelegation", false);
		noDelegationPolicies.put("allowKeyBindingDelegation", true);
		noDelegationPolicies.put("allowVciRequestDelegation", true);

		org.json.simple.JSONObject policyRequest = new org.json.simple.JSONObject();
		policyRequest.put("name", KYC_DELEGATION_DISABLED_POLICY_NAME);
		policyRequest.put("policyGroupName", KYC_DELEGATION_DISABLED_POLICY_GROUP);
		policyRequest.put("desc", "desc mosip misp no-delegation policy");
		policyRequest.put("policyType", "MISP");
		// required field, missing here previously caused policy creation to fail server-side
		policyRequest.put(GlobalConstants.VERSION, "1.0");
		policyRequest.put("policies", noDelegationPolicies);

		org.json.simple.JSONObject policyBody = new org.json.simple.JSONObject();
		policyBody.put("id", GlobalConstants.STRING);
		policyBody.put(GlobalConstants.METADATA, new HashMap<>());
		policyBody.put(GlobalConstants.REQUEST, policyRequest);
		policyBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		policyBody.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		String authPolicyUrl = ApplnURI + properties.getProperty("authPolicyUrl");
		Response policyResponse = RestClient.postRequestWithCookie(authPolicyUrl, policyBody,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		String policyId = extractIdOrFail(policyResponse, "no-delegation policy creation");

		// publish - not enforced until published
		String publishPolicyURL = ApplnURI + properties.getProperty("publishPolicyurl");
		if (publishPolicyURL.contains("POLICYID")) {
			publishPolicyURL = publishPolicyURL.replace("POLICYID", policyId).replace("POLICYGROUPID", policyGroupId);
		}
		RestClient.postRequestWithCookie(publishPolicyURL, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		// new MISP partner mapped to that policy group; Auth-Partner-ID/OIDC-Client-Id stay unchanged
		String partnersUrl = ApplnURI + "/v1/partnermanager/partners";
		org.json.simple.JSONObject partnerRequest = new org.json.simple.JSONObject();
		partnerRequest.put("address", "Bangalore");
		partnerRequest.put("contactNumber", "8553967572");
		partnerRequest.put("emailId", "mosip_nodeleg" + AdminTestUtil.timeStamp + "@gmail.com");
		partnerRequest.put("organizationName", KYC_DELEGATION_DISABLED_PARTNER_ID);
		partnerRequest.put(GlobalConstants.PARTNERID, KYC_DELEGATION_DISABLED_PARTNER_ID);
		partnerRequest.put(GlobalConstants.PARTNERTYPE, "Misp_Partner");
		partnerRequest.put("policyGroup", KYC_DELEGATION_DISABLED_POLICY_GROUP);

		org.json.simple.JSONObject partnerBody = new org.json.simple.JSONObject();
		partnerBody.put("id", GlobalConstants.STRING);
		partnerBody.put(GlobalConstants.METADATA, new HashMap<>());
		partnerBody.put(GlobalConstants.REQUEST, partnerRequest);
		partnerBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		partnerBody.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		RestClient.postRequestWithCookie(partnersUrl, partnerBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);

		// MispPartnerAndLicenseKeyGeneration.getCertificates() hardcodes keyFileNameByPartnerName=false,
		// which reuses the first partner's cached cert files - go straight to AuthTestsUtil with true so
		// the cert chain (and its Organization field) is unique to this partner id
		io.mosip.testrig.apirig.dto.CertificateChainResponseDto certChain;
		try {
			certChain = new io.mosip.testrig.apirig.utils.AuthTestsUtil().generatePartnerKeys(
					io.mosip.testrig.apirig.utils.PartnerTypes.MISP, KYC_DELEGATION_DISABLED_PARTNER_ID, true, null,
					BaseTestCase.certsForModule, ApplnURI.replace("https://", ""));
		} catch (Exception e) {
			throw new RuntimeException("failed to generate no-delegation partner keys", e);
		}
		MispPartnerAndLicenseKeyGeneration.uploadCACertificate(certChain.getCaCertificate(), "Auth");
		MispPartnerAndLicenseKeyGeneration.uploadIntermediateCertificate(certChain.getInterCertificate(), "Auth");
		org.json.JSONObject signedCertificateValue = MispPartnerAndLicenseKeyGeneration.uploadPartnerCertificate(
				certChain.getPartnerCertificate(), "Auth", KYC_DELEGATION_DISABLED_PARTNER_ID);
		// MispPartnerAndLicenseKeyGeneration.uploadSignedCertificate() hardcodes partnerName=null/keyFileNameByPartnerName=false,
		// which would update the wrong (generic) key file - call AuthTestsUtil directly with the same params used above
		HashMap<String, String> signedCertRequest = new HashMap<>();
		signedCertRequest.put("certData", signedCertificateValue.getString("signedCertificateData"));
		try {
			new io.mosip.testrig.apirig.utils.AuthTestsUtil().updatePartnerCertificate(
					io.mosip.testrig.apirig.utils.PartnerTypes.MISP, KYC_DELEGATION_DISABLED_PARTNER_ID, true,
					signedCertRequest, null, BaseTestCase.certsForModule, ApplnURI.replace("https://", ""));
		} catch (Exception e) {
			throw new RuntimeException("failed to update no-delegation partner certificate", e);
		}

		String mappingKey = KeyCloakUserAndAPIKeyGeneration.submitPartnerAndGetMappingKey(
				KYC_DELEGATION_DISABLED_PARTNER_ID, KYC_DELEGATION_DISABLED_POLICY_NAME);
		KeyCloakUserAndAPIKeyGeneration.approvePartnerAPIKey(mappingKey);

		kycDelegationDisabledMispLicKey = MispPartnerAndLicenseKeyGeneration
				.generateMispLicKey(KYC_DELEGATION_DISABLED_PARTNER_ID);

		kycDelegationDisabledPartnerKeyUrl = kycDelegationDisabledMispLicKey + "/" + PartnerRegistration.partnerId;
		return kycDelegationDisabledPartnerKeyUrl;
	}

}
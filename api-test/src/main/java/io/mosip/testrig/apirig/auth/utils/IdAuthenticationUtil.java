package io.mosip.testrig.apirig.auth.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.SkipException;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.dbaccess.DBManager;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.CryptoCoreUtil;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.JWKKeyUtil;
import io.mosip.testrig.apirig.utils.KernelAuthentication;
import io.mosip.testrig.apirig.utils.KeyCloakUserAndAPIKeyGeneration;
import io.mosip.testrig.apirig.utils.KeyMgrUtility;
import io.mosip.testrig.apirig.utils.KeycloakUserManager;
import io.mosip.testrig.apirig.utils.MispPartnerAndLicenseKeyGeneration;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.PartnerTypes;
import io.mosip.testrig.apirig.utils.RestClient;
import io.mosip.testrig.apirig.utils.SecurityXSSException;
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

	// Skip the 180s OTP poll when otpChannel isn't an email (e.g. negative
	// tests that pass a bare number) - it can never receive a notification.
	@Override
	public String updateTimestampOtp(String otpIdentyEnryptRequest, String otpChannel, String testCaseName) {
		if (otpChannel == null || !otpChannel.contains("@")) {
			logger.warn("otpChannel '" + otpChannel + "' is not email-shaped for " + testCaseName
					+ " - skipping OTP notification poll, using empty otp");
			otpIdentyEnryptRequest = JsonPrecondtion.parseAndReturnJsonContent(otpIdentyEnryptRequest,
					generateCurrentUTCTimeStamp(), "timestamp");
			return otpIdentyEnryptRequest;
		}
		return super.updateTimestampOtp(otpIdentyEnryptRequest, otpChannel, testCaseName);
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
			// Intentional: app rejects DEMO on delegated flow (IDA-MPA-029), not a bug.
			throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
		} else if (testCaseDTO.getUniqueIdentifier() != null
				&& (testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_10")
						|| testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_11")
						|| testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_12")
						|| testCaseDTO.getUniqueIdentifier().equals("TC_IDA_KycExchangeNeg_13"))) {
			// Depend on DemoAuthDelegated's kycToken (skipped above); skip these too.
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

	// For policy/partner-setup responses only - fails with the body if "id" is missing.
	private static String extractIdOrFail(Response response, String step) {
		String body = response.getBody().asString();
		org.json.JSONObject json = new org.json.JSONObject(body);
		if (json.isNull(GlobalConstants.RESPONSE)
				|| !json.getJSONObject(GlobalConstants.RESPONSE).has("id")) {
			throw new RuntimeException(step + " failed: " + body);
		}
		return json.getJSONObject(GlobalConstants.RESPONSE).getString("id");
	}

	// Cached so a retry rethrows the original cause instead of re-registering fixed names.
	private static RuntimeException kycDelegationDisabledSetupFailure = null;

	public static synchronized String generateAndGetKycDelegationDisabledPartnerKeyUrl() {
		if (kycDelegationDisabledPartnerKeyUrl != null) {
			return kycDelegationDisabledPartnerKeyUrl;
		}
		if (kycDelegationDisabledSetupFailure != null) {
			throw kycDelegationDisabledSetupFailure;
		}
		try {
			return createKycDelegationDisabledPartner();
		} catch (RuntimeException e) {
			kycDelegationDisabledSetupFailure = e;
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private static String createKycDelegationDisabledPartner() {
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
		Response publishResponse = RestClient.postRequestWithCookie(publishPolicyURL, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		assertSuccessStatusCode(publishResponse, "Failed to publish no-delegation policy");

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

		Response partnerResponse = RestClient.postRequestWithCookie(partnersUrl, partnerBody,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		assertSuccessStatusCode(partnerResponse, "Failed to register no-delegation partner");

		// getCertificates() would reuse the first partner's certs; call AuthTestsUtil directly instead.
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
		// uploadSignedCertificate() would update the wrong generic key file; use AuthTestsUtil directly.
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

		kycDelegationDisabledPartnerKeyUrl = kycDelegationDisabledMispLicKey + "/" + KYC_DELEGATION_DISABLED_PARTNER_ID;
		return kycDelegationDisabledPartnerKeyUrl;
	}

	// Skips cleanly instead of an opaque JSONException when a setup call fails.
	private static void assertSuccessStatusCode(Response response, String failureMessage) {
		if (response == null) {
			throw new SkipException(failureMessage + ": null response");
		}
		int statusCode = response.getStatusCode();
		if (statusCode < 200 || statusCode >= 300) {
			throw new SkipException(failureMessage + ": HTTP " + statusCode + " - " + response.asString());
		}
	}

	// Creates and publishes an auth policy under the main partner's policy group
	// using the given allowed-auth-types file, and returns the created policy id.
	@SuppressWarnings("unchecked")
	private static String createAndPublishPolicy(String policyNameToCreate, String attrFilePath) {
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		String url = ApplnURI + properties.getProperty("authPolicyUrl");
		org.json.simple.JSONObject actualrequestBody = getRequestJson(AUTH_POLICY_BODY);
		org.json.simple.JSONObject actualrequest2 = getRequestJson(AUTH_POLICY_REQUEST);
		org.json.simple.JSONObject actualrequestAttr = getRequestJson(attrFilePath);

		actualrequest2.put("name", policyNameToCreate);
		actualrequest2.put("policyGroupName", PartnerRegistration.policyGroup);
		actualrequest2.put("policies", actualrequestAttr);
		actualrequestBody.put(GlobalConstants.REQUEST, actualrequest2);
		actualrequestBody.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());

		Response response = RestClient.postRequestWithCookie(url, actualrequestBody, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		assertSuccessStatusCode(response, "Failed to create auth policy");
		String responseBody = response.getBody().asString();
		String createdPolicyId = new JSONObject(responseBody).getJSONObject(GlobalConstants.RESPONSE)
				.getString("id");

		String publishPolicyURL = ApplnURI + properties.getProperty("publishPolicyurl");
		if (publishPolicyURL.contains("POLICYID")) {
			publishPolicyURL = publishPolicyURL.replace("POLICYID", createdPolicyId);
			publishPolicyURL = publishPolicyURL.replace("POLICYGROUPID", policygroupId);
		}

		Response publishResponse = RestClient.postRequestWithCookie(publishPolicyURL, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		assertSuccessStatusCode(publishResponse, "Failed to publish auth policy");

		return createdPolicyId;
	}

	// API-key generation is partner self-service in PMS; a generic admin token
	// gets "User not authorized" (PMS_PRT_055), so log in as that partner instead.
	private static String loginAsPartner(String partnerId) {
		Map<String, String> kernelProps = AdminTestUtil.readProperty("Kernel");
		String authenticationInternalEndpoint = kernelProps.get("authenticationInternal");

		org.json.simple.JSONObject actualrequest = getRequestJson("config/Authorization/internalAuthRequest.json");
		org.json.simple.JSONObject request = new org.json.simple.JSONObject();
		request.put(GlobalConstants.APPID, ConfigManager.getPmsAppId());
		request.put(GlobalConstants.PASSWORD, GlobalConstants.MOSIP123);
		request.put(GlobalConstants.USER_NAME, partnerId);
		request.put(GlobalConstants.CLIENTID, ConfigManager.getPmsClientId());
		request.put(GlobalConstants.CLIENTSECRET, ConfigManager.getPmsClientSecret());
		actualrequest.put(GlobalConstants.REQUEST, request);

		Response response = AdminTestUtil.postWithJson(authenticationInternalEndpoint, actualrequest);
		assertSuccessStatusCode(response, "Failed to fetch admin token for partner " + partnerId);
		return new JSONObject(response.getBody().asString()).getJSONObject(GlobalConstants.RESPONSE)
				.getString(GlobalConstants.TOKEN);
	}

	// Maps partner to policy, approves the mapping, and generates the API key.
	// An unapproved mapping key alone gets IDA-MPA-009 "Partner is not registered".
	private static String mapPartnerToPolicyAndGenerateApiKey(String partnerId, String policyNameToUse) {
		KeycloakUserManager.createKeyCloakUsers(partnerId, partnerId + "@mosip.net", PartnerRegistration.partnerType);

		String mappingKey = KeyCloakUserAndAPIKeyGeneration.submitPartnerAndGetMappingKey(partnerId, policyNameToUse);
		KeyCloakUserAndAPIKeyGeneration.approvePartnerAPIKey(mappingKey);

		String url = ApplnURI + "/v1/partnermanager/partners/" + partnerId + "/generate/apikey";
		String token = loginAsPartner(partnerId);

		HashMap<String, String> requestBody = new HashMap<>();
		requestBody.put("policyName", policyNameToUse);
		requestBody.put("label", generateRandomAlphabeticString(4).toUpperCase());

		HashMap<String, Object> body = new HashMap<>();
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, GlobalConstants.STRING);

		Response response = RestClient.patchRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		assertSuccessStatusCode(response, "Failed to generate API key for partner " + partnerId);
		JSONObject responseValue = new JSONObject(response.asString()).getJSONObject(GlobalConstants.RESPONSE);

		return responseValue.getString(GlobalConstants.APIKEY);
	}

	private static final String AUTH_POLICY_FOR_POLICY_TOKEN_REQUEST_ATTR = "config/AuthPolicyForPolicyTokenAllowedAuthTypes.json";
	private static final String policyNameForPolicyToken = "mosip auth policy for policy token "
			+ BaseTestCase.runContext + System.currentTimeMillis();
	public static String policyIdForPolicyToken = "";
	public static String policyTokenPartnerKeyUrl = "";

	// authTokenType "policy" - authToken is derived from policy id + UIN, not partner id.
	public static void createAndPublishPolicyWithPolicyTokenType() {
		policyIdForPolicyToken = createAndPublishPolicy(policyNameForPolicyToken,
				AUTH_POLICY_FOR_POLICY_TOKEN_REQUEST_ATTR);
	}

	public static String generateAndGetPolicyTokenPartnerKeyUrl() {
		String policyTokenApiKey = mapPartnerToPolicyAndGenerateApiKey(PartnerRegistration.partnerId,
				policyNameForPolicyToken);
		policyTokenPartnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/"
				+ policyTokenApiKey;
		return policyTokenPartnerKeyUrl;
	}

	private static final String AUTH_POLICY_FOR_RANDOM_TOKEN_REQUEST_ATTR = "config/AuthPolicyForRandomTokenAllowedAuthTypes.json";
	private static final String policyNameForRandomToken = "mosip auth policy for random token "
			+ BaseTestCase.runContext + System.currentTimeMillis();
	public static String policyIdForRandomToken = "";
	public static String randomTokenPartnerKeyUrl = "";

	// authTokenType "random" - a new, unrelated authToken every call.
	public static void createAndPublishPolicyWithRandomTokenType() {
		policyIdForRandomToken = createAndPublishPolicy(policyNameForRandomToken,
				AUTH_POLICY_FOR_RANDOM_TOKEN_REQUEST_ATTR);
	}

	public static String generateAndGetRandomTokenPartnerKeyUrl() {
		String randomTokenApiKey = mapPartnerToPolicyAndGenerateApiKey(PartnerRegistration.partnerId,
				policyNameForRandomToken);
		randomTokenPartnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/"
				+ randomTokenApiKey;
		return randomTokenPartnerKeyUrl;
	}

	// Registers an independent partner under the main policy group, so it can be
	// mapped to the same policy-token policy and prove the token only depends
	// on policy id + UIN, not which partner called.
	private static void registerAdditionalPolicyTokenPartner(String partnerId, String organizationName,
			String emailId) {
		String url = ApplnURI + properties.getProperty("putPartnerRegistrationUrl");
		String token = kernelAuthLib.getTokenByRole(GlobalConstants.PARTNER);

		HashMap<String, String> requestBody = new HashMap<>();
		requestBody.put("address", "Bangalore");
		requestBody.put("contactNumber", "8553967572");
		requestBody.put("emailId", emailId);
		requestBody.put("organizationName", organizationName);
		requestBody.put(GlobalConstants.PARTNERID, partnerId);
		requestBody.put("partnerType", PartnerRegistration.partnerType);
		requestBody.put("policyGroup", PartnerRegistration.policyGroup);

		HashMap<String, Object> body = new HashMap<>();
		body.put("id", GlobalConstants.STRING);
		body.put(GlobalConstants.METADATA, new HashMap<>());
		body.put(GlobalConstants.REQUEST, requestBody);
		body.put(GlobalConstants.REQUESTTIME, generateCurrentUTCTimeStamp());
		body.put(GlobalConstants.VERSION, "LTS");

		Response response = RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION, token);
		assertSuccessStatusCode(response, "Failed to register partner " + partnerId);

		JSONObject certificateValue = PartnerRegistration.getCertificates(partnerId, "RELYING_PARTY");
		String caCertValue = certificateValue.getString("caCertificate");
		String interCertValue = certificateValue.getString("interCertificate");
		String partnerCertValue = certificateValue.getString("partnerCertificate");

		PartnerRegistration.uploadCACertificate(caCertValue, "Auth");
		PartnerRegistration.uploadIntermediateCertificate(interCertValue, "Auth");

		JSONObject signedcertificateValue = PartnerRegistration.uploadPartnerCertificate(partnerCertValue, "Auth",
				partnerId);
		String certValueSigned = signedcertificateValue.getString("signedCertificateData");

		PartnerRegistration.uploadSignedCertificate(certValueSigned, "RELYING_PARTY", partnerId, true);
	}

	// A partner can map to several policies (each mapping mints its own API key),
	// so the same partner is reused across the token-type tests.
	private static String mapPartnerToPolicyAndGetKeyUrl(String partnerId, String policyNameToUse) {
		String apiKey = mapPartnerToPolicyAndGenerateApiKey(partnerId, policyNameToUse);
		return PartnerRegistration.mispLicKey + "/" + partnerId + "/" + apiKey;
	}

	private static String mapToPolicyTokenPolicyAndGetKeyUrl(String partnerId) {
		return mapPartnerToPolicyAndGetKeyUrl(partnerId, policyNameForPolicyToken);
	}

	private static final String policyToken2OrganizationName = BaseTestCase.currentModule + "_policy2_pid";
	public static String policyToken2PartnerId = policyToken2OrganizationName;
	private static final String policyToken2EmailId = "mosip_policy2_" + System.currentTimeMillis() + "@gmail.com";
	public static String policyToken2PartnerKeyUrl = "";

	public static void registerSecondPolicyTokenPartner() {
		registerAdditionalPolicyTokenPartner(policyToken2PartnerId, policyToken2OrganizationName,
				policyToken2EmailId);
	}

	public static String generateAndGetSecondPolicyTokenPartnerKeyUrl() {
		policyToken2PartnerKeyUrl = mapToPolicyTokenPolicyAndGetKeyUrl(policyToken2PartnerId);
		return policyToken2PartnerKeyUrl;
	}

	private static final String policyToken3OrganizationName = BaseTestCase.currentModule + "_policy3_pid";
	public static String policyToken3PartnerId = policyToken3OrganizationName;
	private static final String policyToken3EmailId = "mosip_policy3_" + System.currentTimeMillis() + "@gmail.com";
	public static String policyToken3PartnerKeyUrl = "";

	// A third partner on the same policy-token policy, for a 3-way comparison.
	public static void registerThirdPolicyTokenPartner() {
		registerAdditionalPolicyTokenPartner(policyToken3PartnerId, policyToken3OrganizationName,
				policyToken3EmailId);
	}

	public static String generateAndGetThirdPolicyTokenPartnerKeyUrl() {
		policyToken3PartnerKeyUrl = mapToPolicyTokenPolicyAndGetKeyUrl(policyToken3PartnerId);
		return policyToken3PartnerKeyUrl;
	}

	public static String partnerToken2KeyUrl = "";

	// Second partner on the default (authTokenType=partner) policy, so its token
	// for the same UIN can be compared against the main partner's and shown to differ.
	public static String generateAndGetPartnerToken2KeyUrl() {
		partnerToken2KeyUrl = mapPartnerToPolicyAndGetKeyUrl(policyToken2PartnerId, policyName);
		return partnerToken2KeyUrl;
	}

	public static String randomToken2PartnerKeyUrl = "";
	public static String randomToken3PartnerKeyUrl = "";

	// Maps the second and third partners to the random-token policy, to verify
	// "new random token every call" holds across different partners.
	public static String generateAndGetRandomToken2PartnerKeyUrl() {
		randomToken2PartnerKeyUrl = mapPartnerToPolicyAndGetKeyUrl(policyToken2PartnerId, policyNameForRandomToken);
		return randomToken2PartnerKeyUrl;
	}

	public static String generateAndGetRandomToken3PartnerKeyUrl() {
		randomToken3PartnerKeyUrl = mapPartnerToPolicyAndGetKeyUrl(policyToken3PartnerId, policyNameForRandomToken);
		return randomToken3PartnerKeyUrl;
	}

	/**
	 * Decodes response.encryptedKyc (JWT: 3 segments, or JWE: 5 segments) into
	 * response.decodedKyc. JWE is decrypted with the relying-party's own
	 * keystore (same key BioDataUtility signs bio requests with) - the server
	 * encrypts with the partner cert, not the OIDC client's key.
	 */
	public static String injectDecodedKyc(String responseStr, String testCaseName) {
		try {
			JSONObject respJson = new JSONObject(responseStr);
			if (!respJson.has(GlobalConstants.RESPONSE) || respJson.isNull(GlobalConstants.RESPONSE)) {
				return responseStr;
			}
			JSONObject responseObj = respJson.getJSONObject(GlobalConstants.RESPONSE);
			if (!responseObj.has("encryptedKyc") || responseObj.isNull("encryptedKyc")) {
				return responseStr;
			}
			String encryptedKyc = responseObj.getString("encryptedKyc");
			String[] parts = encryptedKyc.split("\\.");
			String jwtToDecode;
			if (parts.length == 5) {
				JWEObject jweObject = JWEObject.parse(encryptedKyc);
				KeyMgrUtility keyMgrUtil = new KeyMgrUtility(new CryptoCoreUtil());
				String dirPath = keyMgrUtil.getKeysDirPath("", certsForModule, ApplnURI.replace("https://", ""));
				// File is "rp-<organizationName>-partner.p12", not "rp-partner.p12".
				PrivateKeyEntry keyEntry = keyMgrUtil.getKeyEntry(dirPath, PartnerTypes.RELYING_PARTY,
						PartnerRegistration.organizationName, true);
				if (keyEntry == null) {
					logger.warn("No relying-party partner keystore found under " + dirPath
							+ " to decrypt encryptedKyc for " + testCaseName);
					return responseStr;
				}
				jweObject.decrypt(new RSADecrypter((RSAPrivateKey) keyEntry.getPrivateKey()));
				jwtToDecode = jweObject.getPayload().toString();
			} else if (parts.length == 3) {
				jwtToDecode = encryptedKyc;
			} else {
				logger.warn("Unrecognized encryptedKyc format for decode (segments=" + parts.length
						+ "), skipping decode for " + testCaseName);
				return responseStr;
			}
			String payloadSegment = jwtToDecode.split("\\.")[1];
			int pad = (4 - payloadSegment.length() % 4) % 4;
			for (int i = 0; i < pad; i++) {
				payloadSegment += "=";
			}
			String decodedJson = new String(Base64.getUrlDecoder().decode(payloadSegment), StandardCharsets.UTF_8);
			responseObj.put("decodedKyc", new JSONObject(decodedJson));
			return respJson.toString();
		} catch (Exception e) {
			logger.error("Failed to decode encryptedKyc for " + testCaseName + ": " + e.getMessage(), e);
			return responseStr;
		}
	}

	// Asserts a claim was NOT returned under decodedKyc.verified_claims (e.g. for
	// max_age filtering) - OutputValidationUtil has no way to assert absence.
	// Call after injectDecodedKyc; no-ops if decode never ran.
	public static void assertVerifiedClaimAbsent(String responseWithDecodedKyc, String claimName, String testCaseName)
			throws AdminTestException {
		try {
			JSONObject respJson = new JSONObject(responseWithDecodedKyc);
			JSONObject responseObj = respJson.optJSONObject(GlobalConstants.RESPONSE);
			if (responseObj == null) {
				return;
			}
			JSONObject decodedKyc = responseObj.optJSONObject("decodedKyc");
			if (decodedKyc == null) {
				return;
			}
			JSONArray verifiedClaims = decodedKyc.optJSONArray("verified_claims");
			if (verifiedClaims == null) {
				return; // key only exists when at least one claim matched - expected here
			}
			for (int i = 0; i < verifiedClaims.length(); i++) {
				JSONObject entry = verifiedClaims.optJSONObject(i);
				JSONObject claims = entry == null ? null : entry.optJSONObject("claims");
				if (claims != null && claims.has(claimName)) {
					throw new AdminTestException("Expected claim '" + claimName
							+ "' to be filtered out of verified_claims for " + testCaseName
							+ " but it was present: " + entry);
				}
			}
		} catch (JSONException e) {
			logger.error("Failed to check verified-claim absence for " + testCaseName + ": " + e.getMessage(), e);
		}
	}

	// Same as AdminTestUtil.postRequestWithCookieAuthHeaderAndSignature, but with
	// a deliberately corrupted signature header (that helper always signs for real,
	// with no hook to corrupt it).
	public Response postRequestWithCookieAuthHeaderAndCorruptSignature(String url, String jsonInput,
			String cookieName, String role, String testCaseName) throws SecurityXSSException {
		return postRequestWithCookieAuthHeaderAndCorruptSignature(url, jsonInput, cookieName, role, testCaseName,
				"invalid-signature-value");
	}

	// Empty value -> "missing signature" (MISSING_INPUT_PARAMETER); non-empty
	// garbage -> "invalid signature" (DSIGN_FALIED).
	public Response postRequestWithCookieAuthHeaderAndCorruptSignature(String url, String jsonInput,
			String cookieName, String role, String testCaseName, String corruptSignatureValue)
			throws SecurityXSSException {
		Response response = null;
		HashMap<String, String> headers = new HashMap<>();
		headers.put(AUTHORIZATHION_HEADERNAME, AUTH_HEADER_VALUE);
		String inputJson = inputJsonKeyWordHandeler(jsonInput, testCaseName);
		headers.put(SIGNATURE_HEADERNAME, corruptSignatureValue);
		String token = new KernelAuthentication().getTokenByRole(role);
		logger.info(GlobalConstants.POST_REQ_URL + url);
		GlobalMethods.reportRequest(headers.toString(), inputJson, url);
		try {
			response = RestClient.postRequestWithMultipleHeaders(url, inputJson, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, token, headers);
			GlobalMethods.checkXSSProtectionHeader(response, url);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);
			return response;
		} catch (SecurityXSSException se) {
			throw se;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return response;
		}
	}

}
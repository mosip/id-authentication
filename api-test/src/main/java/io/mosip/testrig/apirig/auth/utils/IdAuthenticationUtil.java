package io.mosip.testrig.apirig.auth.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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

	/**
	 * Creates and publishes an additional auth policy (under the same policy
	 * group as the main auth partner) using the given allowed-auth-types
	 * attributes file (which determines, among other things, the authTokenType
	 * of the policy), and returns the created policy id.
	 */
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
		String responseBody = response.getBody().asString();
		String createdPolicyId = new JSONObject(responseBody).getJSONObject(GlobalConstants.RESPONSE)
				.getString("id");

		String publishPolicyURL = ApplnURI + properties.getProperty("publishPolicyurl");
		if (publishPolicyURL.contains("POLICYID")) {
			publishPolicyURL = publishPolicyURL.replace("POLICYID", createdPolicyId);
			publishPolicyURL = publishPolicyURL.replace("POLICYGROUPID", policygroupId);
		}

		RestClient.postRequestWithCookie(publishPolicyURL, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

		return createdPolicyId;
	}

	/**
	 * Logs in as the given partner (PMS internal userid/password auth, the same
	 * way {@code KernelAuthentication.getAuthForNewPartner()} logs in as the main
	 * partner) and returns the auth token. Generating an API key is a partner
	 * self-service action in PMS - it has to be done as that partner's own
	 * Keycloak user, not via a generic admin/role token, otherwise PMS rejects
	 * it with "User not authorized" (PMS_PRT_055).
	 */
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
		return new JSONObject(response.getBody().asString()).getJSONObject(GlobalConstants.RESPONSE)
				.getString(GlobalConstants.TOKEN);
	}

	/**
	 * Maps the given partner to the given policy, approves that mapping, and
	 * generates the resulting API key. {@code submitPartnerAndGetMappingKey}
	 * alone only returns a mapping key (not an API key) for an unapproved
	 * mapping; using it as-is in a partner key URL is why IDA used to reject
	 * these calls with "Partner is not registered" (IDA-MPA-009) - the mapping
	 * was never approved and no API key was ever generated for it.
	 */
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
		JSONObject responseValue = new JSONObject(response.asString()).getJSONObject(GlobalConstants.RESPONSE);

		return responseValue.getString(GlobalConstants.APIKEY);
	}

	private static final String AUTH_POLICY_FOR_POLICY_TOKEN_REQUEST_ATTR = "config/AuthPolicyForPolicyTokenAllowedAuthTypes.json";
	private static final String policyNameForPolicyToken = "mosip auth policy for policy token "
			+ BaseTestCase.runContext + System.currentTimeMillis();
	public static String policyIdForPolicyToken = "";
	public static String policyTokenPartnerKeyUrl = "";

	/**
	 * Creates and publishes a second auth policy (under the same policy group as
	 * the main auth partner) whose authTokenType is "policy", so that the
	 * generated authToken is derived from the policy id and the UIN rather than
	 * the partner id.
	 */
	public static void createAndPublishPolicyWithPolicyTokenType() {
		policyIdForPolicyToken = createAndPublishPolicy(policyNameForPolicyToken,
				AUTH_POLICY_FOR_POLICY_TOKEN_REQUEST_ATTR);
	}

	/**
	 * Maps the existing auth partner to the policy-token policy (created via
	 * {@link #createAndPublishPolicyWithPolicyTokenType()}) and builds the
	 * partner key URL that exercises that mapping.
	 */
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

	/**
	 * Creates and publishes a third auth policy (under the same policy group as
	 * the main auth partner) whose authTokenType is "random", so that a new,
	 * unrelated authToken is generated on every authentication call regardless
	 * of the UIN or partner.
	 */
	public static void createAndPublishPolicyWithRandomTokenType() {
		policyIdForRandomToken = createAndPublishPolicy(policyNameForRandomToken,
				AUTH_POLICY_FOR_RANDOM_TOKEN_REQUEST_ATTR);
	}

	/**
	 * Maps the existing auth partner to the random-token policy (created via
	 * {@link #createAndPublishPolicyWithRandomTokenType()}) and builds the
	 * partner key URL that exercises that mapping.
	 */
	public static String generateAndGetRandomTokenPartnerKeyUrl() {
		String randomTokenApiKey = mapPartnerToPolicyAndGenerateApiKey(PartnerRegistration.partnerId,
				policyNameForRandomToken);
		randomTokenPartnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/"
				+ randomTokenApiKey;
		return randomTokenPartnerKeyUrl;
	}

	/**
	 * Registers an additional, independent auth partner (its own
	 * certificates/keys) under the same policy group as the main partner, so
	 * that it can later be mapped to the same policy-token policy as the main
	 * partner. Used to verify that a "policy" authTokenType token depends only
	 * on the policy id and the UIN, not on which partner under that policy made
	 * the call.
	 */
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

		RestClient.postRequestWithCookie(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
				GlobalConstants.AUTHORIZATION, token);

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

	/**
	 * Maps the given partner to the given policy and builds its partner key URL
	 * for that mapping. A partner can be mapped to several different policies
	 * (each mapping mints its own API key), which is how the same partner
	 * entity is reused across the policy/partner/random token type tests
	 * instead of registering a fresh partner for every combination.
	 */
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

	/**
	 * Registers a third partner mapped to the same policy-token policy, so that
	 * the "same token for the same UIN regardless of partner" property can be
	 * verified across more than just a single pair of partners.
	 */
	public static void registerThirdPolicyTokenPartner() {
		registerAdditionalPolicyTokenPartner(policyToken3PartnerId, policyToken3OrganizationName,
				policyToken3EmailId);
	}

	public static String generateAndGetThirdPolicyTokenPartnerKeyUrl() {
		policyToken3PartnerKeyUrl = mapToPolicyTokenPolicyAndGetKeyUrl(policyToken3PartnerId);
		return policyToken3PartnerKeyUrl;
	}

	public static String partnerToken2KeyUrl = "";

	/**
	 * Maps the second partner (already registered for the policy-token tests via
	 * {@link #registerSecondPolicyTokenPartner()}) to the default policy, whose
	 * authTokenType is "partner", instead of the policy-token policy. This gives
	 * a second, independent partner under the SAME authTokenType=partner policy
	 * as the main partner, so the resulting authToken (which is derived from
	 * partnerId and UIN) can be compared against the main partner's token for
	 * the same UIN and shown to differ.
	 */
	public static String generateAndGetPartnerToken2KeyUrl() {
		partnerToken2KeyUrl = mapPartnerToPolicyAndGetKeyUrl(policyToken2PartnerId, policyName);
		return partnerToken2KeyUrl;
	}

	public static String randomToken2PartnerKeyUrl = "";
	public static String randomToken3PartnerKeyUrl = "";

	/**
	 * Maps the second and third partners (already registered for the
	 * policy-token tests) to the random-token policy created via
	 * {@link #createAndPublishPolicyWithRandomTokenType()}, so that the
	 * "a new random token every call, regardless of partner" property can be
	 * verified across multiple different partners sharing the same policy.
	 */
	public static String generateAndGetRandomToken2PartnerKeyUrl() {
		randomToken2PartnerKeyUrl = mapPartnerToPolicyAndGetKeyUrl(policyToken2PartnerId, policyNameForRandomToken);
		return randomToken2PartnerKeyUrl;
	}

	public static String generateAndGetRandomToken3PartnerKeyUrl() {
		randomToken3PartnerKeyUrl = mapPartnerToPolicyAndGetKeyUrl(policyToken3PartnerId, policyNameForRandomToken);
		return randomToken3PartnerKeyUrl;
	}

}
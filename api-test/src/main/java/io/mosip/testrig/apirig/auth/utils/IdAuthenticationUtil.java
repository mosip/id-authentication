package io.mosip.testrig.apirig.auth.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.SkipException;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.dbaccess.DBManager;
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

		if (IdAuthConfigManager.isLocalRun()) {
			String localSkips = IdAuthConfigManager.getproperty("local.skipTestCases");
			if (localSkips != null && java.util.Arrays.stream(localSkips.split(",")).map(String::trim)
					.anyMatch(testCaseName::equals)) {
				throw new SkipException(GlobalConstants.SERVICE_NOT_DEPLOYED
						+ " locally (local.skipTestCases). Hence skipping the testcase");
			}
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
	
	/**
	 * Rewrites demographic literals that are hardcoded in the test data.
	 *
	 * Values such as the date of birth are not $ID:$ placeholders - they are baked
	 * into the yml because an environment run creates the identity itself through
	 * AddIdentity and knows what it planted. A local run authenticates against a
	 * pre-seeded identity whose demographics differ, so those literals have to be
	 * mapped onto the seeded ones.
	 *
	 * The yml files hardcode a single dob literal ("1970/07/08" - the standard
	 * MOSIP test-data value) that only the identity it was originally written
	 * against actually carries. A local run authenticates against whichever
	 * identity is seeded locally, so the literal is remapped to that identity's
	 * own real dob rather than to one fixed value for every UIN/VID.
	 *
	 * individualId selects the mapping from "<uin-or-vid>.dob" (and .name,
	 * .gender) in the local overlay; identities with no such entry are left
	 * unchanged. Returns the input untouched unless local run mode is on, so
	 * environment runs are unaffected.
	 */
	private static final String YML_DEFAULT_DOB = "1970/07/08";
	private static final String YML_INFANT_DOB = "2023/09/28";
	private static final String YML_DRAFT_DOB = "1992/04/15";

	private static final String YML_DEFAULT_NAME = "TEST_FULLNAMEeng";
	private static final String YML_VARIANT_NAME = "Lisa.GN";
	private static final String YML_DEFAULT_GENDER = "MLE";
	private static final java.util.Set<String> YML_DEFAULT_AGES = java.util.Set.of("51", "41");

	public static String applyLocalSubstitutions(String json, String individualId) {
		if (json == null || !IdAuthConfigManager.isLocalRun()) {
			return json;
		}
		String result = json;

		if (individualId != null && !individualId.isBlank()) {
			String resolvedId = (individualId.contains("$ID:")
					? replaceIdWithAutogeneratedId(individualId, "$ID:")
					: individualId).trim();

			String dob = IdAuthConfigManager.getproperty(resolvedId + ".dob");
			boolean hasDob = dob != null && !dob.isBlank();
			for (String ymlDob : new String[] { YML_DEFAULT_DOB, YML_INFANT_DOB, YML_DRAFT_DOB }) {
				if (hasDob && result.contains(ymlDob)) {
					result = result.replace(ymlDob, dob);
					logger.info("Local dob substitution for " + resolvedId + ": '" + ymlDob + "' -> '" + dob + "'");
				}
			}

			String name = IdAuthConfigManager.getproperty(resolvedId + ".name");
			if (name != null && !name.isBlank() && result.contains(YML_DEFAULT_NAME)) {
				result = result.replace(YML_DEFAULT_NAME, name);
				logger.info("Local name substitution for " + resolvedId + ": '" + YML_DEFAULT_NAME + "' -> '" + name
						+ "'");
			}
			if (name != null && !name.isBlank() && result.contains(YML_VARIANT_NAME)) {
				result = result.replace(YML_VARIANT_NAME, name);
				logger.info("Local name-variant substitution for " + resolvedId + ": '" + YML_VARIANT_NAME + "' -> '"
						+ name + "'");
			}

			String gender = IdAuthConfigManager.getproperty(resolvedId + ".gender");
			String quotedGender = "\"" + YML_DEFAULT_GENDER + "\"";
			if (gender != null && !gender.isBlank() && result.contains(quotedGender)) {
				result = result.replace(quotedGender, "\"" + gender + "\"");
				logger.info("Local gender substitution for " + resolvedId + ": '" + YML_DEFAULT_GENDER + "' -> '"
						+ gender + "'");
			}

			if (hasDob && result.matches("(?s).*\"key\"\\s*:\\s*\"age\".*")) {
				java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"value\"\\s*:\\s*\"(\\d+)\"")
						.matcher(result);
				if (m.find() && YML_DEFAULT_AGES.contains(m.group(1))) {
					try {
						String age = String.valueOf(java.time.temporal.ChronoUnit.YEARS.between(
								java.time.LocalDate.parse(dob, java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")),
								java.time.LocalDate.now()));
						result = result.substring(0, m.start(1)) + age + result.substring(m.end(1));
						logger.info("Local age substitution for " + resolvedId + ": '" + m.group(1) + "' -> '" + age
								+ "'");
					} catch (java.time.format.DateTimeParseException e) {
						logger.warn("Local age substitution skipped, " + resolvedId + ".dob is not yyyy/MM/dd: " + dob);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Lets the local suite run part of a yml in one {@code <test>} block and the rest in
	 * another, so a testcase can be placed where the mocks put its identity in the
	 * state it needs (for example between the UIN deactivate and activate calls).
	 *
	 * Reads the {@code <test>} parameters localIncludeTestCases (run only these) and
	 * localExcludeTestCases (run all but these), each a comma separated list of
	 * uniqueIdentifiers. Returns the testcases untouched unless local run mode is on.
	 */
	public static Object[] filterLocalTestCases(Object[] testCases, ITestContext context) {
		if (testCases == null || context == null || !IdAuthConfigManager.isLocalRun()) {
			return testCases;
		}
		Map<String, String> params = context.getCurrentXmlTest().getLocalParameters();
		Set<String> include = splitIds(params.get("localIncludeTestCases"));
		Set<String> exclude = splitIds(params.get("localExcludeTestCases"));
		if (include.isEmpty() && exclude.isEmpty()) {
			return testCases;
		}
		List<Object> kept = new ArrayList<>();
		for (Object testCase : testCases) {
			String id = testCase instanceof TestCaseDTO ? ((TestCaseDTO) testCase).getUniqueIdentifier() : null;
			if ((include.isEmpty() || include.contains(id)) && !exclude.contains(id)) {
				kept.add(testCase);
			}
		}
		logger.info("Local run - " + context.getName() + ": running " + kept.size() + " of " + testCases.length
				+ " testcases (include=" + include + ", exclude=" + exclude + ")");
		return kept.toArray();
	}

	private static Set<String> splitIds(String value) {
		Set<String> ids = new HashSet<>();
		if (value != null) {
			for (String id : value.split(",")) {
				if (!id.isBlank()) {
					ids.add(id.trim());
				}
			}
		}
		return ids;
	}

	/**
	 * Points individual testcases at a different seeded value than the shared
	 * $ID:key$ entry gives everyone else. Only the seeded identity with biometrics
	 * can satisfy an eKYC response, but the key those tests share also serves demo
	 * tests that must stay on their own identity.
	 *
	 * Configured as local.override.<testCaseName>.<seedKey> = <value>. Returns the
	 * input untouched unless local run mode is on.
	 */
	public static String applyLocalTestCaseOverrides(String input, String testCaseName) {
		if (input == null || testCaseName == null || !IdAuthConfigManager.isLocalRun()) {
			return input;
		}
		String result = input;
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\$ID:([A-Za-z0-9_]+)\\$").matcher(input);
		while (m.find()) {
			String value = IdAuthConfigManager.getproperty("local.override." + testCaseName + "." + m.group(1));
			if (value != null && !value.isBlank()) {
				result = result.replace(m.group(0), value.trim());
				logger.info("Local override for " + testCaseName + ": " + m.group(0) + " -> " + value.trim());
			}
		}
		return result;
	}

	/**
	 * Replaces literal values written into the env ymls with their local
	 * counterparts, for values that are not $ID:key$ placeholders (for example the
	 * Logitech device the env hotlist tests block; locally the captures come from
	 * the MOSIP mock SBI, so the hotlist stub blocks its device provider instead).
	 *
	 * Configured as local.literalReplacements = from1=>to1;from2=>to2. Returns the
	 * input untouched unless local run mode is on.
	 */
	public static String applyLocalLiteralReplacements(String input) {
		if (input == null || !IdAuthConfigManager.isLocalRun()) {
			return input;
		}
		String pairs = IdAuthConfigManager.getproperty("local.literalReplacements");
		if (pairs == null || pairs.isBlank()) {
			return input;
		}
		String result = input;
		for (String pair : pairs.split(";")) {
			String[] fromTo = pair.split("=>", 2);
			if (fromTo.length == 2 && !fromTo[0].isBlank() && result.contains(fromTo[0].trim())) {
				result = result.replace(fromTo[0].trim(), fromTo[1].trim());
				logger.info("Local literal replacement: " + fromTo[0].trim() + " -> " + fromTo[1].trim());
			}
		}
		return result;
	}

	/**
	 * Local run only (no-op otherwise). The WireMock stand-ins for idrepo, resident
	 * and hotlist answer first and post their event to IDA afterwards, on their own
	 * thread. Back-to-back calls (deactivate then activate, block then unblock) can
	 * reach IDA out of order, so after a call to one of those endpoints wait until
	 * IDA has applied the event.
	 *
	 * local.mock.settleEndpoints lists endpoint prefixes (comma-separated);
	 * local.mock.settleMillis is the wait (default 2000).
	 */
	public static void settleAfterLocalMockCall(String endPoint) {
		if (endPoint == null || !IdAuthConfigManager.isLocalRun()) {
			return;
		}
		String prefixes = IdAuthConfigManager.getproperty("local.mock.settleEndpoints");
		if (prefixes == null || prefixes.isBlank() || java.util.Arrays.stream(prefixes.split(","))
				.map(String::trim).noneMatch(p -> !p.isEmpty() && endPoint.startsWith(p))) {
			return;
		}
		String settleMillis = IdAuthConfigManager.getproperty("local.mock.settleMillis");
		try {
			Thread.sleep(settleMillis == null || settleMillis.isBlank() ? 2000L : Long.parseLong(settleMillis.trim()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * usePreConfiguredOtp answers every channel with the valid OTP, but a channel
	 * that is not an email address can never receive one. In an environment run
	 * the OTP lookup finds nothing there and the template's own OTP is sent, which
	 * is exactly what the wrong-OTP tests rely on; this restores that.
	 */
	public static String restoreUndeliverableOtp(String request, String template, String otpChannel) {
		if (request == null || !IdAuthConfigManager.isLocalRun() || otpChannel == null
				|| otpChannel.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			return request;
		}
		String templateOtp = io.mosip.testrig.apirig.testrunner.JsonPrecondtion.getValueFromJson(template, "otp");
		if (templateOtp == null || templateOtp.isBlank()) {
			return request;
		}
		logger.info("Local run - otpChannel '" + otpChannel + "' is not an email, sending the template OTP");
		return io.mosip.testrig.apirig.testrunner.JsonPrecondtion.parseAndReturnJsonContent(request, templateOtp,
				"otp");
	}

	/**
	 * Local counterpart of AdminTestUtil.inputTitleHandler. The titleFromAdmin
	 * tests write a placeholder such as "Miss-TitleFromServer"; the handler maps it
	 * to a masterdata code (MIS) and asks the masterdata service for that code's
	 * title name. Locally there is no masterdata service behind the rig's base URL,
	 * so the same lookup is made against local.masterdata.titleUrl - the stub IDA
	 * itself reads titles from - and the placeholder is replaced with the name.
	 */
	public static String resolveLocalTitles(String jsonString) {
		String titleUrl = IdAuthConfigManager.getproperty("local.masterdata.titleUrl");
		if (jsonString == null || titleUrl == null || titleUrl.isBlank()) {
			return jsonString;
		}
		try {
			JSONObject nameObject = new JSONObject(jsonString).getJSONArray("name").getJSONObject(0);
			String placeholder = nameObject.getString("value").split("\\s+")[0];
			String code = propsMap.getProperty(placeholder);
			if (code == null) {
				return jsonString;
			}
			String language = nameObject.getString("language");
			java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient().send(
					java.net.http.HttpRequest.newBuilder(java.net.URI.create(titleUrl.trim())).GET().build(),
					java.net.http.HttpResponse.BodyHandlers.ofString());
			JSONArray titles = new JSONObject(response.body()).getJSONObject("response").getJSONArray("titleList");
			for (int i = 0; i < titles.length(); i++) {
				JSONObject title = titles.getJSONObject(i);
				if (code.equals(title.getString("code")) && language.equals(title.optString("langCode"))) {
					logger.info("Local title resolution: '" + placeholder + "' -> '" + title.getString("titleName") + "'");
					return jsonString.replace(placeholder, title.getString("titleName"));
				}
			}
		} catch (Exception e) {
			logger.error("Local title resolution failed: " + e.getMessage());
		}
		return jsonString;
	}

	public static void dbCleanUp() {
		if (IdAuthConfigManager.isLocalFlagEnabled("local.skipDbCleanup")) {
			return;
		}
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
	
}
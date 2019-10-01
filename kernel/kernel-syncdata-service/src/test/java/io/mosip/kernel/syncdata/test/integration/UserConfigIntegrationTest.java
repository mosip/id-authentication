package io.mosip.kernel.syncdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserConfigIntegrationTest {

	@MockBean
	RestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	SignatureUtil signingUtil;

	@Autowired
	private SyncConfigDetailsService syncConfigDetailsService;

	private SignatureResponse signResponse;

	// ###########################CONFIG START#########################
	private static final String JSON_CONFIG_RESPONSE = "{\r\n" + "\"registrationConfiguration\":\r\n"
			+ "							{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"},\r\n"
			+ "\r\n" + "\"globalConfiguration\":\r\n"
			+ "						{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-key-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-key-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}\r\n"
			+ "}";
	private static final String JSON_REGISTRATION_CONFIG_RESPONSE = "{\"keyValidityPeriodPreRegPack\":\"3\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"defaultDOB\":\"1-Jan\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"supervisorVerificationRequiredForExceptions\":\"true\",\"keyValidityPeriodRegPack\":\"3\",\"irisRetryAttempts\":\"10\",\"fingerprintQualityThreshold\":\"120\",\"multifactorauthentication\":\"true\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"supervisorAuthType\":\"password\",\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":\"10\",\"modeOfNotifyingIndividual\":\"mobile\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user the OTP is $otp\",\"maxDocSizeInMB\":\"150\",\"emailNotificationTemplateOtp\":\"Hello $user the OTP is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user the OTP is $otp\",\"faceRetry\":\"12\",\"noOfFingerprintAuthToOnboardUser\":\"10\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"supervisorAuthMode\":\"IRIS\",\"operatorRegSubmissionMode\":\"fingerprint\",\"officerAuthType\":\"password\",\"faceQualityThreshold\":\"25\",\"gpsDistanceRadiusInMeters\":\"3\",\"automaticSyncFreqServerToClient\":\"25\",\"maxDurationWithoutMasterdataSyncInDays\":\"7\",\"loginMode\":\"bootable dongle\",\"irisQualityThreshold\":\"25\",\"retentionPeriodAudit\":\"3\",\"fingerprintRetryAttempts\":\"234\",\"emailNotificationTemplateNewReg\":\"Hello $user the OTP is $otp\",\"passwordExpiryDurationInDays\":\"3\",\"emailNotificationTemplateLostUIN\":\"Hello $user the OTP is $otp\",\"blockRegistrationIfNotSynced\":\"10\",\"noOfIrisAuthToOnboardUser\":\"10\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\"}";
	private static final String JSON_GLOBAL_CONFIG_RESPONSE = "{\"mosip.kernel.crypto.symmetric-algorithm-name\":\"AES\",\"mosip.kernel.virus-scanner.port\":\"3310\",\"mosip.kernel.email.max-length\":\"50\",\"mosip.kernel.email.domain.ext-max-lenght\":\"7\",\"mosip.kernel.rid.sequence-length\":\"5\",\"mosip.kernel.uin.uin-generation-cron\":\"0 * * * * *\",\"mosip.kernel.rid.centerid-length\":\"5\",\"mosip.kernel.email.special-char\":\"!#$%&'*+-\\/=?^_`{|}~.\",\"mosip.kernel.rid.timestamp-length\":\"14\",\"mosip.kernel.vid.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.asymmetric-key-length\":\"2048\",\"mosip.kernel.uin.min-unused-threshold\":\"100000\",\"mosip.kernel.prid.sequence-limit\":\"3\",\"auth.role.prefix\":\"ROLE_\",\"mosip.kernel.email.domain.ext-min-lenght\":\"2\",\"auth.server.validate.url\":\"http:\\/\\/localhost:8091\\/auth\\/validate_token\",\"mosip.kernel.machineid.length\":\"4\",\"mosip.supported-languages\":\"eng,ara,fra\",\"mosip.kernel.prid.length\":\"14\",\"auth.header.name\":\"Authorization\",\"mosip.kernel.crypto.asymmetric-algorithm-name\":\"RSA\",\"mosip.kernel.phone.min-length\":\"9\",\"mosip.kernel.uin.length\":\"10\",\"mosip.kernel.virus-scanner.host\":\"104.211.209.102\",\"mosip.kernel.email.min-length\":\"7\",\"mosip.kernel.rid.machineid-length\":\"5\",\"mosip.kernel.prid.repeating-block-limit\":\"3\",\"mosip.kernel.vid.length.repeating-block-limit\":\"2\",\"mosip.kernel.rid.length\":\"29\",\"mosip.kernel.phone.max-length\":\"15\",\"mosip.kernel.prid.repeating-limit\":\"2\",\"mosip.kernel.uin.restricted-numbers\":\"786,666\",\"mosip.kernel.email.domain.special-char\":\"-\",\"mosip.kernel.vid.length.repeating-limit\":\"2\",\"mosip.kernel.registrationcenterid.length\":\"4\",\"mosip.kernel.phone.special-char\":\"+ -\",\"mosip.kernel.uin.uins-to-generate\":\"200000\",\"mosip.kernel.vid.length\":\"16\",\"mosip.kernel.tokenid.length\":\"36\",\"mosip.kernel.uin.length.repeating-block-limit\":\"2\",\"mosip.kernel.tspid.length\":\"4\",\"mosip.kernel.tokenid.sequence-limit\":\"3\",\"mosip.kernel.uin.length.repeating-limit\":\"2\",\"mosip.kernel.uin.length.sequence-limit\":\"3\",\"mosip.kernel.keygenerator.symmetric-key-length\":\"256\",\"mosip.kernel.data-key-splitter\":\"#KEY_SPLITTER#\"}";
	private static final String JSON_SYNC_JOB_DEF = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-04-02T07:49:18.454Z\", \"metadata\": null, \"response\": { \"syncJobDefinitions\": [ { \"id\": \"LCS_J00002\", \"name\": \"Login Credentials Sync\", \"apiName\": null, \"parentSyncJobId\": \"NULL\", \"syncFreq\": \"0 0 11 * * ?\", \"lockDuration\": \"NULL\" } ] }, \"errors\": null } ";
	// ###########################CONFIG END#########################

	@WithUserDetails(value = "reg-officer")
	@Test
	public void testGetConfig() throws Exception {
		signResponse = new SignatureResponse();
		signResponse.setData("asdasdsadf4e");
		signResponse.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
		when(signingUtil.sign(Mockito.anyString(), Mockito.anyString())).thenReturn(signResponse);
		ReflectionTestUtils.setField(syncConfigDetailsService, "globalConfigFileName",
				"mosip.kernel.syncdata.global-config-file");
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_GLOBAL_CONFIG_RESPONSE);
		mockMvc.perform(get("/configs")).andExpect(status().isOk());
	}

	@WithUserDetails(value = "reg-officer")
	@Test
	public void testGlobalConfig() throws Exception {
		ReflectionTestUtils.setField(syncConfigDetailsService, "globalConfigFileName",
				"mosip.kernel.syncdata.global-config-file");
		signResponse = new SignatureResponse();
		signResponse.setData("asdasdsadf4e");
		signResponse.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
		when(signingUtil.sign(Mockito.anyString(),Mockito.anyString())).thenReturn(signResponse);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_GLOBAL_CONFIG_RESPONSE);
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isOk());
	}

	@WithUserDetails(value = "reg-officer")
	@Test
	public void testGlobalConfigExceptionTest() throws Exception {
		ReflectionTestUtils.setField(syncConfigDetailsService, "globalConfigFileName", null);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);

		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_GLOBAL_CONFIG_RESPONSE);
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isInternalServerError());
	}

	@WithUserDetails(value = "reg-officer")
	@Test
	public void testGlobalConfigServiceExceptionTest() throws Exception {
		ReflectionTestUtils.setField(syncConfigDetailsService, "globalConfigFileName",
				"mosip.kernel.syncdata.global-config-file");
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(HttpServerErrorException.class);
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isInternalServerError());
	}

	@WithUserDetails(value = "reg-officer")
	@Test
	public void testRegistrationConfig() throws Exception {
		signResponse = new SignatureResponse();
		signResponse.setData("asdasdsadf4e");
		signResponse.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
		when(signingUtil.sign(Mockito.anyString(), Mockito.anyString())).thenReturn(signResponse);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
				.thenReturn(JSON_REGISTRATION_CONFIG_RESPONSE);
		mockMvc.perform(get("/registrationcenterconfig/1")).andExpect(status().isOk());
	}
}

package io.mosip.kernel.synchandler.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;

@Service
public class SyncConfigDetailsServiceImpl implements SyncConfigDetailsService {

	@Override
	public JsonNode getEnrolmentClientConfigDetails() {

		return getConfigDetailsResponse(
				"{\"archivalPolicy\":\"arc_policy_2\",\"otpTimeOutInMinutes\":2,\"numberOfWrongAttemptsForOtp\":5,\"accountFreezeTimeoutInHours\":10, \"uinLength\":24,\"vidLength\":32,\"pridLength\":32,\"tokenIdLength\":23,\"tspIdLength\":24,\"registrationCenterId\":\"KDUE83CJ3\",\"machineId\":\"MCBD3UI3\",\"mobilenumberlength\":10,\"restrictedNumbers\":[\"8732\",\"321\",\"65\"]}");
	}

	@Override
	public JsonNode getAdminConfigDetails(String regId) {

		
		return getConfigDetailsResponse("{\"loginMode\":\"bootable dongle\",\"languages\":{\"primary\":\"arabic\",\"secondary\":\"french\"},\"fingerprintQualityThreshold\":120,\"fingerprintRetryAttempts\":234,\"irisQualityThreshold\":25,\"irisRetryAttempts\":10,\"faceQualityThreshold\":25,\"faceRetry\":12,\"supervisorVerificationRequiredForExceptions\":true,\"operatorRegSubmissionMode\":\"fingerprint\",\"supervisorAuthMode\":\"IRIS\",\"emailNotificationTemplateOtp\":\"Hello $user, the OTP is $otp\",\"smsNotificationTemplateOtp\":\"OTP for your request is $otp\",\"emailNotificationTemplateNewReg\":\"Hello $user, the OTP is $otp\",\"smsNotificationTemplateNewReg\":\"OTP for your request is $otp\",\"emailNotificationTemplateRegCorrection\":\"Hello $user, the OTP is $otp\",\"smsNotificationTemplateRegCorrection\":\"OTP for your request is $otp\",\"emailNotificationTemplateUpdateUIN\":\"Hello $user, the OTP is $otp\",\"smsNotificationTemplateUpdateUIN\":\"OTP for your request is $otp\",\"emailNotificationTemplateLostUIN\":\"Hello $user, the OTP is $otp\",\"smsNotificationTemplateLostUIN\":\"OTP for your request is $otp\",\"passwordExpiryDurationInDays\":3,\"keyValidityPeriodRegPack\":3,\"keyValidityPeriodPreRegPack\":3,\"retentionPeriodAudit\":3,\"automatedSyncFrequency\": {\"masterDataServerToClient\":3,\"clientConfigServerToClient\":3,\"regIdsClientToServer\":3,\"regPacketStatusServerToClient\":3,\"loginCredentialsSync\":3,\"policySyncServerToClient\":3,\"clientStateServerToClient\":3,\"userRoleRightsServerToClient\":3},\"automaticSyncFreqServerToClient\":25,\"blockRegistrationIfNotSynced\":10,\"maxDurationRegPermittedWithoutMasterdataSyncInDays\":10,\"maxDurationWithoutMasterdataSyncInDays\":7,\"modeOfNotifyingIndividual\":\"mobile\",\"noOfFingerprintAuthToOnboardUser\":10,\"noOfIrisAuthToOnboardUser\":10,\"multifactorauthentication\":true,\"loginsequence\":{\"1\":\"OTP\", \"2\":\"Password\", \"3\":\"Fingerprint\" },\"gpsDistanceRadiusInMeters\":3,\"officerAuthType\":\"password\",\"supervisorAuthType\":\"password\",\"defaultDOB\":\"1-Jan\",\"maxDocSizeInMB\":150}");

	}

	private JsonNode getConfigDetailsResponse(String response) {

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser parser;
		JsonNode actualObj = null;
		try {
			parser = factory.createParser(response);
			actualObj = mapper.readTree(parser);

		} catch (IOException e) {

		}

		return actualObj;

	}

}

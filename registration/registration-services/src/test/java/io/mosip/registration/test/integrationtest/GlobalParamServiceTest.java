package io.mosip.registration.test.integrationtest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.GlobalParamName;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.repositories.GlobalParamRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AppConfig.class,DaoConfig.class})
public class GlobalParamServiceTest {

	@Autowired
	GlobalParamService globalParamService;
	/** The globalParam repository. */
	@Autowired
	private GlobalParamRepository globalParamRepository;
    /**
     * Setup Test Cases
     */
	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();	
	}
	//IRIS_THRESHOLD=70, LEFTSLAP_FINGERPRINT_THRESHOLD=80, NUM_OF_FINGERPRINT_RETRIES=3, NUM_OF_IRIS_RETRIES=0, MODE_OF_COMMUNICATION=SMS,EMAIL, RIGHTSLAP_FINGERPRINT_THRESHOLD=80, THUMBS_FINGERPRINT_THRESHOLD=80, INVALID_LOGIN_COUNT=3, INVALID_LOGIN_TIME=2, ONHOLD_COMMENTS=Gender-photo mismatch,Age-photo mismatch,Name correction required,Address correction required,Date of birth correction required, REJECT_COMMENTS=Correction not possible,Wrong Person,Invalid Data,Incorrect indroducer,Incorrect IDs, KEY_POLICY_SYNC_THRESHOLD_VALUE=1, MAX_REG_PACKET_SIZE=5, REGISTARTION_CENTER=20916, smsNotificationTemplateRegCorrection=null, keyValidityPeriodPreRegPack=Fingerprint, policySyncServerToClient=Fingerprint, smsNotificationTemplateOtp=null, supervisorVerificationRequiredForExceptions=null, clientConfigServerToClient=Fingerprint, irisRetryAttempts=null, fingerprintQualityThreshold=null, userRoleRightsServerToClient=Fingerprint, smsNotificationTemplateUpdateUIN=null, emailNotificationTemplateUpdateUIN=null, emailNotificationTemplateOtp=null, supervisorAuthMode=null, operatorRegSubmissionMode=null, gpsDistanceRadiusInMeters=Fingerprint, loginCredentialsSync=Fingerprint, maxDurationWithoutMasterdataSyncInDays=null, loginMode=null, clientStateServerToClient=Fingerprint, secondary=null, 1=null, 2=null, 3=null, retentionPeriodAudit=Fingerprint, fingerprintRetryAttempts=null, regIdsClientToServer=Fingerprint, smsNotificationTemplateNewReg=null, primary=null, defaultDOB=null, regPacketStatusServerToClient=Fingerprint, keyValidityPeriodRegPack=Fingerprint, multifactorauthentication=null, supervisorAuthType=null, maxDurationRegPermittedWithoutMasterdataSyncInDays=null, modeOfNotifyingIndividual=null, maxDocSizeInMB=null, emailNotificationTemplateRegCorrection=null, faceRetry=null, noOfFingerprintAuthToOnboardUser=null, smsNotificationTemplateLostUIN=null, officerAuthType=null, faceQualityThreshold=null, automaticSyncFreqServerToClient=null, irisQualityThreshold=null, emailNotificationTemplateNewReg=null, passwordExpiryDurationInDays=Fingerprint, emailNotificationTemplateLostUIN=null, blockRegistrationIfNotSynced=null, noOfIrisAuthToOnboardUser=null, masterDataServerToClient=Fingerprint, FINGERPRINT_DISABLE_FLAG=Y, REG_PAK_MAX_CNT_APPRV_LIMIT=30, REG_PAK_MAX_TIME_APPRV_LIMIT=30
	@Test
	/**
	 * Global @param Test to Fetch 
	 */
	public void getGlobalParamsTest() {
		//Fetch Value from DB 
		List<GlobalParamName> globalParams = globalParamRepository.findByIsActiveTrue();
		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParams.forEach(param -> globalParamMap.put(param.getName(), param.getVal()));
		io.mosip.registration.context.ApplicationContext.getInstance().getApplicationLanguageBundle();
		//Fetch Value from Service Assert if Global is loaded properly
		Map<String,Object> initSettings=globalParamService.getGlobalParams();
		Assert.assertEquals(initSettings.get("IRIS_THRESHOLD").toString(), globalParamMap.get("IRIS_THRESHOLD"));
		Assert.assertEquals(initSettings.get("LEFTSLAP_FINGERPRINT_THRESHOLD").toString(), globalParamMap.get("LEFTSLAP_FINGERPRINT_THRESHOLD"));
		Assert.assertEquals(initSettings.get("NUM_OF_FINGERPRINT_RETRIES").toString(), globalParamMap.get("NUM_OF_FINGERPRINT_RETRIES"));
		Assert.assertEquals(initSettings.get("NUM_OF_IRIS_RETRIES").toString(), globalParamMap.get("NUM_OF_IRIS_RETRIES"));
		Assert.assertEquals(initSettings.get("RIGHTSLAP_FINGERPRINT_THRESHOLD").toString(), globalParamMap.get("RIGHTSLAP_FINGERPRINT_THRESHOLD"));
		Assert.assertEquals(initSettings.get("THUMBS_FINGERPRINT_THRESHOLD").toString(), globalParamMap.get("THUMBS_FINGERPRINT_THRESHOLD"));
		Assert.assertEquals(initSettings.get("KEY_POLICY_SYNC_THRESHOLD_VALUE").toString(), globalParamMap.get("KEY_POLICY_SYNC_THRESHOLD_VALUE"));
		Assert.assertEquals(initSettings.get("MAX_REG_PACKET_SIZE").toString(), globalParamMap.get("MAX_REG_PACKET_SIZE"));
		Assert.assertEquals(initSettings.get("REG_PAK_MAX_TIME_APPRV_LIMIT").toString(), globalParamMap.get("REG_PAK_MAX_TIME_APPRV_LIMIT"));
		Assert.assertEquals(initSettings.get("REG_PAK_MAX_CNT_APPRV_LIMIT").toString(), globalParamMap.get("REG_PAK_MAX_CNT_APPRV_LIMIT"));
	}
	
	/**
	 * SyncConfigData checking if Sync Config is running or not 
	 */
	@Test
	public void synchConfigDataTest() {
		ResponseDTO response=globalParamService.synchConfigData();
		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable() && globalParamService.getGlobalParams().isEmpty()) {
		Assert.assertEquals(response.getErrorResponseDTOs().get(0).getCode(), RegistrationConstants.ERROR);
		Assert.assertEquals(response.getErrorResponseDTOs().get(0).getMessage(),RegistrationConstants.GLOBAL_CONFIG_ERROR_MSG);
		}else {
			Assert.assertEquals(response.getSuccessResponseDTO().getMessage(),Matchers.anyOf(Matchers.is(RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE),Matchers.is(RegistrationConstants.MASTER_SYNC_FAILURE_MSG))); 
			Assert.assertEquals(response.getSuccessResponseDTO().getCode(), RegistrationConstants.ALERT_INFORMATION);
			
		}
	}
}

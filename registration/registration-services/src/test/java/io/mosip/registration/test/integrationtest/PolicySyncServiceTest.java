package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.PolicySyncService;
import io.mosip.registration.service.config.GlobalParamService;

public class PolicySyncServiceTest extends BaseIntegrationTest {

	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	PolicySyncService policySyncService;
	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
		
	}
	
	@Test
	public void getPolicyTest() {
		ResponseDTO response=policySyncService.fetchPolicy();
		assertEquals(response.getSuccessResponseDTO().getCode(), RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
		assertEquals(response.getSuccessResponseDTO().getMessage(), RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
		assertEquals(response.getSuccessResponseDTO().getInfoType(), RegistrationConstants.ALERT_INFORMATION);
	}
}

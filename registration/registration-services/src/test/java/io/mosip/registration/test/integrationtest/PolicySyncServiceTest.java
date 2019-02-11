package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

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
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.PolicySyncService;
import io.mosip.registration.service.config.GlobalParamService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AppConfig.class,DaoConfig.class})
public class PolicySyncServiceTest {

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
		ResponseDTO response=policySyncService.fetchPolicy("20");
		assertEquals(response.getSuccessResponseDTO().getCode(), RegistrationConstants.POLICY_SYNC_SUCCESS_CODE);
		assertEquals(response.getSuccessResponseDTO().getMessage(), RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE);
		assertEquals(response.getSuccessResponseDTO().getInfoType(), RegistrationConstants.ALERT_INFORMATION);
	}
}

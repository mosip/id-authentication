package io.mosip.registration.test.integrationtest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;

public class SyncStatusValidatorServiceTest extends BaseIntegrationTest
{
	@Autowired
	private SyncStatusValidatorService syncstatusvalidatorservice;
	
	@Autowired
	private GlobalParamService globalParamService;
	
	
	@Test
	public void test1() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		
		applicationContext.getApplicationMap().putAll(globalParamService.getGlobalParams());
				
		//syncstatusvalidatorservice.validateSyncStatus();
		ResponseDTO result = syncstatusvalidatorservice.validateSyncStatus();
		System.out.println("********"+result.getErrorResponseDTOs().get(0).getMessage());
	}
}

package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;

public class SyncStatusValidatorServiceTest extends BaseIntegrationTest
{
	//defect MOS-16007
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
		assertTrue(result.getErrorResponseDTOs().isEmpty());
	}
	
	
	@Test
	public void test2() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		
		applicationContext.getApplicationMap().putAll(globalParamService.getGlobalParams());
				
		//syncstatusvalidatorservice.validateSyncStatus();
		ResponseDTO result = syncstatusvalidatorservice.validateSyncStatus();
		assertTrue(result.getSuccessResponseDTO()==null);
	}
}

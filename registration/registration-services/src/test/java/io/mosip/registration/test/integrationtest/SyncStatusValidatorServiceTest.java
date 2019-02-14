package io.mosip.registration.test.integrationtest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;

public class SyncStatusValidatorServiceTest extends BaseIntegrationTest
{
	@Autowired
	private SyncStatusValidatorService syncstatusvalidatorservice;
	
	@Test
	public void syncstatusvalidatorservice_verify()
	{
		//syncstatusvalidatorservice.validateSyncStatus();
		ResponseDTO result = syncstatusvalidatorservice.validateSyncStatus();
		System.out.println("********"+result);
	}
}

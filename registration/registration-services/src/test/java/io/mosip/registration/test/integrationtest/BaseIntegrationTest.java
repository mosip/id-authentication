package io.mosip.registration.test.integrationtest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import org.junit.runners.Suite;

/**
 * Base integration test class
 * 
 * @author Omsai Eswar M.
 *
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class, DaoConfig.class })
@Suite.SuiteClasses({ GlobalParamServiceTest.class, DocumentScannerServiceTest.class, FingerPrintServiceTest.class,
		JobConfigurationServiceTest.class, LoginServiceTest.class, MasterSyncServiceTest.class,
		PacketHandlerServiceTest.class, PolicySyncServiceTest.class, PreRegistrationDataSyncServiceImplTest.class,
		RegPacketStatusServiceImplTest.class, StorageServiceTest.class, SyncStatusValidatorServiceTest.class })
@ActiveProfiles(profiles = "integration-test")
public abstract class BaseIntegrationTest {
	static {
		System.setProperty("spring.profiles.active", "qa");
	}
}

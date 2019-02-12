package io.mosip.registration.test.integrationtest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AppConfig.class, DaoConfig.class})
@ActiveProfiles(profiles="integration-test")
/**
 * Base integration test class
 * 
 * @author Omsai Eswar M.
 *
 */
public abstract class BaseIntegrationTest {

}

package io.mosip.registration.test.integrationtest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;

/**
 * Base integration test class
 * 
 * @author Omsai Eswar M.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {AppConfig.class, DaoConfig.class})
@ActiveProfiles(profiles="integration-test")
public abstract class BaseIntegrationTest {

}

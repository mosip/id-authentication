package io.mosip.registration.test.integrationtest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.context.ApplicationContext;

/**
 * Base integration test class
 * 
 * @author Omsai Eswar M.
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {AppConfig.class, DaoConfig.class})
@ActiveProfiles(profiles="integration-test")
public abstract class BaseIntegrationTest {
@Before
public void setUO() {
	System.out.println("Here");
}
}

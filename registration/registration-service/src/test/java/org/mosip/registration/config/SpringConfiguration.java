package org.mosip.registration.config;

import org.junit.runner.RunWith;
import org.mosip.registration.config.AppConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =	{AppConfig.class})
public class SpringConfiguration {

}

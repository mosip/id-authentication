package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.impl.TemplateServiceImpl;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class TemplateServiceImplTest {
	
	@Autowired
	private TemplateServiceImpl templateServiceImpl;
	
	@Before
	public void setup() {
		ApplicationContext context=ApplicationContext.getInstance();
		context.getApplicationLanguageBundle();
		context.getApplicationLanguagevalidationBundle();
		context.getApplicationMap();
		context.getApplicationMessagesBundle();
		
	}
	@Test
	public void getTemplate_ValidTest(){
		Template result= templateServiceImpl.getTemplate(RegistrationConstants.NOTIFICATION_TEMPLATE);
		System.out.println(result.getFileTxt());
		assertEquals(result.getDescr(),"Email and SMS Notification Template");
		
	}
	
	@Test
	public void getTemplate_InvalidTest() {
		Template result=templateServiceImpl.getTemplate("Inavlid Template");
		assertNull(result.getDescr());
	}

}

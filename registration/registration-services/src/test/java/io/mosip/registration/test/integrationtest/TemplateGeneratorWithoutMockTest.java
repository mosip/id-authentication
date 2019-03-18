package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.impl.TemplateServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.acktemplate.TemplateGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class TemplateGeneratorWithoutMockTest {

	@Autowired
	TemplateGenerator templateGenerator;
	@Autowired
	TemplateServiceImpl templateServiceImpl;
	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;
	
	private static Map<String, Object> sessionContextMap;
	
	@Before
	public void setup() {
		ApplicationContext context=ApplicationContext.getInstance();
		context.setApplicationLanguageBundle();
		context.setApplicationMessagesBundle();
		context.setLocalLanguageProperty();
		context.setLocalMessagesBundle();
		ReflectionTestUtils.setField(context,"applicationLanguge","en");
		ReflectionTestUtils.setField(context,"localLanguage","en");
	}
	
	@Test
	public void generateTemplate_Test() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO =DataProvider.getPacketDTO();
		String templateText=templateServiceImpl.getHtmlTemplate(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE,"eng");
		ResponseDTO result=templateGenerator.generateTemplate(templateText, registrationDTO,templateManagerBuilder,RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE);
		assertEquals(result.getErrorResponseDTOs().get(0).getCode(), "ERROR");
	}
	
	
	
	
}


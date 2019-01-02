package io.mosip.registration.processor.message.sender.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;

@RunWith(MockitoJUnitRunner.class)
public class TemplateGeneratorTest {
	@InjectMocks
	TemplateGenerator templateGenerator;

	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	private TemplateDto templateDto;
	private TemplateResponseDto responseDto;

	@Before
	public void setup() throws ApisResourceAccessException, IOException {
		templateDto = new TemplateDto();
		templateDto.setFileText("Hi $FirstName, your UIN is generated");
		List<TemplateDto> dtoList = new ArrayList<>();
		dtoList.add(templateDto);
		responseDto = new TemplateResponseDto();
		responseDto.setTemplates(dtoList);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(responseDto);

	}

	@Test
	public void testSuccessfulTemplateGenerator() throws IOException, ApisResourceAccessException {
		String templateTypeCode = "SMS";
		String langCode = "eng";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("FirstName", "Alok");

		String result = templateGenerator.getTemplate(templateTypeCode, attributes, langCode);
		assertEquals("Hi Alok, your UIN is generated", result);
	}

}

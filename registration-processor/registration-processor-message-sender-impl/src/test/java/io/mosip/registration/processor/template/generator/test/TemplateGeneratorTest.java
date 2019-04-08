package io.mosip.registration.processor.template.generator.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.template.TemplateGenerator;

/**
 * The Class TemplateGeneratorTest.
 */
@RunWith(SpringRunner.class)
public class TemplateGeneratorTest {

	/** The template generator. */
	@InjectMocks
	TemplateGenerator templateGenerator;

	/** The rest client service. */
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	private ObjectMapper mapper;

	/** The template dto. */
	private TemplateDto templateDto;

	/** The response dto. */
	private TemplateResponseDto responseDto;

	/**
	 * Setup.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws ApisResourceAccessException, IOException {
		ResponseWrapper<TemplateResponseDto> responseWrapper = new ResponseWrapper<>();
		templateDto = new TemplateDto();
		templateDto.setFileText("Hi $FirstName, your UIN is generated");
		List<TemplateDto> dtoList = new ArrayList<>();
		dtoList.add(templateDto);
		responseDto = new TemplateResponseDto();
		responseDto.setTemplates(dtoList);
		String s = responseDto.toString();
		responseWrapper.setResponse(responseDto);
		responseWrapper.setErrors(null);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(responseWrapper);
		Mockito.when(mapper.writeValueAsString(any())).thenReturn(s);
		Mockito.when(mapper.readValue(anyString(), any(Class.class))).thenReturn(responseDto);
	}

	/**
	 * Test successful template generator.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	@Test
	public void testSuccessfulTemplateGenerator() throws IOException, ApisResourceAccessException {
		String templateTypeCode = "SMS";
		String langCode = "eng";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("FirstName", "Alok");
		InputStream expected = IOUtils.toInputStream("Hi Alok, your UIN is generated", "UTF-8");
		InputStream result = templateGenerator.getTemplate(templateTypeCode, attributes, langCode);

		Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, result));
	}

	@Test(expected = TemplateProcessingFailureException.class)
	public void testExceptions() throws IOException, ApisResourceAccessException {
		String templateTypeCode = "SMS";
		String langCode = "eng";
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("FirstName", "Alok");

		TemplateResourceNotFoundException e = new TemplateResourceNotFoundException(null, null);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenThrow(e);

		templateGenerator.getTemplate(templateTypeCode, attributes, langCode);
	}

}

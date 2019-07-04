package io.mosip.registration.processor.core.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.digital.signature.dto.SignResponseDto;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.exception.DigitalSignatureException;

@RunWith(SpringRunner.class)
public class DigitalSignatureTest {

	@InjectMocks
	DigitalSignatureUtility utility;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Mock
	private Environment env;
	
	@Mock
	private ObjectMapper mapper;
	
	private static String signature="signature";
	@Before
	public void setUp() throws ApisResourceAccessException, JsonParseException, JsonMappingException, IOException {
		Mockito.when(env.getProperty("mosip.registration.processor.digital.signature.id")).thenReturn("id");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	}

	@Test
	public void testGetSignature() throws ApisResourceAccessException, IOException {
		SignResponseDto dto = new SignResponseDto();
		dto.setSignature(signature);
		ResponseWrapper<SignResponseDto> response = new ResponseWrapper<SignResponseDto>();
		response.setResponse(dto);
		Mockito.when(registrationProcessorRestService.postApi(Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any(), Matchers.any())).thenReturn(response);
		Mockito.when(mapper.writeValueAsString(Matchers.any())).thenReturn("value");
		Mockito.when(mapper.readValue(Matchers.anyString(), Matchers.any(Class.class))).thenReturn(dto);
		
		Assert.assertSame(signature, utility.getDigitalSignature("qwerty"));
	}
	
	
	@Test(expected=DigitalSignatureException.class)
	public void testException() throws ApisResourceAccessException {
		Mockito.when(registrationProcessorRestService.postApi(Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any(), Matchers.any())).thenThrow(ApisResourceAccessException.class);
		utility.getDigitalSignature("qwerty");
	}
	
}

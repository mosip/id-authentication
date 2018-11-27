package io.mosip.registration.test.login;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.mosip.registration.dto.OtpGeneratorRequestDto;
import io.mosip.registration.dto.OtpGeneratorResponseDto;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;


public class RestClientUtilTest {
	
	@Mock
	RestTemplate restTemplate;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	RestClientUtil restClientUtil;
	
	@Test
	public void invokeTest() throws URISyntaxException, HttpClientErrorException, HttpServerErrorException, ResourceAccessException, SocketTimeoutException {
		OtpGeneratorResponseDto generatorResponseDto=new OtpGeneratorResponseDto();
		generatorResponseDto.setOtp("099977");
		OtpGeneratorRequestDto otpGeneratorRequestDto=new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("tutuy");
		HttpEntity<?> httpEntity=new HttpEntity<OtpGeneratorRequestDto>(otpGeneratorRequestDto);
		URI uri=new URI("http://localhost:8080/otpmanager/otps");
		RequestHTTPDTO requestHTTPDTO=new RequestHTTPDTO();
		requestHTTPDTO.setClazz(OtpGeneratorResponseDto.class);
		
		requestHTTPDTO.setHttpEntity(httpEntity);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setUri(uri);
		
		Assert.assertNull(restClientUtil.invoke(requestHTTPDTO));
	}

}

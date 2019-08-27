package io.mosip.registration.test.login;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.OtpGeneratorResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

public class RestClientUtilTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	RestClientUtil restClientUtil;

	@Mock
	RequestHTTPDTO requestHTTPDTO;

	@Test(expected= ResourceAccessException.class)
	public void invokeTest() throws URISyntaxException, HttpClientErrorException, HttpServerErrorException,
			ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		OtpGeneratorResponseDTO generatorResponseDto = new OtpGeneratorResponseDTO();
		generatorResponseDto.setOtp("099977");
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("tutuy");
		HttpEntity<?> httpEntity = new HttpEntity<OtpGeneratorRequestDTO>(otpGeneratorRequestDTO);
		URI uri = new URI("http://localhost:8080/otpmanager/otps");
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setClazz(OtpGeneratorResponseDTO.class);

		requestHTTPDTO.setHttpEntity(httpEntity);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
		requestHTTPDTO.setUri(uri);

		Assert.assertNull(restClientUtil.invoke(requestHTTPDTO));
	}

	@Ignore
	@Test(expected= ResourceAccessException.class)
	public void invokeHeadersTest() throws URISyntaxException, HttpClientErrorException, HttpServerErrorException,
			ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		OtpGeneratorResponseDTO generatorResponseDto = new OtpGeneratorResponseDTO();
		generatorResponseDto.setOtp("099977");
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("tutuy");
		HttpEntity<?> httpEntity = new HttpEntity<OtpGeneratorRequestDTO>(otpGeneratorRequestDTO);
		URI uri = new URI("http://localhost:8080/otpmanager/otps");
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setClazz(OtpGeneratorResponseDTO.class);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
		requestHTTPDTO.setHttpEntity(httpEntity);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setUri(uri);

		Assert.assertNull(restClientUtil.invoke(requestHTTPDTO));
	}

	@Ignore
	@Test(expected= ResourceAccessException.class)
	public void invokeHeadersException() throws URISyntaxException, HttpClientErrorException, HttpServerErrorException,
			ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		OtpGeneratorResponseDTO generatorResponseDto = new OtpGeneratorResponseDTO();
		generatorResponseDto.setOtp("099977");
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("tutuy");
		HttpEntity<?> httpEntity = new HttpEntity<OtpGeneratorRequestDTO>(otpGeneratorRequestDTO);
		URI uri = new URI("https://localhost:8080/otpmanager/otps");
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setClazz(OtpGeneratorResponseDTO.class);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
		requestHTTPDTO.setHttpEntity(httpEntity);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setUri(uri);

		Assert.assertNull(restClientUtil.invoke(requestHTTPDTO));

	}

	@Ignore
	@Test(expected= ResourceAccessException.class)
	public void invokeHeaderException() throws URISyntaxException, HttpClientErrorException, HttpServerErrorException,
			ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		OtpGeneratorResponseDTO generatorResponseDto = new OtpGeneratorResponseDTO();
		generatorResponseDto.setOtp("099977");
		OtpGeneratorRequestDTO otpGeneratorRequestDTO = new OtpGeneratorRequestDTO();
		otpGeneratorRequestDTO.setKey("tutuy");
		HttpEntity<?> httpEntity = new HttpEntity<OtpGeneratorRequestDTO>(otpGeneratorRequestDTO);
		URI uri = new URI("https://localhost:8080/otpmanager/otps");
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setClazz(OtpGeneratorResponseDTO.class);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
		requestHTTPDTO.setHttpEntity(httpEntity);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setUri(uri);

		Assert.assertNull(restClientUtil.invoke(requestHTTPDTO));

	}

}

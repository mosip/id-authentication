package io.mosip.kernel.syncdata.test.utils;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.syncdata.dto.CryptoManagerRequestDto;
import io.mosip.kernel.syncdata.dto.CryptoManagerResponseDto;
import io.mosip.kernel.syncdata.exception.CryptoManagerServiceException;
import io.mosip.kernel.syncdata.exception.ParseResponseException;
import io.mosip.kernel.syncdata.utils.SigningUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SigningUtilTest {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.kernel.syncdata.syncdata-request-id:SYNCDATA.REQUEST}")
	private String syncDataRequestId;

	@Value("${mosip.kernel.syncdata.syncdata-version-id:v1.0}")
	private String syncDataVersionId;

	@Value("${mosip.kernel.syncdata.cryptomanager-encrypt-url:http://localhost:8087/cryptomanager/encrypt/private}")
	private String encryptUrl;

	@Autowired
	private ObjectMapper objectMapper;

	private MockRestServiceServer server;

	@Autowired
	private SigningUtil signingUtil;

	private CryptoManagerRequestDto cryptoManagerRequestDto;

	private RequestWrapper<CryptoManagerRequestDto> requestWrapper;

	@Before
	public void setUp() {
		server = MockRestServiceServer.bindTo(restTemplate).build();
		cryptoManagerRequestDto = new CryptoManagerRequestDto();
		cryptoManagerRequestDto.setApplicationId("KERNEL");
		cryptoManagerRequestDto.setReferenceId("KER");
		cryptoManagerRequestDto.setData("MOSIP");
		cryptoManagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		requestWrapper = new RequestWrapper<>();
	}

	@Test
	public void signResponseData() throws JsonProcessingException {

		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);
		CryptoManagerResponseDto cryptoManagerResponseDto = new CryptoManagerResponseDto();
		cryptoManagerResponseDto.setData(
				"jxAq1SysvWKK78C-2TduZDn2ACJLXReYjM4rWsd2KBSVat_wFxU5D_tiNUvI7gZ9hEGZbhcnQ5n0z8TsAMD3VYFc8WBVIjGsskE7ijhlVHjP3wsP4G1llj0eWcwLAido9K5iwSeeGbT7bJzsiVJTsqtZKRvHFj8gBW0T76jpviri2joYxJY3xD7f2HiA0dbVHzUiD5D8NkYZmQwlYMTeSNoHPYn2hq4Bt22YAjdIlQNNTxlUu1XM7P7eR-unRXXPsl9wDw6Gl1xzgN3SOE-WqmI3oIq61JvZiXhi_SKIt_RqMwymUHmTlb1MQfGB32ip6nPR1xdU3ArGRAuvYnmIGA");

		ResponseWrapper<CryptoManagerResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(cryptoManagerResponseDto);
		String response = objectMapper.writeValueAsString(responseWrapper);
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.signResponseData("MOSIP");
	}

	@Test(expected = CryptoManagerServiceException.class)
	public void signResponseDataErrorTest() throws JsonProcessingException {

		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);

		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError("KER-CRY-001", "No Such algorithm is supported");
		errors.add(serviceError);
		responseWrapper.setErrors(errors);

		String response = objectMapper.writeValueAsString(responseWrapper);
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.signResponseData("MOSIP");
	}

	@Test(expected = ParseResponseException.class)
	public void signResponseDataExceptionTest() throws JsonProcessingException {

		String response = "{\"id\": \"string\",\"version\": \"string\",\"responsetime\": \"2019-04-06T12:52:32.450Z\",\"metadata\": null,\"response\": {\"data\": \"n7AvMtZ_nHb2AyD9IrXfA6sG9jc8IEgmkIYN2pVFaJ9Qw8v1JEMgneL0lVR-},\"errors\": null}";
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.signResponseData("MOSIP");
	}
}

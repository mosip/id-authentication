package io.mosip.kernel.cryptomanager.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.test.CryptoManagerTestBootApplication;

@SpringBootTest(classes = CryptoManagerTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CryptographicServiceIntegrationExceptionTest {

	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String publicKeyUrl;

	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String symmetricKeyUrl;

	@Value("${mosip.kernel.keymanager-service-encrypt-url}")
	private String encryptUrl;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	private MockRestServiceServer server;

	private UriComponentsBuilder builder;

	private Map<String, String> uriParams;

	private CryptomanagerRequestDto requestDto;

	private RequestWrapper<CryptomanagerRequestDto> requestWrapper;

	private static final String ID = "mosip.crypto.service";
	private static final String VERSION = "V1.0";

	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(ID);
		requestWrapper.setVersion(VERSION);
		requestWrapper.setRequesttime(LocalDateTime.now(ZoneId.of("UTC")));
        server = MockRestServiceServer.bindTo(restTemplate).build();
		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");
		builder = UriComponentsBuilder.fromUriString(publicKeyUrl).queryParam("timeStamp", "2018-12-06T12:07:44.403Z")
				.queryParam("referenceId", "ref123");
	}

	@WithUserDetails("reg-processor")
	@Test
	public void testInvalidSpecEncrypt() throws Exception {
		KeymanagerPublicKeyResponseDto keymanagerPublicKeyResponseDto = new KeymanagerPublicKeyResponseDto(
				CryptoUtil.encodeBase64("badprivatekey".getBytes()), LocalDateTime.now(),
				LocalDateTime.now().plusDays(100));
		ResponseWrapper<KeymanagerPublicKeyResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerPublicKeyResponseDto);
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString()))
				.andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		requestDto.setApplicationId("REGISTRATION");
		requestDto.setData("dXJ2aWw");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));

		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<CryptomanagerResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<CryptomanagerResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-CRY-002"));
	}

	@WithUserDetails("reg-processor")
	@Test
	public void testMethodArgumentNotValidException() throws Exception {
		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		requestDto.setApplicationId("");
		requestDto.setData("");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<CryptomanagerResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<CryptomanagerResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-CRY-004"));
	}

	@WithUserDetails("reg-processor")
	@Test
	public void testInvalidFormatException() throws Exception {
		String requestBody = "{\r\n" + "\"id\":\"\",\r\n" + "\"version\":\"\",\r\n" + "\"requesttime\":\"\",\r\n"
				+ "\"metadata\":{},\r\n" + "\"request\":{\r\n" + "  \"applicationId\": \"REGISTRATION\",\r\n"
				+ "  \"data\": \"dXJ2aWwKCgoKam9zaGk=\",\r\n" + "  \"referenceId\": \"REF01\",\r\n"
				+ "  \"timeStamp\": \"2018-12-1\"\r\n" + "}\r\n" + "}";
		MvcResult result = mockMvc.perform(post("/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<CryptomanagerResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<CryptomanagerResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-CRY-004"));
	}

	@WithUserDetails("reg-processor")
	@Test
	public void testIllegalArgumentException() throws Exception {
		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		requestDto.setApplicationId("REGISTRATION");
		requestDto.setData("dXJ2aWw");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		MvcResult result =mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<CryptomanagerResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<CryptomanagerResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-CRY-003"));
	}

	@WithUserDetails("reg-processor")
	@Test
	public void testEncryptKeymanagerErrorsTest() throws Exception {
		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		ServiceError error = new ServiceError("KER-KMS-003", "No unique alias is found");
		List<ServiceError> errors = new ArrayList<>();
		errors.add(error);
		errorResponse.setErrors(errors);
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString()))
				.andRespond(withSuccess(objectMapper.writeValueAsString(errorResponse), MediaType.APPLICATION_JSON));
		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		/* Set value in CryptomanagerRequestDto */
		requestDto.setApplicationId("REGISTRATION");
		requestDto.setData("dXJ2aWw");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		MvcResult result =mockMvc.perform(post("/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<CryptomanagerResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<CryptomanagerResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-KMS-003"));

	}

	@WithUserDetails("reg-processor")
	@Test
	public void testDecryptKeymanagerErrorsTest() throws Exception {
		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		ServiceError error = new ServiceError("KER-KMS-003", "No unique alias is found");
		List<ServiceError> errors = new ArrayList<>();
		errors.add(error);
		errorResponse.setErrors(errors);
		server.expect(requestTo(symmetricKeyUrl))
				.andRespond(withSuccess(objectMapper.writeValueAsString(errorResponse), MediaType.APPLICATION_JSON));
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		/* Set value in CryptomanagerRequestDto */
		requestDto.setApplicationId("uoiuoi");
		requestDto.setData("dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<CryptomanagerResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<CryptomanagerResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-KMS-003"));
	}
}

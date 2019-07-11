package io.mosip.authentication.partnerdemo.service.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.partnerdemo.service.dto.CryptomanagerRequestDto;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;;

/**
 * The Class Decrypt is used to decrypt the KYC Response.
 *  @author Arun Bose S
 * @author Sanjay Murali
 */
@RestController
public class Decrypt {

	@Autowired
	private Environment env;
	
	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;
	
	/** The app ID. */
	@Value("${application.id}")
	private String appID;
	
	/** The app ID. */
	@Value("${cryptomanager.partner.id}")
	private String partnerId;
	
	/** The encrypt URL. */
	@Value("${mosip.kernel.decrypt-url}")
	private String decryptURL;
	
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(Decrypt.class);

	/**
	 * Decrypt.
	 *
	 * @param data the data
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyManagementException the key management exception
	 */
	@PostMapping(path = "/authRequest/decrypt", produces = MediaType.APPLICATION_JSON_VALUE) 
	public String decrypt(@RequestBody String data)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, KeyManagementException {
		return kernelDecrypt(data);
	}

	/**
	 * This method is used to call the kernel decrypt api for decryption.
	 *
	 * @param data the data
	 * @return the string
	 * @throws KeyManagementException the key management exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String kernelDecrypt(String data)
			throws KeyManagementException, NoSuchAlgorithmException {
		Encrypt.turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = generateAuthToken();
				if(authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));

		CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
		cryptomanagerRequestDto.setApplicationId(appID);
		cryptomanagerRequestDto.setReferenceId(partnerId);
		cryptomanagerRequestDto.setData(data);
		cryptomanagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		
		HttpEntity<RequestWrapper<CryptomanagerRequestDto>> httpEntity = new HttpEntity<>(createRequest(cryptomanagerRequestDto));
		ResponseEntity<Map> response = restTemplate.exchange(decryptURL, HttpMethod.POST, httpEntity, Map.class);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			String responseData = (String) ((Map<String, Object>) response.getBody().get("response")).get("data");
			return new String (CryptoUtil.decodeBase64(responseData), StandardCharsets.UTF_8);
		}
		return null;
	}
	
	/**
	 * Generate auth token.
	 *
	 * @return the string
	 */
	public String generateAuthToken() {
		ObjectNode requestBody = objMapper.createObjectNode();
		requestBody.put("clientId", env.getProperty("auth-token-generator.rest.clientId"));
		requestBody.put("secretKey", env.getProperty("auth-token-generator.rest.secretKey"));
		requestBody.put("appId", env.getProperty("auth-token-generator.rest.appId"));
		RequestWrapper<ObjectNode> request = new RequestWrapper<>();
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setRequest(requestBody);
		ClientResponse response = WebClient.create(env.getProperty("auth-token-generator.rest.uri")).post()
				.syncBody(request)
				.exchange().block();
		logger.info("sessionID", "IDA", "DECRYPT", "AuthResponse :" +  response.toEntity(String.class).block().getBody());
		List<ResponseCookie> list = response.cookies().get("Authorization");
		if(list != null && !list.isEmpty()) {
			ResponseCookie responseCookie = list.get(0);
			return responseCookie.getValue();
		}
		return "";
	}
	
	/**
	 * Creates the request.
	 *
	 * @param <T> the generic type
	 * @param t the t
	 * @return the request wrapper
	 */
	public static <T> RequestWrapper<T> createRequest(T t){
    	RequestWrapper<T> request = new RequestWrapper<>();
    	request.setRequest(t);
    	request.setId("ida");
    	request.setRequesttime(DateUtils.getUTCCurrentDateTime());
    	return request;
    }

}

package io.mosip.kernel.responsesignature.api;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.responsesignature.api.dto.KeymanagerPublicKeyResponseDto;


@SpringBootApplication
public class App
{
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@Bean
	CommandLineRunner runnner() {
		return args->{
			testDecrypt();
		};
	}
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/**
	 * Keymanager URL to Get PublicKey
	 */
	@Value("${mosip.kernel.keymanager-service-publickey-url:http://localhost:8088/keymanager/publickey/{applicationId}}")
	private String getPublicKeyUrl;

	public void testDecrypt() {
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("applicationId", "KERNEL");
		String localDateTime = "2019-09-09T09:09:09.001Z";
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getPublicKeyUrl)
				.queryParam("timeStamp", localDateTime).queryParam("referenceId", "KER");

		ResponseEntity<String> response = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(),
				HttpMethod.GET, null, String.class);

		String responseBody = response.getBody();
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);

		KeymanagerPublicKeyResponseDto keyManagerResponseDto=null;
		ResponseWrapper<?> responseObject;

		try {
			responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
			keyManagerResponseDto = objectMapper.readValue(
					objectMapper.writeValueAsString(responseObject.getResponse()),
					KeymanagerPublicKeyResponseDto.class);
		} catch (IOException e) {

			e.printStackTrace();
		}
		PublicKey key=null; 
		try {
			 key = KeyFactory.getInstance("RSA").generatePublic(
					new X509EncodedKeySpec(CryptoUtil.decodeBase64(keyManagerResponseDto.getPublicKey())));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] hashedData=decryptor.asymmetricPublicDecrypt(key, CryptoUtil.decodeBase64("GFrZOEJyl6HTYZUhuQgv14ASvqipg9B4gbI_5vkZZmW5SawsgkmQmEgr_5O9QOECSH03Nc6WQ_WWTspYVpTUYMiqplrEdmFOK2txsWZE-Qi6vs-HZBmL79zTcFEVck5g1cnNabmD2TCpa8k7VhcAxfxWSZOoO8jZDhYaaPym84UwRJdWriky6g4yW6PJNwcKmvmbKASaJ9U1CnRnQZ0cI2jRcWpmBZEU863lSwJWOmdrsaiYVjQsjgX8O8dEoJe4p0Hdnq53kq5bpMJrwtTOJoK54nhGoksO4yq9RcQ4YP3yl_D2fv9mMEvVkcaqTqhdu5o5Q9iCIXEgI87L2KwFfQ"));
		System.out.println(hashedData);
		Assert.assertArrayEquals(CryptoUtil.decodeBase64("EFAEED20B75B06359B86985DA5C8D738A20CABF62578FB6AAAC67B05604CBCCA"), hashedData);

	}
}

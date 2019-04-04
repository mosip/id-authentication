package io.mosip.kernel.responsesignature.api;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

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
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.responsesignature.api.dto.KeymanagerPublicKeyResponseDto;

@SpringBootApplication
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	CommandLineRunner runnner() {
		return args -> {
			testDecrypt();
			//encryptMyData();
		};
	}

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;
	
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
	@Autowired
	KeyGenerator keyGen;

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

		KeymanagerPublicKeyResponseDto keyManagerResponseDto = null;
		ResponseWrapper<?> responseObject;

		try {
			responseObject = objectMapper.readValue(response.getBody(), ResponseWrapper.class);
			keyManagerResponseDto = objectMapper.readValue(
					objectMapper.writeValueAsString(responseObject.getResponse()),
					KeymanagerPublicKeyResponseDto.class);
		} catch (IOException e) {

			e.printStackTrace();
		}
		PublicKey key = null;
		try {
			key = KeyFactory.getInstance("RSA").generatePublic(
					new X509EncodedKeySpec(CryptoUtil.decodeBase64(keyManagerResponseDto.getPublicKey())));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] hashedData = decryptor.asymmetricPublicDecrypt(key, CryptoUtil.decodeBase64(
				"OK9R9yYaTnrrGsFC5-dmik1hDN1bCgzNPIznTxwn1BUBwhnJcOEKVdqhQFeaGaqKn_JR99gmjH3vLypAbPVtW1najdubWo1YfEZNKMxAGFq2pj8mPBTxX3FXI4m2y7iXhD2bNNJOxhzA1dJm9MjtJBJuNwTNq5YygfCak-lxHom_iiJUZcsDTF2H8zgfBvc4gZzjv1H9nXtZ7b1LHvasO6qnwgN1KxrJVo0306jde_7DjDQZUZcO-9azgkTEVwPvHJEpt-GvwkbJAl-8g1Lb28RHY0DtpaFbZTCAKyE31dsjO3DjUiqpIGLu3vhsCjSmu1RGOlol0XtHdEDfiziUgg"));
		System.out.println(hashedData);
		
	}
	
	public void encryptMyData() {
		KeyPair keyPair=keyGen.getAsymmetricKey();
		
		byte[] encryptedData= encryptor.asymmetricPrivateEncrypt(keyPair.getPrivate(), "urvil".getBytes());
		System.out.println(encryptedData);
		
		byte[] decryptData=decryptor.asymmetricPublicDecrypt(keyPair.getPublic(), encryptedData);
		
		System.out.println(new String(decryptData));
		
	}
}

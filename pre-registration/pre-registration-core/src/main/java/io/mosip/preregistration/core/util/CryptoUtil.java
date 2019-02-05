package io.mosip.preregistration.core.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.CryptoManagerRequestDTO;
import io.mosip.preregistration.core.common.dto.CryptoManagerResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * @author Tapaswini Behera
 * @since 1.0.0
 *
 */
@Service
public class CryptoUtil {

	private Logger log = LoggerConfiguration.logConfig(CryptoUtil.class);

	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;

	@Value("${cryptoResource.url}")
	public String cryptoResourceUrl;

	public byte[] encrypt(byte[] originalInput, LocalDateTime localDateTime) {
		log.info("sessionId", "idType", "id", "In encrypt method of CryptoUtil service - " + originalInput);

		ResponseEntity<CryptoManagerResponseDTO> response = null;
		byte[] encryptedBytes = null;
		try {
			String encodedBytes = io.mosip.kernel.core.util.CryptoUtil.encodeBase64(originalInput);
			CryptoManagerRequestDTO dto = new CryptoManagerRequestDTO();
			dto.setApplicationId("REGISTRATION");
			dto.setData(encodedBytes);
			dto.setReferenceId("");
			dto.setTimeStamp(localDateTime);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<CryptoManagerRequestDTO> request = new HttpEntity<>(dto, headers);

			response = restTemplate.exchange(cryptoResourceUrl + "/encrypt", HttpMethod.POST, request,
					CryptoManagerResponseDTO.class);
			encryptedBytes = response.getBody().getData().getBytes();
			System.out.println("encryptedBytes: " + encryptedBytes);
		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id", "In encrypt method of CryptoUtil Util for HttpClientErrorException- "
					+ ex.getResponseBodyAsString());
		}
		return encryptedBytes;

	}

	public String decrypt(byte[] originalInput, LocalDateTime localDateTime) {
		log.info("sessionId", "idType", "id", "In decrypt method of CryptoUtil service - " + originalInput);
		ResponseEntity<CryptoManagerResponseDTO> response = null;
		String decodedBytes = "";
		try {

			CryptoManagerRequestDTO dto = new CryptoManagerRequestDTO();
			dto.setApplicationId("REGISTRATION");
			dto.setData(new String(originalInput, StandardCharsets.UTF_8));
			dto.setReferenceId("");
			dto.setTimeStamp(localDateTime);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<CryptoManagerRequestDTO> request = new HttpEntity<>(dto, headers);

			response = restTemplate.exchange(cryptoResourceUrl + "/decrypt", HttpMethod.POST, request,
					CryptoManagerResponseDTO.class);
			decodedBytes = new String(Base64.decodeBase64(response.getBody().getData().getBytes()),
					StandardCharsets.UTF_8);

		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id", "In decrypt method of CryptoUtil Util for HttpClientErrorException- "
					+ ex.getResponseBodyAsString());
		}
		return decodedBytes;

	}

}

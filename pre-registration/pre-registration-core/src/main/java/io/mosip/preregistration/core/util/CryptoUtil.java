package io.mosip.preregistration.core.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.CryptoManagerRequestDTO;
import io.mosip.preregistration.core.common.dto.CryptoManagerResponseDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.EncryptionFailedException;

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
		log.info("sessionId", "idType", "id", "In encrypt method of CryptoUtil service ");

		ResponseEntity<ResponseWrapper<CryptoManagerResponseDTO>> response = null;
		byte[] encryptedBytes = null;
		try {
			String encodedBytes = io.mosip.kernel.core.util.CryptoUtil.encodeBase64(originalInput);
			CryptoManagerRequestDTO dto = new CryptoManagerRequestDTO();
			dto.setApplicationId("REGISTRATION");
			dto.setData(encodedBytes);
			dto.setReferenceId("");
			dto.setTimeStamp(localDateTime);
			RequestWrapper<CryptoManagerRequestDTO> requestKernel = new RequestWrapper<>();
			requestKernel.setRequest(dto);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<RequestWrapper<CryptoManagerRequestDTO>> request = new HttpEntity<>(requestKernel, headers);
			log.info("sessionId", "idType", "id",
					"In encrypt method of CryptoUtil service cryptoResourceUrl: " + cryptoResourceUrl + "/encrypt");
			response = restTemplate.exchange(cryptoResourceUrl + "/encrypt", HttpMethod.POST, request,
					new ParameterizedTypeReference<ResponseWrapper<CryptoManagerResponseDTO>>() {
					});
			if (!(response.getBody().getErrors() == null || response.getBody().getErrors().isEmpty())) {
				throw new EncryptionFailedException(response.getBody().getErrors(), null);
			}
			encryptedBytes = response.getBody().getResponse().getData().getBytes();
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In encrypt method of CryptoUtil Util for Exception- " + ex.getMessage());
			throw ex;
		}
		return encryptedBytes;

	}

	public byte[] decrypt(byte[] originalInput, LocalDateTime localDateTime) {
		log.info("sessionId", "idType", "id", "In decrypt method of CryptoUtil service ");
		ResponseEntity<ResponseWrapper<CryptoManagerResponseDTO>> response = null;
		byte[] decodedBytes = null;
		try {

			CryptoManagerRequestDTO dto = new CryptoManagerRequestDTO();
			dto.setApplicationId("REGISTRATION");
			dto.setData(new String(originalInput, StandardCharsets.UTF_8));
			dto.setReferenceId("");
			dto.setTimeStamp(localDateTime);
			RequestWrapper<CryptoManagerRequestDTO> requestKernel = new RequestWrapper<>();
			requestKernel.setRequest(dto);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<RequestWrapper<CryptoManagerRequestDTO>> request = new HttpEntity<>(requestKernel, headers);
			log.info("sessionId", "idType", "id",
					"In decrypt method of CryptoUtil service cryptoResourceUrl: " + cryptoResourceUrl + "/decrypt");
			response = restTemplate.exchange(cryptoResourceUrl + "/decrypt", HttpMethod.POST, request,
					new ParameterizedTypeReference<ResponseWrapper<CryptoManagerResponseDTO>>() {
					});
			if (!(response.getBody().getErrors() == null || response.getBody().getErrors().isEmpty())) {
				throw new EncryptionFailedException(response.getBody().getErrors(), null);
			}
			decodedBytes = Base64.decodeBase64(response.getBody().getResponse().getData().getBytes());

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In decrypt method of CryptoUtil Util for Exception- " + ex.getMessage());
			throw ex;
		}
		return decodedBytes;

	}

}

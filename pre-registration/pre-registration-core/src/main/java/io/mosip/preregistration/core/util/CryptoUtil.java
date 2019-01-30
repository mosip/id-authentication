package io.mosip.preregistration.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.CryptoManagerRequestDTO;
import io.mosip.preregistration.core.common.dto.CryptoManagerResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;

@Service
public class CryptoUtil {

	private Logger log = LoggerConfiguration.logConfig(CryptoUtil.class);

	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;

	// @Value("${cryptoResource.url}")
	public String cryptoResourceUrl = "https://integ.mosip.io/cryptomanager/v1.0";

	public byte[] encrypt(String originalInput) {
		ResponseEntity<CryptoManagerResponseDTO> response = null;
		try {
			byte[] encodedBytes = Base64.encodeBase64(originalInput.getBytes());

			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String date1 = "2018-12-10 06:12:52";
			LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);

			CryptoManagerRequestDTO dto = new CryptoManagerRequestDTO();
			dto.setApplicationId("REGISTRATION");
			dto.setData(new String(encodedBytes));
			dto.setReferenceId("");
			dto.setTimeStamp(localDateTime1);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<CryptoManagerRequestDTO> request = new HttpEntity<>(dto, headers);

			response = restTemplate.exchange(cryptoResourceUrl+"/encrypt", HttpMethod.POST, request,
					CryptoManagerResponseDTO.class);
			 System.out.println("response:"+response.getBody().getData().toString());

		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			ex.printStackTrace();
		}
		return response.getBody().getData().getBytes();

	}
	
	public byte[] decrypt(String originalInput) {
		ResponseEntity<CryptoManagerResponseDTO> response = null;
		try {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String date1 = "2018-12-10 06:12:52";
			LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);

			CryptoManagerRequestDTO dto = new CryptoManagerRequestDTO();
			dto.setApplicationId("REGISTRATION");
			dto.setData(originalInput);
			dto.setReferenceId("");
			dto.setTimeStamp(localDateTime1);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<CryptoManagerRequestDTO> request = new HttpEntity<>(dto, headers);

			response = restTemplate.exchange(cryptoResourceUrl+"/decrypt", HttpMethod.POST, request,
					CryptoManagerResponseDTO.class);
			// System.out.println("response"+response.getBody().getData().toString());
			byte[] decodedBytes = Base64.decodeBase64(response.getBody().getData().getBytes());
			System.out.println("decodedBytes " + new String(decodedBytes));
		} catch (HttpClientErrorException ex) {
			log.error("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getResponseBodyAsString());
			ex.printStackTrace();
		}
		return response.getBody().getData().getBytes();

	}

}

package io.mosip.kernel.syncdata.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.syncdata.dto.CryptoManagerRequestDto;
import io.mosip.kernel.syncdata.dto.CryptoManagerResponseDto;

/**
 * HashUtil which wraps HMACUtil to hashes the response.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class HashUtil {

	@Autowired
	RestTemplate restTemplate;
	
	@Value("${mosip.kernel.syncdata.syncdata-request-id:SYNCDATA.REQUEST}")
	private String syncDataRequestId;

	@Value("${mosip.kernel.syncdata.syncdata-version-id:v1.0}")
	private String syncDataVersionId;
	
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Hash data.
	 *
	 * @param response
	 *            the response
	 * @return digestasplainText {@link Stirng}
	 */
	public String hashData(String response) {

		byte[] responseByteArray = HMACUtils.generateHash(response.getBytes());
		
		String hashAsPlainText=HMACUtils.digestAsPlainText(responseByteArray);
		System.out.println(CryptoUtil.encodeBase64(hashAsPlainText.getBytes()));
		CryptoManagerRequestDto cryptoManagerRequestDto= new CryptoManagerRequestDto();
		cryptoManagerRequestDto.setApplicationId("KERNEL");
		cryptoManagerRequestDto.setReferenceId("KER");
		cryptoManagerRequestDto.setData(CryptoUtil.encodeBase64(hashAsPlainText.getBytes()));
		cryptoManagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		RequestWrapper<CryptoManagerRequestDto> requestWrapper= new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);
		ResponseEntity<String> responseEntity=null;
		try {
		 responseEntity=restTemplate.postForEntity("http://localhost:8087/cryptomanager/encrypt/private", requestWrapper, String.class);
		
		}
		catch(RestClientException ex) {
			ex.printStackTrace();
		}
		
		CryptoManagerResponseDto cryptoManagerResponseDto=null;
		ResponseWrapper<CryptoManagerResponseDto> responseObject;
		try {

			responseObject = objectMapper.readValue(responseEntity.getBody(),
					new TypeReference<ResponseWrapper<CryptoManagerResponseDto>>() {
					});

			cryptoManagerResponseDto = responseObject.getResponse();
		} catch (IOException | NullPointerException exception) {
			//TODO throw exception
		}
		if(cryptoManagerResponseDto.getData()==null) {
			//TODO throw exception
		}
		return cryptoManagerResponseDto.getData();

	}

}

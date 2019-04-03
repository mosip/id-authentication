package io.mosip.kernel.syncdata.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.syncdata.dto.CryptoManagerRequestDto;

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

	/**
	 * Hash data.
	 *
	 * @param response
	 *            the response
	 * @return digestasplainText {@link Stirng}
	 */
	public String hashData(String response) {

		byte[] responseByteArray = HMACUtils.generateHash(response.getBytes());
		System.out.println(HMACUtils.digestAsPlainText(responseByteArray));
		String hashAsPlainText=HMACUtils.digestAsPlainText(responseByteArray);
		CryptoManagerRequestDto cryptoManagerRequestDto= new CryptoManagerRequestDto();
		cryptoManagerRequestDto.setApplicationId("KERNEL");
		cryptoManagerRequestDto.setReferenceId("KER");
		cryptoManagerRequestDto.setData(hashAsPlainText);
		cryptoManagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		RequestWrapper<CryptoManagerRequestDto> requestWrapper= new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);
		try {
		ResponseEntity<String> responseEntity=restTemplate.postForEntity("http://localhost:8087/cryptomanager/encrypt/private", requestWrapper, String.class);
		}
		catch(RestClientException ex) {
			ex.printStackTrace();
		}
		return HMACUtils.digestAsPlainText(responseByteArray);

	}

}

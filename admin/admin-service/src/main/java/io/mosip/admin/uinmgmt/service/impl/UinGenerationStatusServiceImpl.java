package io.mosip.admin.uinmgmt.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.uinmgmt.constant.UinGenerationStatusErrorCode;
import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.admin.uinmgmt.exception.UinGenerationStatusException;
import io.mosip.admin.uinmgmt.service.UinGenerationStatusService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;

/**
 * @author Sidhant Agarwal
 * @snce 1.0.0
 *
 */
@Service
public class UinGenerationStatusServiceImpl implements UinGenerationStatusService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.admin.packetstatus.api}")
	private String getPacketStatusApi;

	@Autowired
	ObjectMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.admin.uinmgmt.service.UinGenerationStatusService#getPacketStatus(
	 * java.lang.String)
	 */
	@Override
	public ResponseWrapper<List<UinGenerationStatusDto>> getPacketStatus(String rid) {
		String answer;
		RequestWrapper<List<UinGenerationStatusDto>> request = new RequestWrapper<>();
		request.setId("mosip.registration.status");
		request.setVersion("1.0");
		request.setRequesttime(LocalDateTime.now(ZoneId.of("UTC")));

		List<UinGenerationStatusDto> uingen = new ArrayList<>();
		UinGenerationStatusDto uin = new UinGenerationStatusDto();
		uin.setRegistrationId(rid);
		uingen.add(uin);
		request.setRequest(uingen);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<RequestWrapper<List<UinGenerationStatusDto>>> entity = new HttpEntity<>(request, headers);
		try {
			answer = restTemplate.postForObject(getPacketStatusApi, entity, String.class);
		} catch (RestClientException e) {
			throw new UinGenerationStatusException(
					UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorCode(),
					UinGenerationStatusErrorCode.UIN_GENERATION_STATUS_EXCEPTION.getErrorMessage(), e);
		}

		ResponseWrapper<List<UinGenerationStatusDto>> responseObject = null;
		try {

			responseObject = mapper.readValue(answer,
					new TypeReference<ResponseWrapper<List<UinGenerationStatusDto>>>() {
					});
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(UinGenerationStatusErrorCode.PARSE_EXCEPTION.getErrorCode(),
					UinGenerationStatusErrorCode.PARSE_EXCEPTION.getErrorMessage() + exception.getMessage(), exception);
		}

		return responseObject;
	}
}

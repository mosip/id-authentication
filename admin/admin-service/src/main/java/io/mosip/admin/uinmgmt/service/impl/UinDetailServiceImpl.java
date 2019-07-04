package io.mosip.admin.uinmgmt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.uinmgmt.constant.UinDetailErrorCode;
import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;
import io.mosip.admin.uinmgmt.exception.UinDetailNotFoundException;
import io.mosip.admin.uinmgmt.service.UinDetailService;

/**
 * Service Impl class for Uin Status Service
 * 
 * @author Megha Tanga
 *
 */
@Service
@RefreshScope
public class UinDetailServiceImpl implements UinDetailService {

	/**
	 * RestTemplate object
	 */
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Field to hold the URL
	 */
	@Value("${mosip.admin.uinmgmt.uin-detail-search}")
	String uinDetailUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.admin.uinmgmt.service.UinStatusService#getUinDetailsNew(java.lang.
	 * String)
	 */
	public UinDetailResponseDto getUinDetails(String uin) {
		UinDetailResponseDto uinDetailResponseDto = new UinDetailResponseDto();
		try {
			uinDetailResponseDto = restTemplate.getForObject(uinDetailUrl, UinDetailResponseDto.class, uin);
		} catch (RestClientException e) {
			throw new UinDetailNotFoundException(UinDetailErrorCode.REST_SERVICE_EXCEPTION.getErrorCode(),
					UinDetailErrorCode.REST_SERVICE_EXCEPTION.getErrorMessage() + e.getMessage(), e);

		}
		if (uinDetailResponseDto == null || uinDetailResponseDto.getResponse() == null) {
			throw new UinDetailNotFoundException(UinDetailErrorCode.INVAVIDE_UIN.getErrorCode(),
					UinDetailErrorCode.INVAVIDE_UIN.getErrorMessage());
		}
		return uinDetailResponseDto;

	}

}

package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterHistoryService;
import io.swagger.annotations.Api;

/**
 * Controller with api to fetch registration center history
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "RegistrationCenterHistory" })
public class RegistrationCenterHistoryController {

	/**
	 * {@link RegistrationCenterHistoryService} instance
	 */
	@Autowired
	RegistrationCenterHistoryService registrationCenterHistoryService;

	/**
	 * Get api to fetch list of registration centers
	 * 
	 * @param registrationCenterId
	 *            The id of registration center
	 * @param languageCode
	 *            The language code
	 * @param effectiveDate
	 *            The effective date
	 * @return {@link RegistrationCenterResponseDto} instance
	 */
	@GetMapping("/v1.0/registrationcentershistory/{registrationCenterId}/{langcode}/{effectiveDate}")
	public RegistrationCenterHistoryResponseDto getRegistrationCentersHistory(
			@PathVariable("registrationCenterId") String registrationCenterId, @PathVariable("langcode") String langCode,
			@PathVariable("effectiveDate") String effectiveDate) {

		return registrationCenterHistoryService.getRegistrationCenterHistory(registrationCenterId, langCode,
				effectiveDate);
	}

}

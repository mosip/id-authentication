package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.postresponse.UserDetailsHistoryResponseDto;
import io.mosip.kernel.masterdata.service.UserDetailsHistoryService;
import io.swagger.annotations.Api;

/**
 * Controller class for user details
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "User Details" })
public class UserDetailsHistoryController {

	@Autowired
	UserDetailsHistoryService userDetailsHistoryService;

	@ResponseFilter
	@GetMapping(value = "/users/{id}/{eff_dtimes}")
	@PreAuthorize("hasRole('REGISTRATION_PROCESSOR')")
	public ResponseWrapper<UserDetailsHistoryResponseDto> getTitlesBylangCode(@PathVariable("id") String userId,
			@PathVariable("eff_dtimes") String date) {
		ResponseWrapper<UserDetailsHistoryResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(userDetailsHistoryService.getByUserIdAndTimestamp(userId, date));
		return responseWrapper;
	}

}

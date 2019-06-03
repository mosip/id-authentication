package io.mosip.kernel.smsnotification.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.notification.spi.SmsNotification;
import io.mosip.kernel.smsnotification.dto.SmsRequestDto;
import io.mosip.kernel.smsnotification.dto.SmsResponseDto;

/**
 * This controller class receives contact number and message in data transfer
 * object and sends SMS on the provided contact number.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */

@CrossOrigin
@RestController
public class SmsNotificationController {

	/**
	 * The reference that autowire sms notification service class.
	 */
	@Autowired
	SmsNotification<SmsResponseDto> smsNotifierService;

	/**
	 * This method sends sms to the contact number provided.
	 * 
	 * @param smsRequestDto the request dto for sms-notification.
	 * @return the status and message as dto response.
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','ID_AUTHENTICATION','AUTH', 'PRE_REGISTRATION_ADMIN','PRE_REGISTRATION_ADMIN')")
	@ResponseFilter
	@PostMapping(value = "/sms/send")
	public ResponseWrapper<SmsResponseDto> sendSmsNotification(
			@Valid @RequestBody RequestWrapper<SmsRequestDto> smsRequestDto) {
		ResponseWrapper<SmsResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(smsNotifierService.sendSmsNotification(smsRequestDto.getRequest().getNumber(),
				smsRequestDto.getRequest().getMessage()));
		return responseWrapper;
	}
}

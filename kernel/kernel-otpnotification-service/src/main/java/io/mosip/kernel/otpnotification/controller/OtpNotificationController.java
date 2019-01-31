package io.mosip.kernel.otpnotification.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.otpnotification.spi.OtpNotification;
import io.mosip.kernel.otpnotification.dto.OtpNotificationRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpNotificationResponseDto;
import io.swagger.annotations.ApiOperation;

/**
 * Controller class for OTP notification.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RestController
public class OtpNotificationController {

	/**
	 * Reference to OtpNotification.
	 */
	@Autowired
	private OtpNotification<OtpNotificationResponseDto,OtpNotificationRequestDto> otpNotificationService;

	/**
	 * Api to notify with OTP to user.
	 * 
	 * @param request
	 *            the request dto.
	 * @return the response entity.
	 */
	@PostMapping(value = "/v1.0/otpnotification/send")
	@ApiOperation(value = "Service to send OTP notification", response = OtpNotificationResponseDto.class)
	public ResponseEntity<OtpNotificationResponseDto> sendOtpNotification(
			@Valid @RequestBody OtpNotificationRequestDto request) {

		return new ResponseEntity<>(otpNotificationService.sendOtpNotification(request), HttpStatus.OK);
	}

}

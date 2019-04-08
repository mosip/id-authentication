package io.mosip.kernel.otpnotification.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
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
@CrossOrigin
public class OtpNotificationController {

	/**
	 * Reference to {@link OtpNotification}.
	 */
	@Autowired
	private OtpNotification<OtpNotificationResponseDto, OtpNotificationRequestDto> otpNotificationService;

	/**
	 * API to notify with OTP to user.
	 * 
	 * @param otpNotificationRequestDto the request dto.
	 * @return the response entity.
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER')")
	@ResponseFilter
	@PostMapping(value = "/otp/send")
	@ApiOperation(value = "Service to send OTP notification")
	public ResponseWrapper<OtpNotificationResponseDto> sendOtpNotification(
			@Valid @RequestBody RequestWrapper<OtpNotificationRequestDto> otpNotificationRequestDto,
			HttpServletRequest request) {
		Enumeration<String> names = request.getHeaderNames();
		System.out.println("\n\n ***** Inside Filter Check ***** \n\n");
		System.out.println("URL : " + request.getRequestURL());
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			System.out.println(name + " : " + request.getHeader(name));
		}
		System.out.println("\n\n");
		ResponseWrapper<OtpNotificationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(otpNotificationService.sendOtpNotification(otpNotificationRequestDto.getRequest()));
		return responseWrapper;
	}
}

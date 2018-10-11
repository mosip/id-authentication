package io.mosip.kernel.smsnotifier.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.spi.smsnotifier.SmsNotifier;
import io.mosip.kernel.smsnotifier.dto.SmsResponseDto;
import io.mosip.kernel.smsnotifier.dto.SmsRequestDto;

/**
 * This controller class receives contact number and message in data transfer
 * object and sends SMS on the provided contact number.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */

@CrossOrigin
@RestController
@RequestMapping(value = "/smsnotifier")
public class SmsNotifierController {

	/**
	 * The reference that autowire sms notification service class.
	 */
	@Autowired
	SmsNotifier<SmsResponseDto> service;

	/**
	 * This method sends sms to the contact number provided.
	 * 
	 * @param smsRequestDto
	 *            the request dto for sms-notification.
	 * @return the status and message as dto response.
	 */
	@PostMapping(value = "/texts")
	public ResponseEntity<SmsResponseDto> sendSms(@Valid @RequestBody SmsRequestDto smsRequestDto) {

		return new ResponseEntity<>(service.sendSmsNotification(smsRequestDto.getNumber(), smsRequestDto.getMessage()),
				HttpStatus.ACCEPTED);

	}

}

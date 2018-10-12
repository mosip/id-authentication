package io.mosip.kernel.smsnotifier.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.spi.smsnotifier.SmsNotifier;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.kernel.smsnotifier.constant.SmsExceptionConstants;
import io.mosip.kernel.smsnotifier.dto.SmsRequestDto;
import io.mosip.kernel.smsnotifier.dto.SmsResponseDto;
import io.mosip.kernel.smsnotifier.exception.JsonParseException;

/**
 * This controller class receives contact number and message in data transfer
 * object and sends SMS on the provided contact number.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */

@CrossOrigin
@RestController
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
	@PostMapping(value = "/notifier/sms")
	public ResponseEntity<SmsResponseDto> sendSms(@Valid @RequestBody SmsRequestDto smsRequestDto) {
		SmsResponseDto smsResponseDto = null;
		try {
			smsResponseDto = service.sendSmsNotification(smsRequestDto.getNumber(), smsRequestDto.getMessage());

		} catch (MosipJsonParseException | MosipJsonMappingException | MosipIOException e) {

			throw new JsonParseException(SmsExceptionConstants.SMS_EMPTY_JSON.getErrorCode(),
					SmsExceptionConstants.SMS_EMPTY_JSON.getErrorMessage(), e.getCause());
		}
		return new ResponseEntity<>(smsResponseDto, HttpStatus.ACCEPTED);

	}

}

package io.mosip.registration.processor.message.sender.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import io.mosip.registration.processor.message.sender.dto.MessageNotificationRequest;
import io.mosip.registration.processor.message.sender.dto.MessageNotificationResponse;
import io.mosip.registration.processor.message.sender.service.MessageNotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;


		@RestController
		@RequestMapping("/v0.1/registration-processor/message-sender")
		@Api(tags = "Message-Sender")
		public class MessageNotificationController {

		private MessageNotificationService notificationService;
			
			@PostMapping(value = "/sendSms")
			@ApiResponse(code = 200, message = "status successfully updated")

			public ResponseEntity<MessageNotificationResponse> sendSms(@RequestBody(required = true) MessageNotificationRequest messageNotificationRequest) {
				
				MessageNotificationResponse smsResponseDto=notificationService.sendSmsNotification(messageNotificationRequest);
				return ResponseEntity.status(HttpStatus.OK).body(smsResponseDto);
			}
		 
		
		
			@PostMapping(value = "/sendEmail")
			@ApiResponse(code = 200, message = "sent Email successfully ")
		public ResponseEntity<MessageNotificationResponse> sendEmail(@RequestBody(required = true) MessageNotificationRequest messageNotificationRequest) {
			
			MessageNotificationResponse smsResponseDto=notificationService.sendEmailNotification(messageNotificationRequest);
			return ResponseEntity.status(HttpStatus.OK).body(smsResponseDto);
		}
		
		}
	 




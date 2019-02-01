package io.mosip.preregistration.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.notification.dto.QRCodeResponseDTO;
import io.mosip.preregistration.notification.service.NotificationService;
import io.swagger.annotations.ApiOperation;

/**
 * Controller class for notification triggering.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v0.1/pre-registration/")
@CrossOrigin("*")
public class NotificationController {

	/**
	 * Reference to {@link NotificationService}.
	 */
	@Autowired
	private NotificationService notificationService;

	/**
	 * Api to Trigger notification.
	 * 
	 * @param jsonbObject
	 *            the json string.
	 * @param langCode
	 *            the language code.
	 * @param file
	 *            the file to send.
	 * @return the response entity.
	 */
	@PostMapping(path = "/notification", consumes = {
			"multipart/form-data" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Trigger notification")
	public ResponseEntity<MainResponseDTO<NotificationDTO>> sendNotification(
			@RequestPart(value = "NotificationDTO", required = true) String jsonbObject,
			@RequestPart(value = "langCode", required = true) String langCode,
			@RequestPart(value = "file", required = false) MultipartFile file) {
		System.out.println("======="+jsonbObject+"================");
		return new ResponseEntity<>(notificationService.sendNotification(jsonbObject, langCode, file), HttpStatus.OK);
	}
	
	/**
	 * @param Json Stirng data
	 * @return the response entity
	 */
	@PostMapping(path="/generateQRCode")
	public ResponseEntity<MainResponseDTO<QRCodeResponseDTO>> generateQRCode(@RequestBody String data) {
		
		return  new ResponseEntity<>( notificationService.generateQRCode(data),HttpStatus.OK);
		
	}
}

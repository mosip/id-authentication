package io.mosip.preregistration.notification.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for notification triggering.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@RestController
@RequestMapping("/")
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
	@PostMapping(path = "/notify", consumes = {
			"multipart/form-data" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Trigger notification")
	public ResponseEntity<MainResponseDTO<NotificationDTO>> sendNotification(
			@RequestPart(value = "NotificationDTO", required = true) String jsonbObject,
			@RequestPart(value = "langCode", required = true) String langCode,
			@RequestPart(value = "file", required = false) MultipartFile file) {
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
	
	/**
	 *
	 * @return the response entity
	 */
	@GetMapping(path="/config" ,produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get global and Pre-Registration config data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "global and Pre-Registration config data successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the global and Pre-Registration config data") })
	public ResponseEntity<MainResponseDTO<Map<String,String>>> configParams() {
		return  new ResponseEntity<>( notificationService.getConfig(),HttpStatus.OK);
		
	}
}

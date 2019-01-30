package io.mosip.preregistration.acknowledgement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.preregistration.acknowledgement.dto.AcknowledgementDTO;
import io.mosip.preregistration.acknowledgement.service.AcknowledgementService;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.swagger.annotations.ApiOperation;

/**
 * @author Sanober Noor
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v0.1/pre-registration/")
public class AcknowledgementController {

	@Autowired
	private AcknowledgementService acknowledgementService;

	@PostMapping(path = "/notification",  consumes = { "multipart/form-data" },produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Trigger notification")
	
	public ResponseEntity<MainListResponseDTO<AcknowledgementDTO>> acknowledgementNotifier(@RequestPart (value = "AcknowledgementDTO", required = true)String jsonbObject,
			@RequestPart(value = "langCode", required = true) String langCode,@RequestPart(value = "file", required = true)  MultipartFile file){
	
	return new ResponseEntity<>(acknowledgementService.acknowledgementNotifier(jsonbObject, langCode, file),
			HttpStatus.OK);
	}
}

package io.mosip.registration.processor.manual.adjudication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.mosip.registration.processor.manual.adjudication.dto.ApplicantDetailsDto;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/manual-adjudication")
@Api(tags = "Manual Adjudication")
public class ManualAdjudicationController {
	@Autowired
	private ManualAdjudicationService manualAdjudicationService;
	
	@PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponse(code = 200, message = "status successfully updated")
	public ResponseEntity<ApplicantDetailsDto> assignStatusController(@RequestBody(required = true) UserDto userDto) {
		
		ApplicantDetailsDto dto = manualAdjudicationService.assignStatus(userDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}	
	
	@PostMapping(value="/applicantFiles", produces = MediaType.IMAGE_JPEG_VALUE)
	@ApiResponses({
		@ApiResponse(code = 200, message = "status successfully updated"),
		@ApiResponse(code = 400, message = "Invalid file requested"),
		@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public ResponseEntity<byte[]> getApplicantFile(String regId, String fileName) {
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(regId, fileName);
		return ResponseEntity.ok().body(packetInfo);
	}
	
	@PostMapping(value="/applicantData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({
		@ApiResponse(code = 200, message = "status successfully updated"),
		@ApiResponse(code = 400, message = "Invalid file requested"),
		@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public ResponseEntity<byte[]> getApplicantData(String regId, String fileName) {
		byte[] packetInfo = manualAdjudicationService.getApplicantData(regId, fileName);
		return ResponseEntity.ok().body(packetInfo);
	}
		

}

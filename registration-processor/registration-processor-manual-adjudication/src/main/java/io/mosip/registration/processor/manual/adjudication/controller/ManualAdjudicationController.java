package io.mosip.registration.processor.manual.adjudication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.manual.adjudication.dto.FileRequestDto;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The controller class for Manual Adjudication
 * 
 * @author Shuchita
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
@RestController
@RequestMapping("/v0.1/registration-processor/manual-adjudication")
@Api(tags = "Manual Adjudication")
@CrossOrigin
public class ManualAdjudicationController {
	
	@Autowired
	private ManualAdjudicationService manualAdjudicationService;

	@PostMapping(path = "/assignment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponse(code = 200, message = "status successfully updated")
	public ResponseEntity<ManualVerificationDTO> assignApplicant(@RequestBody(required = true) UserDto userDto) {
		ManualVerificationDTO manualVerificationDTO = manualAdjudicationService.assignStatus(userDto);
		return ResponseEntity.status(HttpStatus.OK).body(manualVerificationDTO);
	}
	
	@PostMapping(path = "/decision", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponse(code = 200, message = "status successfully updated")
	public ResponseEntity<ManualVerificationDTO> updatePacketStatus(@RequestBody(required = true) ManualVerificationDTO manualVerificationDTO) {
		ManualVerificationDTO updatedManualVerificationDTO = manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
		return ResponseEntity.status(HttpStatus.OK).body(updatedManualVerificationDTO);
	}

	@PostMapping(value = "/applicantFiles")
	@ApiResponses({ @ApiResponse(code = 200, message = "file fetching successful"),
			@ApiResponse(code = 400, message = "Invalid file requested"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<byte[]> getApplicantDemographic(@RequestBody(required=true)FileRequestDto dto) {
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(dto.getRegId(),dto.getFileName());
		return ResponseEntity.status(HttpStatus.OK).body(packetInfo);
	}

	@PostMapping(value = "/applicantData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = "data fetching successful"),
			@ApiResponse(code = 400, message = "Invalid file requested"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<byte[]> getApplicantBiometric(@RequestBody(required=true)FileRequestDto dto) {
		byte[] packetInfo = manualAdjudicationService.getApplicantData(dto.getRegId(),dto.getFileName());
		return ResponseEntity.status(HttpStatus.OK).body(packetInfo);
	}

}

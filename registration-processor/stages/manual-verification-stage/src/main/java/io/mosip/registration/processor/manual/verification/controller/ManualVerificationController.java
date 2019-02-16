/*package io.mosip.registration.processor.manual.verification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.dto.FileRequestDto;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.PacketInfoRequestDto;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationAppException;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationValidationException;
import io.mosip.registration.processor.manual.verification.request.dto.ManualAppBiometricRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualAppDemographicRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualVerificationAssignmentRequestDTO;
import io.mosip.registration.processor.manual.verification.request.dto.ManualVerificationDecisionRequestDTO;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationRequestValidator;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationValidationUtil;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.validator.RegistrationStatusRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

*//**
 * The controller class for Manual Adjudication.
 *
 * @author Shuchita
 * @author Pranav Kumar
 * @author Rishabh Keshari
 * @since 0.0.1
 *//*
@RestController
@RequestMapping("/v0.1/registration-processor/manual-verification")
@Api(tags = "Manual Adjudication")
@CrossOrigin
public class ManualVerificationController {

	*//** The manual adjudication service. *//*
	@Autowired
	private ManualVerificationService manualAdjudicationService;

	*//** The validator. *//*
	@Autowired
	private ManualVerificationRequestValidator validator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}
	*//**
	 * Assign applicant.
	 *
	 * @param userDto the user dto
	 * @return the response entity
	 * @throws ManualVerificationAppException 
	 *//*
	@PostMapping(path = "/assignment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponse(code = 200, message = "status successfully updated")
	public ResponseEntity<ManualVerificationDTO> assignApplicant(@Validated @RequestBody(required = true) ManualVerificationAssignmentRequestDTO assignmentDto,@ApiIgnore Errors errors) throws ManualVerificationAppException {
		try {
			ManualVerificationValidationUtil.validate(errors);
			ManualVerificationDTO manualVerificationDTO = manualAdjudicationService.assignApplicant(assignmentDto.getRequest());
			return ResponseEntity.status(HttpStatus.OK).body(manualVerificationDTO);
		}catch(ManualVerificationValidationException e) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}

	*//**
	 * Update packet status.
	 *
	 * @param manualVerificationDTO the manual verification DTO
	 * @return the response entity
	 * @throws ManualVerificationAppException 
	 *//*
	@PostMapping(path = "/decision", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponse(code = 200, message = "status successfully updated")
	public ResponseEntity<ManualVerificationDTO> updatePacketStatus(@Validated @RequestBody(required = true) ManualVerificationDecisionRequestDTO decisionDTO,@ApiIgnore Errors errors) throws ManualVerificationAppException {

		try {
			ManualVerificationValidationUtil.validate(errors);

			ManualVerificationDTO updatedManualVerificationDTO = manualAdjudicationService.updatePacketStatus(decisionDTO.getRequest());
			return ResponseEntity.status(HttpStatus.OK).body(updatedManualVerificationDTO);
		}catch(ManualVerificationValidationException e) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}

	*//**
	 * Gets the applicant biometric.
	 *
	 * @param dto the dto
	 * @return the applicant biometric
	 * @throws ManualVerificationAppException 
	 *//*
	@PostMapping(value = "/applicantBiometric")
	@ApiResponses({ @ApiResponse(code = 200, message = "file fetching successful"),
		@ApiResponse(code = 400, message = "Invalid file requested"),
		@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<byte[]> getApplicantBiometric(@Validated @RequestBody(required=true)ManualAppBiometricRequestDTO applicantBiometricDto,@ApiIgnore Errors errors) throws ManualVerificationAppException {
		try {
			ManualVerificationValidationUtil.validate(errors);

			byte[] packetInfo = manualAdjudicationService.getApplicantFile(applicantBiometricDto.getRequest().getRegId(),applicantBiometricDto.getRequest().getFileName());
			return ResponseEntity.status(HttpStatus.OK).body(packetInfo);
		}catch(ManualVerificationValidationException e) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}

	*//**
	 * Gets the applicant demographic.
	 *
	 * @param packetInfoRequestDto the packet info request dto
	 * @return the applicant demographic
	 * @throws ManualVerificationAppException 
	 *//*
	@PostMapping(value = "/applicantDemographic", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = "data fetching successful"),
		@ApiResponse(code = 400, message = "Invalid file requested"),
		@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<byte[]> getApplicantDemographic(@Validated  @RequestBody(required=true)ManualAppDemographicRequestDTO applicantDemographicDto,@ApiIgnore Errors errors) throws ManualVerificationAppException {
		try {
			ManualVerificationValidationUtil.validate(errors);
			byte[] packetInfo = manualAdjudicationService.getApplicantFile(applicantDemographicDto.getRequest().getRegId(), PacketFiles.ID.name());
			return ResponseEntity.status(HttpStatus.OK).body(packetInfo);
		}catch(ManualVerificationValidationException e) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}

	*//**
	 * Gets the packet info.
	 *
	 * @param packetInfoRequestDto the packet info request dto
	 * @return the packet info
	 * @throws ManualVerificationAppException 
	 *//*
	@PostMapping(value = "/packetInfo")
	@ApiResponses({ @ApiResponse(code = 200, message = "data fetching successful"),
		@ApiResponse(code = 400, message = "Invalid file requested"),
		@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<PacketMetaInfo> getPacketInfo(@Validated @RequestBody(required=true)ManualAppDemographicRequestDTO packetInfoRequestDto,@ApiIgnore Errors errors) throws ManualVerificationAppException {
		try {
			ManualVerificationValidationUtil.validate(errors);

			PacketMetaInfo packetInfo = manualAdjudicationService.getApplicantPacketInfo(packetInfoRequestDto.getRequest().getRegId());
			return ResponseEntity.status(HttpStatus.OK).body(packetInfo);
		}catch(ManualVerificationValidationException e) {
			throw new ManualVerificationAppException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}
}

*/
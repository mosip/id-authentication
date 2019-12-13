package io.mosip.resident.controller;

import java.io.ByteArrayInputStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.constant.AuthTypeStatus;
import io.mosip.resident.constant.RequestIdType;
import io.mosip.resident.dto.AuthHistoryRequestDTO;
import io.mosip.resident.dto.AuthHistoryResponseDTO;
import io.mosip.resident.dto.AuthLockOrUnLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.RegStatusCheckResponseDTO;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResidentReprintResponseDto;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.ResidentService;
import io.mosip.resident.validator.RequestValidator;

@RestController
public class ResidentController {

	@Autowired
	private ResidentService residentService;

	@Autowired
	private RequestValidator validator;

	@ResponseFilter
	@PostMapping(value = "/rid/check-status")
	public ResponseWrapper<RegStatusCheckResponseDTO> getRidStatus(
			@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) throws ApisResourceAccessException {
		ResponseWrapper<RegStatusCheckResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.getRidStatus(requestDTO.getRequest()));
		return response;
	}

	@PostMapping(value = "/req/euin")
	public ResponseEntity<Object> reqEuin(@Valid @RequestBody RequestWrapper<EuinRequestDTO> requestDTO)
			throws ResidentServiceCheckedException {
		validator.validateEuinRequest(requestDTO);
		byte[] pdfbytes = residentService.reqEuin(requestDTO.getRequest());

		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfbytes));

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
				.header("Content-Disposition",
						"attachment; filename=\"" + requestDTO.getRequest().getIndividualId() + ".pdf\"")
				.body((Object) resource);
	}

	@ResponseFilter
	@PostMapping(value = "/req/print-uin")
	public ResponseEntity<Object> reqPrintUin(@Valid @RequestBody RequestWrapper<ResidentReprintRequestDto> requestDTO)
			throws ResidentServiceCheckedException {
		validator.validateRequest(requestDTO, RequestIdType.RE_PRINT_ID);
		ResponseWrapper<ResidentReprintResponseDto> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqPrintUin(requestDTO.getRequest()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@ResponseFilter
	@PostMapping(value = "/req/auth-lock")
	public ResponseWrapper<ResponseDTO> reqAauthLock(
			@Valid @RequestBody RequestWrapper<AuthLockOrUnLockRequestDto> requestDTO)
			throws ResidentServiceCheckedException {
		validator.validateAuthLockOrUnlockRequest(requestDTO, AuthTypeStatus.LOCK);
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqAauthTypeStatusUpdate(requestDTO.getRequest(), AuthTypeStatus.LOCK));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/auth-unlock")
	public ResponseWrapper<ResponseDTO> reqAuthUnlock(
			@Valid @RequestBody RequestWrapper<AuthLockOrUnLockRequestDto> requestDTO)
			throws ResidentServiceCheckedException {
		validator.validateAuthLockOrUnlockRequest(requestDTO, AuthTypeStatus.UNLOCK);
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqAauthTypeStatusUpdate(requestDTO.getRequest(), AuthTypeStatus.UNLOCK));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/auth-history")
	public ResponseWrapper<AuthHistoryResponseDTO> reqAuthHistory(
			@Valid @RequestBody RequestWrapper<AuthHistoryRequestDTO> requestDTO)
			throws ResidentServiceCheckedException {
		validator.validateAuthHistoryRequest(requestDTO);
		ResponseWrapper<AuthHistoryResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqAuthHistory(requestDTO.getRequest()));
		return response;
	}

}
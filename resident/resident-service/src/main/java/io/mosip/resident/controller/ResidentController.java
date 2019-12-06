package io.mosip.resident.controller;

import java.io.ByteArrayInputStream;

import javax.validation.Valid;

import io.mosip.resident.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
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
	public ResponseWrapper<ResponseDTO> getRidStatus(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.getRidStatus(requestDTO.getRequest()));
		return response;
	}

	
	@PostMapping(value = "/req/euin")
	public ResponseEntity<Object> reqEuin(@Valid @RequestBody RequestWrapper<EuinRequestDTO> requestDTO) {

		byte[] pdfbytes = residentService.reqEuin(requestDTO.getRequest());

		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfbytes));

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
				.header("Content-Disposition",
						"attachment; filename=\"" + requestDTO.getRequest().getIndividualId() + ".pdf\"")
				.body((Object) resource);
	}

	@ResponseFilter
	@PostMapping(value = "/req/print-uin")
	public ResponseWrapper<ResponseDTO> reqPrintUin(
			@Valid @RequestBody RequestWrapper<ResidentReprintRequestDto> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqPrintUin(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/uin")
	public ResponseWrapper<ResponseDTO> reqUin(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqUin(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/rid")
	public ResponseWrapper<ResponseDTO> reqRid(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqRid(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/update-uin")
	public ResponseWrapper<ResponseDTO> reqUpdateUin(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqUpdateUin(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/vid")
	public ResponseWrapper<ResponseDTO> generatVid(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.generatVid(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PatchMapping(value = "/vid/{vid}")
	public ResponseWrapper<ResponseDTO> revokeVid(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO,
			@PathVariable String vid) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.revokeVid(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/auth-lock")
	public ResponseWrapper<ResponseDTO> reqAauthLock(
			@Valid @RequestBody RequestWrapper<AuthLockRequestDto> requestDTO) {
		validator.validateAuthLockRequest(requestDTO);
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqAauthLock(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/auth-unlock")
	public ResponseWrapper<ResponseDTO> reqAuthUnlock(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqAuthUnlock(requestDTO.getRequest()));
		return response;
	}

	@ResponseFilter
	@PostMapping(value = "/req/auth-history")
	public ResponseWrapper<ResponseDTO> reqAuthHistory(@Valid @RequestBody RequestWrapper<RequestDTO> requestDTO) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(residentService.reqAuthHistory(requestDTO.getRequest()));
		return response;
	}

}
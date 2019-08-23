package io.mosip.kernel.auditmanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.auditmanager.service.AuditManagerService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * AuditManager controller with api to add new {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
@CrossOrigin
public class AuditManagerController {
	/**
	 * AuditManager Service field with functions related to auditing
	 */
	@Autowired
	AuditManagerService service;

	/**
	 * Function to add new audit
	 * 
	 * @param requestDto
	 *            {@link AuditRequestDto} having required fields for auditing
	 * @return The {@link AuditResponseDto} having the status of audit
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION','TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','PRE_REGISTRATION','PRE_REGISTRATION_ADMIN')")
	@ResponseFilter
	@PostMapping(value = "/audits",produces= MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<AuditResponseDto> addAudit(@RequestBody @Valid RequestWrapper<AuditRequestDto> requestDto) {
		ResponseWrapper<AuditResponseDto> response = new ResponseWrapper<>();
		response.setResponse(service.addAudit(requestDto.getRequest()));
		return response;
	}
}

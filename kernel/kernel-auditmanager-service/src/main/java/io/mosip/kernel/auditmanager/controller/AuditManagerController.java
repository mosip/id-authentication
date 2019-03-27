package io.mosip.kernel.auditmanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * AuditManager controller with api to add new {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
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
	 * @param auditRequestDto
	 *            {@link AuditRequestDto} having required fields for auditing
	 * @return The {@link AuditResponseDto} having the status of audit
	 */
	@ResponseFilter
	@PostMapping(value = "/audits")
	public ResponseEntity<AuditResponseDto> addAudit(@RequestBody @Valid RequestWrapper<AuditRequestDto> requestDto) {
		return new ResponseEntity<>(service.addAudit(requestDto.getRequest()), HttpStatus.OK);
	}
}

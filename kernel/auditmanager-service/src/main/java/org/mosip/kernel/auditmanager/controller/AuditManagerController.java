package org.mosip.kernel.auditmanager.controller;

import javax.validation.Valid;

import org.mosip.kernel.auditmanager.dto.AuditResponseDto;
import org.mosip.kernel.auditmanager.model.Audit;
import org.mosip.kernel.auditmanager.request.AuditRequestDto;
import org.mosip.kernel.auditmanager.service.AuditManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	 * @param auditRequestDto
	 *            {@link AuditRequestDto} having required fields for auditing
	 * @return The {@link AuditResponseDto} having the status of audit
	 */
	@PostMapping(value = "/auditmanager/audits")
	public ResponseEntity<AuditResponseDto> addAudit(@RequestBody @Valid AuditRequestDto auditRequestDto) {

		return new ResponseEntity<>(service.addAudit(auditRequestDto), HttpStatus.CREATED);
	}

}

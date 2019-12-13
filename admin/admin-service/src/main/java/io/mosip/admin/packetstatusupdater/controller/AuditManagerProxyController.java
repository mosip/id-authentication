package io.mosip.admin.packetstatusupdater.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.packetstatusupdater.dto.AuditManagerRequestDto;
import io.mosip.admin.packetstatusupdater.dto.AuditManagerResponseDto;
import io.mosip.admin.packetstatusupdater.service.AuditManagerProxyService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * @author Megha Tanga
 */

@RestController
@RequestMapping("/auditmangaer")
public class AuditManagerProxyController {
	/**
	 * AuditManager Service field with functions related to auditing
	 */
	@Autowired
	AuditManagerProxyService auditManagerProxyService;
	
	
	/**
	 * Function to proxy service to log admin UI audit
	 * 
	 * @param requestDto
	 *            {@link AuditRequestDto} having required fields for auditing
	 * @return The {@link AuditResponseDto} having the status of audit
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','GLOBAL_ADMIN')")
	@ResponseFilter
	@PostMapping(value = "/logs",produces= MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper<AuditManagerResponseDto> addAudit(@RequestBody @Valid RequestWrapper<AuditManagerRequestDto> requestDto) {
		ResponseWrapper<AuditManagerResponseDto> response = new ResponseWrapper<>();
		response.setResponse(auditManagerProxyService.logAdminAudit(requestDto.getRequest()));
		return response;
	}

}
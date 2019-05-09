package io.mosip.authentication.common.service.helper;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.AuditResponseDto;
import io.mosip.authentication.core.exception.RestServiceException;

@RestController
public class AuditTestController {

	RestHelper restHelper;

	@PostMapping(path = "/auditmanager/audits", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public AuditResponseDto requestSync(@RequestBody AuditRequestDto request) throws RestServiceException {
		return new AuditResponseDto(true);
	}

}

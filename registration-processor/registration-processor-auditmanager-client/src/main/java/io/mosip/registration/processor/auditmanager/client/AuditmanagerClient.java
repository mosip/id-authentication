package io.mosip.registration.processor.auditmanager.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.registration.processor.auditmanager.dto.AuditRequestDto;
import io.mosip.registration.processor.auditmanager.dto.AuditResponseDto;



@Component
public class AuditmanagerClient {
	
	@Value("${kernel-auditmanager-service}")
	private String auditManagerServiceHost;
	/**
	 * Function to add new audit
	 * 
	 * @param auditRequestDto
	 *            {@link AuditRequestDto} having required fields for auditing
	 * @return The {@link AuditResponseDto} having the status of audit
	 */
	public AuditResponseDto addAudit( AuditRequestDto auditRequestDto){
		
		ResponseEntity<AuditResponseDto> responseEntity = new RestTemplate().postForEntity(
				auditManagerServiceHost,(Object)auditRequestDto,
		        AuditResponseDto.class);
		
		return responseEntity.getBody();
		
		
	} 
}

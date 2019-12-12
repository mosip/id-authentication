package io.mosip.admin.packetstatusupdater.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.packetstatusupdater.dto.AuditManagerRequestDto;
import io.mosip.admin.packetstatusupdater.service.AuditManagerProxyService;

/**
 * 
 * @author Megha Tanga
 *
 */
@Service
public class AuditManagerProxyServiceImpl implements AuditManagerProxyService {

	@Value("${mosip.kernel.audit.manager.api}")
	String auditmanagerapi;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public void logAdminAudit(AuditManagerRequestDto auditManagerRequestDto) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<AuditManagerRequestDto> entity = new HttpEntity<>(auditManagerRequestDto,
				headers);
		try {
			restTemplate.exchange(auditmanagerapi, HttpMethod.POST, entity, Object.class).getBody();
			//restTemplate.postForEntity(auditmanagerapi, entity, Object.class);
		
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			System.out.println("HttpErrorException: " + e.getMessage());
			System.out.println(e.getResponseBodyAsString());
		}

	}

}
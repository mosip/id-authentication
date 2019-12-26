package io.mosip.admin.packetstatusupdater.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.packetstatusupdater.constant.AdminManagerProxyErrorCode;
import io.mosip.admin.packetstatusupdater.dto.AuditManagerRequestDto;
import io.mosip.admin.packetstatusupdater.dto.AuditManagerResponseDto;
import io.mosip.admin.packetstatusupdater.exception.MasterDataServiceException;
import io.mosip.admin.packetstatusupdater.service.AuditManagerProxyService;
import io.mosip.kernel.core.http.RequestWrapper;

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
	public AuditManagerResponseDto logAdminAudit(AuditManagerRequestDto auditManagerRequestDto) {
		AuditManagerResponseDto auditManagerResponseDto = new AuditManagerResponseDto();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		RequestWrapper<AuditManagerRequestDto> request = new RequestWrapper<>();
		request.setId("mosip.admin.audit");
		request.setRequest(auditManagerRequestDto);
		request.setRequesttime(LocalDateTime.now());
		request.setRequest(auditManagerRequestDto);
		HttpEntity<RequestWrapper<AuditManagerRequestDto>> entity = new HttpEntity<>(request,
				headers);
		try {
			Object returnEntityt = restTemplate.postForEntity(auditmanagerapi, entity, Object.class).getBody();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			throw new MasterDataServiceException(AdminManagerProxyErrorCode.ADMIN_FETCH_EXCEPTION.getErrorCode(),
					AdminManagerProxyErrorCode.ADMIN_FETCH_EXCEPTION.getErrorMessage(), ex);
		}
		auditManagerResponseDto.setStatus("Success");
		auditManagerResponseDto.setMessage("Audit logged successfuly");
		return auditManagerResponseDto;

	}

}
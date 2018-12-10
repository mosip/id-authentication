package io.mosip.kernel.auditmanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auditmanager.controller.AuditManagerController;
import io.mosip.kernel.auditmanager.dto.AuditRequestDto;
import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.kernel.auditmanager.service.impl.AuditManagerServiceImpl;

@RunWith(SpringRunner.class)

public class AuditControllerTest {
	@Mock
	private AuditManagerServiceImpl service;
	@InjectMocks
	private AuditManagerController controller;

	@Test
	public void generateOtpTest() throws Exception {

		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setActionTimeStamp(LocalDateTime.now());
		auditRequestDto.setApplicationId("applicationId");
		auditRequestDto.setApplicationName("applicationName");
		auditRequestDto.setCreatedBy("createdBy");
		auditRequestDto.setDescription("description");
		auditRequestDto.setEventId("eventId");
		auditRequestDto.setEventName("eventName");
		auditRequestDto.setEventType("eventType");
		auditRequestDto.setHostIp("hostIp");
		auditRequestDto.setHostName("hostName");
		auditRequestDto.setId("id");
		auditRequestDto.setIdType("idType");
		auditRequestDto.setModuleId("moduleId");
		auditRequestDto.setModuleName("moduleName");
		auditRequestDto.setSessionUserId("sessionUserId");
		auditRequestDto.setSessionUserName("sessionUserName");
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		auditResponseDto.setStatus(true);
		when(service.addAudit(ArgumentMatchers.any())).thenReturn(auditResponseDto);

		assertThat(controller.addAudit(auditRequestDto),
				is(new ResponseEntity<>(service.addAudit(auditRequestDto), HttpStatus.CREATED)));

	}

}

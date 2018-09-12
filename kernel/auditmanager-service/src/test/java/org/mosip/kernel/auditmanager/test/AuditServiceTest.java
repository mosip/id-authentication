package org.mosip.kernel.auditmanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.mosip.kernel.auditmanager.dto.AuditRequestDto;
import org.mosip.kernel.auditmanager.dto.AuditResponseDto;
import org.mosip.kernel.auditmanager.request.AuditRequest;
import org.mosip.kernel.auditmanager.service.impl.AuditManagerServiceImpl;
import org.mosip.kernel.core.spi.auditmanager.AuditHandler;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AuditServiceTest {
	@Mock
	private AuditHandler<AuditRequest> handler;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private AuditManagerServiceImpl service;

	@Test
	public void auditServiceTest() {

		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setActionTimeStamp(OffsetDateTime.now());
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
		when(handler.writeAudit(ArgumentMatchers.any())).thenReturn(true);
		assertThat(service.addAudit(auditRequestDto), is(auditResponseDto));
	}


}

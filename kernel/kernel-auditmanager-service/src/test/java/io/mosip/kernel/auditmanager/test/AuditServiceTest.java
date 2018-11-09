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
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.auditmanager.service.impl.AuditManagerServiceImpl;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;

@RunWith(SpringRunner.class)
public class AuditServiceTest {
	@Mock
	private AuditHandler<AuditRequestDto> handler;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private AuditManagerServiceImpl service;

	@Test
	public void auditServiceTest() {

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
		when(handler.writeAudit(ArgumentMatchers.any())).thenReturn(true);
		assertThat(service.addAudit(auditRequestDto), is(auditResponseDto));
	}


}

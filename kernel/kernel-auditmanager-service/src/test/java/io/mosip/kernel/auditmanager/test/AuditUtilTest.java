package io.mosip.kernel.auditmanager.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.auditmanager.util.AuditAsyncUtil;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuditUtilTest {

	@InjectMocks
	private AuditAsyncUtil auditAsyncUtil;

	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

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

		auditAsyncUtil.addAudit(auditRequestDto);
		verify(auditHandler, times(1)).addAudit(auditRequestDto);
	}
}

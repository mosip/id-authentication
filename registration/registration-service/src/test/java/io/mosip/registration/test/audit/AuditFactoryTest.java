package io.mosip.registration.test.audit;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.context.SessionContext;

import static org.mockito.Mockito.when;

public class AuditFactoryTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;
	@InjectMocks
	private AuditFactory auditFactory;
	
	@Test
	public void auditTest() {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		SessionContext sessionContext = SessionContext.getInstance();
		sessionContext.getUserContext().setUserId("userId");
		sessionContext.getUserContext().setName("operator");
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", sessionContext);
		when(auditHandler.writeAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);
		auditFactory.audit(AuditEventEnum.PACKET_APPROVED, AppModuleEnum.PACKET_CREATOR, "description", "id", "ref");
	}

}

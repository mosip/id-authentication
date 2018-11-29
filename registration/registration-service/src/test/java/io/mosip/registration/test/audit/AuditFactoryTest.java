package io.mosip.registration.test.audit;

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;

public class AuditFactoryTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;
	@InjectMocks
	private AuditFactoryImpl auditFactory;
	@Mock
	private Environment environment;
	
	@Test
	public void auditTest() {
		when(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_ID)).thenReturn("REG");
		when(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_NAME)).thenReturn("REGISTRATION");
		ReflectionTestUtils.setField(auditFactory, "environment", environment);
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		SessionContext sessionContext = SessionContext.getInstance();
		sessionContext.getUserContext().setUserId("userId");
		sessionContext.getUserContext().setName("operator");
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", sessionContext);
		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);
		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "description", "id", "ref");
	}

}

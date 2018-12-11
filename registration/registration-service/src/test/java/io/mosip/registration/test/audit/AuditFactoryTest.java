package io.mosip.registration.test.audit;

import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ InetAddress.class })
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
	public void auditTest() throws UnknownHostException {
		PowerMockito.mockStatic(InetAddress.class);
		PowerMockito.when(InetAddress.getLocalHost()).thenCallRealMethod();
		when(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_ID)).thenReturn("REG");
		when(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_NAME)).thenReturn("REGISTRATION");
		SessionContext.destroySession();

		SessionContext sessionContext = SessionContext.getInstance();
		sessionContext.getUserContext().setUserId("userId");
		sessionContext.getUserContext().setName("operator");
		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);
		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "description", "id", "ref");
	}

	@Test
	public void auditTestWithDefaultValues() throws UnknownHostException {
		PowerMockito.mockStatic(InetAddress.class);
		PowerMockito.when(InetAddress.getLocalHost()).thenThrow(new UnknownHostException());

		when(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_ID)).thenReturn("REG");
		when(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_NAME)).thenReturn("REGISTRATION");
		SessionContext.destroySession();

		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);
		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "description", "id", "ref");
	}

}

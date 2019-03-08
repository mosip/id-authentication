package io.mosip.registration.test.audit;

import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
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
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
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

	@Before
	public void initialize() {
		ReflectionTestUtils.setField(auditFactory, "applicationId", "REG");
		ReflectionTestUtils.setField(auditFactory, "applicationName", "REGISTRATION");
		ReflectionTestUtils.setField(auditFactory, "defaultHostIP", "127.0.0.0");
		ReflectionTestUtils.setField(auditFactory, "defaultHostName", "LOCALHOST");
	}

	@Test
	public void auditTest() throws UnknownHostException {
		PowerMockito.mockStatic(InetAddress.class);
		PowerMockito.when(InetAddress.getLocalHost()).thenCallRealMethod();
		SessionContext.destroySession();

		SessionContext sessionContext = SessionContext.getInstance();
		sessionContext.getUserContext().setUserId("userId");
		sessionContext.getUserContext().setName("operator");
		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);
		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "id", "ref");
	}

	@Test
	public void auditTestWithDefaultValues() throws UnknownHostException {
		PowerMockito.mockStatic(InetAddress.class);
		PowerMockito.when(InetAddress.getLocalHost()).thenThrow(new UnknownHostException());
		SessionContext.destroySession();
		SessionContext.getInstance();

		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);
		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "id", "ref");
		SessionContext.destroySession();
	}

}

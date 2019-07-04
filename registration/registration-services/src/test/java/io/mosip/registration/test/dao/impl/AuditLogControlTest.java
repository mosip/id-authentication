package io.mosip.registration.test.dao.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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

import io.mosip.registration.builder.Builder;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.impl.AuditLogControlDAOImpl;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.RegistrationAuditDates;
import io.mosip.registration.repositories.AuditLogControlRepository;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class })
public class AuditLogControlTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	public AuditLogControlDAOImpl auditLogControlDAOImpl;
	@Mock
	public AuditLogControlRepository auditLogControlRepository;

	@Mock
	AuditDAO auditDAO;

	@Test
	public void testGetLatestRegistrationAuditDates() {
		RegistrationAuditDates registrationAuditDates = new RegistrationAuditDates() {

			@Override
			public Timestamp getAuditLogToDateTime() {
				return Timestamp.valueOf(LocalDateTime.now().minusDays(10));
			}

			@Override
			public Timestamp getAuditLogFromDateTime() {
				return Timestamp.valueOf(LocalDateTime.now().minusDays(1));
			}
		};
		when(auditLogControlRepository.findTopByOrderByCrDtimeDesc()).thenReturn(registrationAuditDates);

		assertThat(auditLogControlDAOImpl.getLatestRegistrationAuditDates(), is(registrationAuditDates));
	}

	@Test
	public void testSave() throws Exception {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");
		AuditLogControl expectedAuditLogControl = Builder.build(AuditLogControl.class)
				.with(auditLogControl -> auditLogControl
						.setAuditLogFromDateTime(Timestamp.valueOf(LocalDateTime.now().minusHours(3))))
				.with(auditLogControl -> auditLogControl
						.setAuditLogToDateTime(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10))))
				.with(auditLogControl -> auditLogControl.setRegistrationId("44319223770005620190123193601"))
				.with(auditLogControl -> auditLogControl.setAuditLogSyncDateTime(currentTimestamp))
				.with(auditLogControl -> auditLogControl.setCrDtime(currentTimestamp))
				.with(auditLogControl -> auditLogControl
						.setCrBy(SessionContext.userContext().getUserId()))
				.get();
		when(auditLogControlRepository.save(Mockito.any(AuditLogControl.class))).thenReturn(expectedAuditLogControl);

		auditLogControlDAOImpl.save(expectedAuditLogControl);
	}

	@Test
	public void deleteTest() {

		AuditLogControl auditLogControl = new AuditLogControl();
		auditLogControl.setAuditLogFromDateTime(new Timestamp(System.currentTimeMillis()));
		auditLogControl.setAuditLogToDateTime(new Timestamp(System.currentTimeMillis()));

		Mockito.doNothing().when(auditDAO).deleteAll(Mockito.any(), Mockito.any());
		Mockito.doNothing().when(auditLogControlRepository).delete(Mockito.any());
		auditLogControlDAOImpl.delete(auditLogControl);
	}

	@Test
	public void getTest() {
		List<AuditLogControl> audits = new LinkedList<>();
		Mockito.when(auditLogControlRepository.findByCrDtimeBefore(new Timestamp(Mockito.anyLong())))
				.thenReturn(audits);
		assertSame(audits, auditLogControlDAOImpl.get(new Timestamp(System.currentTimeMillis())));
	}

	@Test
	public void getByRegIdTest() {
		AuditLogControl audit = new AuditLogControl();
		Mockito.when(auditLogControlRepository.findById(AuditLogControl.class, "REG1234")).thenReturn(audit);
		assertSame(audit, auditLogControlDAOImpl.get("REG1234"));
	}
}

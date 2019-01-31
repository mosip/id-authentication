package io.mosip.registration.test.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.builder.Builder;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.impl.AuditLogControlDAOImpl;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.RegistrationAuditDates;
import io.mosip.registration.repositories.AuditLogControlRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class AuditLogControlTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	public AuditLogControlDAOImpl auditLogControlDAOImpl;
	@Mock
	public AuditLogControlRepository auditLogControlRepository;

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
	public void testSave() {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		AuditLogControl expectedAuditLogControl = Builder.build(AuditLogControl.class)
				.with(auditLogControl -> auditLogControl
						.setAuditLogFromDateTime(Timestamp.valueOf(LocalDateTime.now().minusHours(3))))
				.with(auditLogControl -> auditLogControl
						.setAuditLogToDateTime(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10))))
				.with(auditLogControl -> auditLogControl.setRegistrationId("44319223770005620190123193601"))
				.with(auditLogControl -> auditLogControl.setAuditLogSyncDateTime(currentTimestamp))
				.with(auditLogControl -> auditLogControl.setCrDtime(currentTimestamp))
				.with(auditLogControl -> auditLogControl
						.setCrBy(SessionContext.getInstance().getUserContext().getUserId()))
				.get();
		when(auditLogControlRepository.save(Mockito.any(AuditLogControl.class))).thenReturn(expectedAuditLogControl);

		auditLogControlDAOImpl.save(expectedAuditLogControl);
	}

}

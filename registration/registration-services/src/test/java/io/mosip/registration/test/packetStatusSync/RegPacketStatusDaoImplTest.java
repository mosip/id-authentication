package io.mosip.registration.test.packetStatusSync;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.impl.RegPacketStatusDAOImpl;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.repositories.AuditLogControlRepository;
import io.mosip.registration.repositories.RegTransactionRepository;
import io.mosip.registration.repositories.RegistrationRepository;

public class RegPacketStatusDaoImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	RegistrationRepository registrationRepository;

	@Mock
	RegTransactionRepository regTransactionRepository;

	@InjectMocks
	RegPacketStatusDAOImpl packetStatusDao;

	@Mock
	AuditLogControlRepository auditLogControlRepository;

	@Mock
	AuditLogControlDAO auditLogControlDAO;

	@Test
	public void getTest() {
		when(registrationRepository.findById(Mockito.any(), Mockito.anyString())).thenReturn(new Registration());

		packetStatusDao.get("12345");
	}

	@Test
	public void updateTest() {
		Registration registration = new Registration();
		when(registrationRepository.update(Mockito.any())).thenReturn(registration);

		packetStatusDao.update(registration);
	}

	@Test
	public void findByClientStatusCodeTest() {
		Registration registration = new Registration();
		List<Registration> registrations = null;
		when(registrationRepository.findByclientStatusCodeOrderByCrDtime(Mockito.any())).thenReturn(registrations);

		packetStatusDao.getPacketIdsByStatusUploaded();
	}

	@Test
	public void deleteTest() {
		Registration registration = new Registration();

		registration.setId("REG12345");

		AuditLogControl auditLogControl = new AuditLogControl();
		auditLogControl.setRegistrationId(registration.getId());

		Mockito.doNothing().when(registrationRepository).deleteById(Mockito.anyString());
		Mockito.doNothing().when(regTransactionRepository).deleteInBatch(Mockito.anyCollection());
		Mockito.when(auditLogControlDAO.get("REG12345")).thenReturn(auditLogControl);
		Mockito.doNothing().when(auditLogControlDAO).delete(auditLogControl);

		packetStatusDao.delete(registration);

	}

}

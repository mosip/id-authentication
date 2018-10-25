package io.mosip.registration.test.packetStatusSync;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.dao.impl.RegPacketStatusDAOImpl;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.repositories.RegistrationRepository;

@RunWith(SpringRunner.class)
public class RegPacketStatusDaoImplTest {

	@Mock
	RegistrationRepository registrationRepository;
	
	@Mock
	MosipLogger logger;
	
	@InjectMocks
	RegPacketStatusDAOImpl packetStatusDao;
	
	@Test
	public void getPacketIdsByStatusPostSyncedTest() {
		ReflectionTestUtils.setField(packetStatusDao, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		List<Registration> registrations = new ArrayList<>();
		Registration registration = new Registration();
		registration.setId("12345");
		registration.setClientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
		registrations.add(registration);
		when(registrationRepository.findByclientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode())).thenReturn(registrations);
		List<String> regIds = new ArrayList<>();
		regIds.add("12345");
		assertThat(packetStatusDao.getPacketIdsByStatusUploaded(), is(regIds));
	}
	
	@Test
	public void updatePacketIdsByServerStatusTest() {
		ReflectionTestUtils.setField(packetStatusDao, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		Registration registration = new Registration();
		registration.setId("12345");
		when(registrationRepository.findById(Registration.class, "12345")).thenReturn(registration);
		List<RegPacketStatusDTO> packetStatus = new ArrayList<>();
		RegPacketStatusDTO packetStatusDTO = new RegPacketStatusDTO("12345", "DECRYPTED");
		packetStatus.add(packetStatusDTO);
		packetStatusDao.updatePacketIdsByServerStatus(packetStatus);
	}
	
}

package io.mosip.registration.processor.core.spi.packetinfo.service;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantDocumentRepository;


@RunWith(MockitoJUnitRunner.class)
public class PacketInfoManagerImplTest {


	@Mock
	ApplicantDocumentRepository applicantDocumentRepository;

	

	@Before
	public void setup() {

	}

	@Test
	public void saveDemograficDataSccesTest() {
		Mockito.when(applicantDocumentRepository.save(ArgumentMatchers.any())).thenReturn("");
		
	}

	@Test(expected = Exception.class)
	public void addRegistrationTransactionFailureCheck() {
	
	}

}

package io.mosip.registration.test.dao.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.RegTransactionDAOImpl;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.repositories.RegTransactionRepository;

public class RegTransactionDAOTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private RegTransactionDAOImpl regTransactionDAOImpl;
	@Mock
	private RegTransactionRepository regTransactionRepository;
	
	@Test
	public void testBuildRegTrans() {
		when(regTransactionRepository.create(Mockito.any(RegistrationTransaction.class))).thenReturn(new RegistrationTransaction());
		regTransactionDAOImpl.buildRegTrans("11111","P");
	}
	
	@Test
	public void insertPacketTransDetailsTest() {
		List<RegistrationTransaction> packetListnew = new ArrayList<RegistrationTransaction>();
		packetListnew.add(new RegistrationTransaction());
		when(regTransactionRepository.saveAll(Mockito.anyListOf(RegistrationTransaction.class))).thenReturn(packetListnew);
		regTransactionDAOImpl.insertPacketTransDetails(packetListnew);
	}

}

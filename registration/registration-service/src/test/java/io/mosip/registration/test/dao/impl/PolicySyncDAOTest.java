package io.mosip.registration.test.dao.impl;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.PolicySyncDAOImpl;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.repositories.PolicySyncRepository;

public class PolicySyncDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PolicySyncDAOImpl policySyncDAOImpl;
	@Mock
	private PolicySyncRepository policySyncRepository;

	@Test
	public void findByMaxExpireTime() {
		KeyStore keyStore = new KeyStore();
		keyStore.setCreatedBy("createdBy");
		Mockito.when(policySyncRepository.findFirst1ByOrderByValidTillDtimesDesc()).thenReturn(keyStore);
		policySyncDAOImpl.findByMaxExpireTime();

	}

	@Test
	public void updatePolicy() {
		KeyStore keyStore = new KeyStore();
		keyStore.setCreatedBy("createdBy");
		Mockito.when(policySyncRepository.save(Mockito.any(KeyStore.class))).thenReturn(keyStore);
		policySyncDAOImpl.updatePolicy(keyStore);

	}

}

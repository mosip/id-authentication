package io.mosip.authentication.common.service.websub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;

@RunWith(MockitoJUnitRunner.class)
public class IdAuthWebSubInitializerTest {
	
	@InjectMocks
	private WebSubHelper webSubHelper;
	
	@InjectMocks
	private MasterDataUpdateEventInitializer masterDataUpdateEventInitializer;

	@InjectMocks
	private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;

	@InjectMocks
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;
	
	@InjectMocks
	private AuthAnonymousEventPublisher authAnonymousEventPublisher;
	
	@InjectMocks
	private PartnerCACertEventInitializer partnerCACertEventInitializer;
	
	@InjectMocks
	private IdAuthWebSubInitializer idAuthWebSubInitializer;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	private IdAuthWebSubInitializer getTestSubject() {
		return idAuthWebSubInitializer;
	}

	@Test
	public void testDoInitSubscriptions() throws Exception {
		IdAuthWebSubInitializer testSubject;
		int result;

		// default test
		testSubject = getTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "doInitSubscriptions");
	}

	@Test
	public void testDoRegisterTopics() throws Exception {
		IdAuthWebSubInitializer testSubject;
		int result;

		// default test
		testSubject = getTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "doRegisterTopics");
	}
}
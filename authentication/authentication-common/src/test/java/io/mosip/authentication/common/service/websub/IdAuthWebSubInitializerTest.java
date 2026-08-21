package io.mosip.authentication.common.service.websub;

import static org.mockito.Mockito.verify;

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
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.HotlistEventInitializer;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.RemoveIdStatusEventPublisher;

@RunWith(MockitoJUnitRunner.class)
public class IdAuthWebSubInitializerTest {

	@Mock
	private WebSubHelper webSubHelper;

	@Mock
	private MasterDataUpdateEventInitializer masterDataUpdateEventInitializer;

	@Mock
	private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;

	@Mock
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;

	@Mock
	private AuthAnonymousEventPublisher authAnonymousEventPublisher;

	@Mock
	private PartnerCACertEventInitializer partnerCACertEventInitializer;

	// Added for internal-service controller/batch merge (phases 2-3): verify the topics/publishers
	// added on top of the original external/OTP-only initializer are actually wired.
	@Mock
	private PartnerServiceEventsInitializer partnerServiceEventsInitializer;

	@Mock
	private HotlistEventInitializer hotlistEventInitializer;

	@Mock
	private IdChangeEventsInitializer idChangeEventsInitializer;

	@Mock
	private AuthTypeStatusEventSubscriber authTypeStatusEventSubscriber;

	@Mock
	private CredentialStoreStatusEventPublisher credentialStoreStatusEventPublisher;

	@Mock
	private AuthTypeStatusEventPublisher authTypeStatusEventPublisher;

	@Mock
	private RemoveIdStatusEventPublisher removeIdStatusEventPublisher;

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
		IdAuthWebSubInitializer testSubject = getTestSubject();
		ReflectionTestUtils.invokeMethod(testSubject, "doInitSubscriptions");

		verify(webSubHelper).initSubscriber(org.mockito.ArgumentMatchers.eq(masterDataUpdateEventInitializer),
				org.mockito.ArgumentMatchers.any());
		verify(webSubHelper).initSubscriber(partnerCACertEventInitializer);
		verify(webSubHelper).initSubscriber(partnerServiceEventsInitializer);
		verify(webSubHelper).initSubscriber(hotlistEventInitializer);
		verify(webSubHelper).initSubscriber(idChangeEventsInitializer);
		verify(webSubHelper).initSubscriber(authTypeStatusEventSubscriber);
	}

	@Test
	public void testDoRegisterTopics() throws Exception {
		IdAuthWebSubInitializer testSubject = getTestSubject();
		ReflectionTestUtils.invokeMethod(testSubject, "doRegisterTopics");

		verify(webSubHelper).initRegistrar(fraudEventPublisher);
		verify(webSubHelper).initRegistrar(authTransactionStatusEventPublisher);
		verify(webSubHelper).initRegistrar(authAnonymousEventPublisher);
		verify(webSubHelper).initRegistrar(credentialStoreStatusEventPublisher);
		verify(webSubHelper).initRegistrar(authTypeStatusEventPublisher);
		verify(webSubHelper).initRegistrar(removeIdStatusEventPublisher);
	}
}
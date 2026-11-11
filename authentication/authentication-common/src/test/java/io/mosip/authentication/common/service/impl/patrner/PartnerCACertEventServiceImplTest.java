package io.mosip.authentication.common.service.impl.patrner;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.partnercertservice.service.spi.PartnerCertificateManagerService;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class PartnerCACertEventServiceImplTest {

	@InjectMocks
	private PartnerCACertEventServiceImpl partnerCACertEventServiceImpl;

	@Mock
	private DataShareManager dataShareManager;

	@Mock
	private PartnerCertificateManagerService partnerCertManager;

	@Value("${ida-decrypt-ca-cert-data-share-content:false}")
	private boolean decryptCaCertFromDataShare;

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(partnerCACertEventServiceImpl, "decryptCaCertFromDataShare", false);
	}

	@Test
	public void handleCACertEventTest() throws RestServiceException, IdAuthenticationBusinessException, IOException {
		Map<String, Object> eventData = getEventData();
		Event event = new Event();
		event.setData(eventData);
		EventModel eventModel = new EventModel();
		eventModel.setEvent(event);
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		eventModel.setPublisher("Test");
		eventModel.setTopic("Test");
		partnerCACertEventServiceImpl.handleCACertEvent(eventModel);
	}

	@Test
	public void evictCACertCacheTest() throws RestServiceException, IdAuthenticationBusinessException, IOException {
		Map<String, Object> eventData = getEventData();
		Event event = new Event();
		event.setData(eventData);
		EventModel eventModel = new EventModel();
		eventModel.setEvent(event);
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		eventModel.setPublisher("Test");
		eventModel.setTopic("Test");
		partnerCACertEventServiceImpl.evictCACertCache(eventModel);
	}

	private Map<String, Object> getEventData() throws IOException {
		Map<String, Object> eventData = new HashMap<String, Object>();
		eventData.put("certChainDatashareUrl", "certChainDatashareUrl");
		eventData.put("partnerDomain", "partnerDomain");
		eventData.put("idType", "UIN");
		eventData.put("id", "13E0F1FD2C6B21F33CE5B24D1ACDCCDD02858D2ED91018663425CCB77B5A9799");
		eventData.put("status", "UNBLOCKED");
		eventData.put("expiryTimestamp", null);
		return eventData;
	}

}

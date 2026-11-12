package io.mosip.authentication.common.service.impl.idevent;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdChangeEventHandlerServiceImplTest {

	@InjectMocks
	private IdChangeEventHandlerServiceImpl idChangeEventImpl;

	@Mock
	private IdentityCacheRepository identityCacheRepo;

	@Mock
	private AuditHelper auditHelper;

	@Mock
	private CredentialStoreService credStorService;

	private static final String IDA = "IDA";

	private static final String EXPIRY_TIME = "expiry_timestamp";

	private static final String TRANSACTION_LIMIT = "transaction_limit";

	private static final String ID_HASH = "id_hash";

	@BeforeEach
	public void before() {
	}

	@Test
	public void handleIdEventTest1() throws IdAuthenticationBusinessException {
		EventModel eventModel = new EventModel();
		eventModel.setPublisher("Test");
		eventModel.setTopic(IDAEventType.CREDENTIAL_ISSUED.toString());
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		Event event = new Event();
		eventModel.setEvent(event);
		idChangeEventImpl.handleIdEvent(eventModel);
	}

	@Test
	public void handleIdEventTest2() throws IdAuthenticationBusinessException {
		EventModel eventModel = new EventModel();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("key1", "value1");
		eventModel.setPublisher("Test");
		eventModel.setTopic(IDAEventType.REMOVE_ID.toString());
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		Event event = new Event();
		event.setId("12");
		event.setData(data);
		eventModel.setEvent(event);
		idChangeEventImpl.handleIdEvent(eventModel);
	}

	@Test
	public void handleIdEventExceptionTest1() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		EventModel eventModel = new EventModel();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id_hash", "aZdaadxc");
		eventModel.setPublisher("Test");
		eventModel.setTopic(IDAEventType.DEACTIVATE_ID.toString());
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		Event event = new Event();
		event.setId("12");
		event.setData(data);
		eventModel.setEvent(event);
		idChangeEventImpl.handleIdEvent(eventModel);
			});
	}

	@Test
	public void handleIdEventExceptionTest2() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		EventModel eventModel = new EventModel();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id_hash", "aZdaadxc");
		eventModel.setPublisher("Test");
		eventModel.setTopic(IDAEventType.ACTIVATE_ID.toString());
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		Event event = new Event();
		event.setId("12");
		event.setData(data);
		eventModel.setEvent(event);
		idChangeEventImpl.handleIdEvent(eventModel);
			});
	}

	@Test
	public void handleIdEventExceptionTest3() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		EventModel eventModel = new EventModel();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("key1", "value1");
		eventModel.setPublisher("Test");
		eventModel.setTopic(IDAEventType.DEACTIVATE_ID.toString());
		eventModel.setPublishedOn(LocalDateTime.now().toString());
		Event event = new Event();
		event.setId("12");
		event.setData(data);
		eventModel.setEvent(event);
		idChangeEventImpl.handleIdEvent(eventModel);
			});
	}

	@Test
	public void handleIdEventExceptionTest() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		EventModel event = new EventModel();
		idChangeEventImpl.handleIdEvent(event);
			});
	}

}

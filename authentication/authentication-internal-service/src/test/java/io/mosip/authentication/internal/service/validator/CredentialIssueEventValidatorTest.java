package io.mosip.authentication.internal.service.validator;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class CredentialIssueEventValidatorTest {
	
//	/** The uin validator. */
//	@Mock
//	private UinValidatorImpl uinValidator;
//
//	/** The vid validator. */
//	@Mock
//	private VidValidatorImpl vidValidator;
//	
//	@Autowired
//	EnvPropertyResolver env;
//	
//	@InjectMocks
//	CredentialIssueEventValidator validator;
//
//	@Before
//	public void setUp() throws Exception {
//		ReflectionTestUtils.setField(validator, "env", env);
//		ReflectionTestUtils.setField(validator, "uinValidator", uinValidator);
//		ReflectionTestUtils.setField(validator, "vidValidator", vidValidator);
//	}
//
//	private CredentialIssueEventValidator createTestSubject() {
//		return validator;
//	}
//
//	
//	@Test
//	public void testValidateMissingRequest() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequesttime(DateUtils.getUTCCurrentDateTime());
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		testSubject.validate(requestWrapper, errors);
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
//
//	}
//	
//	@Test
//	public void testValidateNullEventsList() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequesttime(DateUtils.getUTCCurrentDateTime());
//		EventsDTO eventsDto = new EventsDTO();
//		requestWrapper.setRequest(eventsDto);
//
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		testSubject.validate(requestWrapper, errors);
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
//
//	}
//	
//	@Test
//	public void testValidateEmptyEventsList() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequesttime(DateUtils.getUTCCurrentDateTime());
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		requestWrapper.setRequest(eventsDto);
//
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		testSubject.validate(requestWrapper, errors);
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
//
//	}
//	
//	
//	
//	@Test
//	public void testValidateValidReq() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("0123456789");
//		event.setVid("1234567890");
//		event.setTransactionLimit(1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setId("mosip.identity.notify");
//		requestWrapper.setRequest(eventsDto);
//		requestWrapper.setRequesttime(DateUtils.getUTCCurrentDateTime());
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		testSubject.validate(requestWrapper, errors);
//		assertTrue(errors.getAllErrors().isEmpty());
//	}
//	
//	@Test
//	public void testValidateEventNoUinVid() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		ReflectionTestUtils.invokeMethod(testSubject, "validateEvent", new Object[] { event, index, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
//	}
//	
//	
//	@Test
//	public void testValidateEventInvalidUin() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("abc");
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("code", "message"));
//		ReflectionTestUtils.invokeMethod(testSubject, "validateEvent", new Object[] { event, index, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode())));
//	}
//	
//	@Test
//	public void testValidateEventInvalidVid() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setVid("abc");
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("code", "message"));
//		ReflectionTestUtils.invokeMethod(testSubject, "validateEvent", new Object[] { event, index, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_VID.getErrorCode())));
//	}
//	
//	@Test
//	public void testValidateEventInvalidTranasctionLimit() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setVid("1234567890");
//		event.setTransactionLimit(-1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		ReflectionTestUtils.invokeMethod(testSubject, "validateEvent", new Object[] { event, index, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode())));
//	}
//	
//	@Test
//	public void testValidateEventValid() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("0123456789");
//		event.setVid("1234567890");
//		event.setTransactionLimit(1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		ReflectionTestUtils.invokeMethod(testSubject, "validateEvent", new Object[] { event, index, errors });
//		assertTrue(errors.getAllErrors().isEmpty());
//	}
//
//	@Test
//	public void testValidateEvents() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("0123456789");
//		event.setVid("1234567890");
//		event.setTransactionLimit(1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		ReflectionTestUtils.invokeMethod(testSubject, "validateEvents", new Object[] { eventsDto, errors });
//		assertTrue(errors.getAllErrors().isEmpty());
//	}
//
//	@Test
//	public void testValidateRequestWrapperMissingId() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("0123456789");
//		event.setVid("1234567890");
//		event.setTransactionLimit(1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		ReflectionTestUtils.invokeMethod(testSubject, "validateRequestWrapper",
//				new Object[] { requestWrapper, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
//	}
//	
//	@Ignore
//	@Test
//	public void testValidateRequestWrapperMissingRequestTime() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("0123456789");
//		event.setVid("1234567890");
//		event.setTransactionLimit(1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setId("mosip.identity.notify");
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		ReflectionTestUtils.invokeMethod(testSubject, "validateRequestWrapper",
//				new Object[] { requestWrapper, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode())));
//	}
//	
//	@Ignore
//	@Test
//	public void testValidateRequestWrapperFutureRequestTime() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		EventsDTO eventsDto = new EventsDTO();
//		List<EventDTO> events = new ArrayList<>();
//		
//		eventsDto.setEvents(events);
//		EventDTO event = new EventDTO();
//		event.setUin("0123456789");
//		event.setVid("1234567890");
//		event.setTransactionLimit(1);
//		int index = 0;
//		events.add(event);
//		RequestWrapper<EventsDTO> requestWrapper = new RequestWrapper<EventsDTO>();
//		requestWrapper.setId("mosip.identity.notify");
//		requestWrapper.setRequesttime(LocalDateTime.now().plusHours(1));
//		requestWrapper.setRequest(eventsDto);
//		Errors errors =  new BeanPropertyBindingResult(requestWrapper, "RequestWrapper");
//
//		// default test
//		testSubject = createTestSubject();
//		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
//		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
//		ReflectionTestUtils.invokeMethod(testSubject, "validateRequestWrapper",
//				new Object[] { requestWrapper, errors });
//		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals(IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorCode())));
//	}
//
//	@Test
//	public void testSupports() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		Class<?> clazz = RequestWrapper.class;
//		boolean result;
//
//		// default test
//		testSubject = createTestSubject();
//		result = testSubject.supports(clazz);
//		assertTrue(result);
//	}
//	
//	@Test
//	public void testSupportsInvalidClass() throws Exception {
//		CredentialIssueEventValidator testSubject;
//		Class<?> clazz = AuthRequestDTO.class;
//		boolean result;
//
//		// default test
//		testSubject = createTestSubject();
//		result = testSubject.supports(clazz);
//		assertTrue(!result);
//	}
}
package io.mosip.authentication.common.service.impl.idevent;

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
public class IdChangeEventHandlerServiceImplTest {
//
//	@Autowired
//	private Environment env;
//	
//	@Mock
//	private IdAuthSecurityManager securityManager;
//	
//	@Mock
//	private IdRepoManager idRepoManager;
//	
//	@Mock
//	private IdServiceImpl idService;
//	
//	@Mock
//	private IdentityCacheRepository identityCacheRepo;
//	
//	@Mock
//	private AuditHelper auditHelper;
//	
//	@Autowired
//	private ObjectMapper mapper;
//	
//	@InjectMocks
//	IdChangeEventHandlerServiceImpl idChengeEventHandlerServiceImpl;
//	
//	@Before
//	public void before() throws IDDataValidationException, RestServiceException {
//		//ReflectionTestUtils.setField(idChengeEventHandlerServiceImpl, "mapper", mapper);
//	}
//	
//	@Test
//	public void TestCreateUinEvent() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		EventDTO event = new EventDTO();
//		event.setEventType(EventType.CREATE_UIN);
//		String uin = "1122334455";
//		event.setUin(uin);
//		events.add(event);
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		Map<String, Object> idData = createIdData();
//		Mockito.when(idRepoManager.getIdentity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idData);
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		IdentityEntity expectedEntity = new IdentityEntity();
//		expectedEntity.setId(uin);
//		byte[] demoData = getDemoData(idData);
//		expectedEntity.setDemographicData(demoData);
//		byte[] bioData = getBioData(idData);
//		expectedEntity.setBiometricData(bioData);
//		
//		Mockito.when(idService.getDemoData(Mockito.any())).thenReturn(demoData);
//		Mockito.when(idService.getBioData(Mockito.any())).thenReturn(bioData);
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(Arrays.asList(expectedEntity)));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestCreateVidEvent() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		EventDTO event = new EventDTO();
//		event.setEventType(EventType.CREATE_VID);
//		String uin = "1122334455";
//		String vid = "112233445566778899";
//		event.setVid(vid);
//		event.setUin(uin);
//		events.add(event);
//		
//		Map<String, Object> idData = createIdData();
//		
//		IdentityEntity existingUinEntity = new IdentityEntity();
//		existingUinEntity.setId(uin);
//		existingUinEntity.setDemographicData(getDemoData(idData));
//		existingUinEntity.setBiometricData(getBioData(idData));
//
//		IdentityEntity expectedEntity = new IdentityEntity();
//		expectedEntity.setId(vid);
//		byte[] demoData = getDemoData(idData);
//		expectedEntity.setDemographicData(demoData);
//		byte[] bioData = getBioData(idData);
//		expectedEntity.setBiometricData(bioData);
//		
//		Mockito.when(idService.getDemoData(Mockito.any())).thenReturn(demoData);
//		Mockito.when(idService.getBioData(Mockito.any())).thenReturn(bioData);
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		Mockito.when(identityCacheRepo.findById(uin)).thenReturn(Optional.of(existingUinEntity));
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		Mockito.when(securityManager.decryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(Arrays.asList(expectedEntity)));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestUpdateUinEvent_UpdateIdDataOnly() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		EventDTO event = new EventDTO();
//		event.setEventType(EventType.UPDATE_UIN);
//		String uin = "1122334455";
//		event.setUin(uin);
//		events.add(event);
//		
//		IdentityEntity existingUinEntity = new IdentityEntity();
//		existingUinEntity.setId(uin);
//		existingUinEntity.setDemographicData("{}".getBytes());
//		existingUinEntity.setBiometricData("<test/>".getBytes());
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		Map<String, Object> idData = createIdData();
//		List<IdentityEntity> entities = new ArrayList<>();
//		entities.add(existingUinEntity);
//		Mockito.when(identityCacheRepo.findAllById(Arrays.asList(uin))).thenReturn(entities);
//		Mockito.when(idRepoManager.getIdentity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idData);
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		IdentityEntity expectedEntity = new IdentityEntity();
//		expectedEntity.setId(uin);
//		byte[] demoData = getDemoData(idData);
//		expectedEntity.setDemographicData(demoData);
//		byte[] bioData = getBioData(idData);
//		expectedEntity.setBiometricData(bioData);
//		
//		Mockito.when(idService.getDemoData(Mockito.any())).thenReturn(demoData);
//		Mockito.when(idService.getBioData(Mockito.any())).thenReturn(bioData);
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(Arrays.asList(expectedEntity)));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestUpdateUinEvent_UpdateIdDataAndAttribute() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		EventDTO event = new EventDTO();
//		event.setEventType(EventType.UPDATE_UIN);
//		String uin = "1122334455";
//		event.setUin(uin);
//		LocalDateTime expiryTimestamp = LocalDateTime.of(9999, 1, 1, 0, 0);
//		event.setExpiryTimestamp(expiryTimestamp);
//		events.add(event);
//		
//		IdentityEntity existingUinEntity = new IdentityEntity();
//		existingUinEntity.setId(uin);
//		existingUinEntity.setDemographicData("{}".getBytes());
//		existingUinEntity.setBiometricData("<test/>".getBytes());
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		Map<String, Object> idData = createIdData();
//		List<IdentityEntity> entities = new ArrayList<>();
//		entities.add(existingUinEntity);
//		Mockito.when(identityCacheRepo.findAllById(Arrays.asList(uin))).thenReturn(entities );
//		Mockito.when(idRepoManager.getIdentity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idData);
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		IdentityEntity expectedEntity = new IdentityEntity();
//		expectedEntity.setId(uin);
//		byte[] demoData = getDemoData(idData);
//		expectedEntity.setDemographicData(demoData);
//		byte[] bioData = getBioData(idData);
//		expectedEntity.setBiometricData(bioData);
//		expectedEntity.setExpiryTimestamp(expiryTimestamp);
//		
//		Mockito.when(idService.getDemoData(Mockito.any())).thenReturn(demoData);
//		Mockito.when(idService.getBioData(Mockito.any())).thenReturn(bioData);
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(Arrays.asList(expectedEntity)));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestUpdateVidEventNullUin() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		EventDTO event = new EventDTO();
//		event.setEventType(EventType.UPDATE_VID);
//		String vid = "112233445566778899";
//		event.setVid(vid);
//		LocalDateTime expiryTimestamp = LocalDateTime.of(9999, 1, 1, 0, 0);
//		event.setExpiryTimestamp(expiryTimestamp);
//		event.setTransactionLimit(1);
//		events.add(event);
//		
//		Map<String, Object> idData = createIdData();
//		
//		IdentityEntity existingVidEntity = new IdentityEntity();
//		existingVidEntity.setId(vid);
//		existingVidEntity.setDemographicData(getDemoData(idData));
//		existingVidEntity.setBiometricData(getBioData(idData));
//		existingVidEntity.setExpiryTimestamp(LocalDateTime.now());
//
//		IdentityEntity expectedEntity = new IdentityEntity();
//		expectedEntity.setId(vid);
//		expectedEntity.setDemographicData(getDemoData(idData));
//		expectedEntity.setBiometricData(getBioData(idData));
//		expectedEntity.setExpiryTimestamp(expiryTimestamp);
//		expectedEntity.setTransactionLimit(1);
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		List<IdentityEntity> entities = new ArrayList<>();
//		entities.add(existingVidEntity);
//		Mockito.when(identityCacheRepo.findAllById(Arrays.asList(vid))).thenReturn(entities );		
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(Arrays.asList(expectedEntity)));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestMultipleUpdateVidEventNullUin() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		
//		EventDTO event1 = new EventDTO();
//		event1.setEventType(EventType.UPDATE_VID);
//		String vid1 = "112233445566778899";
//		event1.setVid(vid1);
//		LocalDateTime expiryTimestamp1 = LocalDateTime.of(9999, 1, 1, 0, 0);
//		event1.setExpiryTimestamp(expiryTimestamp1);
//		event1.setTransactionLimit(1);
//		events.add(event1);
//		
//		EventDTO event2 = new EventDTO();
//		event2.setEventType(EventType.UPDATE_VID);
//		String vid2 = "223344556677889911";
//		event2.setVid(vid2);
//		LocalDateTime expiryTimestamp2 = LocalDateTime.of(8888, 1, 1, 0, 0);
//		event2.setExpiryTimestamp(expiryTimestamp2);
//		event2.setTransactionLimit(1);
//		events.add(event2);
//		
//		Map<String, Object> idData = createIdData();
//		
//		IdentityEntity existingVidEntity1 = new IdentityEntity();
//		existingVidEntity1.setId(vid1);
//		existingVidEntity1.setDemographicData(getDemoData(idData));
//		existingVidEntity1.setBiometricData(getBioData(idData));
//		existingVidEntity1.setExpiryTimestamp(LocalDateTime.now());
//		existingVidEntity1.setTransactionLimit(1);
//
//		
//		IdentityEntity existingVidEntity2 = new IdentityEntity();
//		existingVidEntity2.setId(vid2);
//		existingVidEntity2.setDemographicData(getDemoData(idData));
//		existingVidEntity2.setBiometricData(getBioData(idData));
//		existingVidEntity2.setExpiryTimestamp(LocalDateTime.now());
//
//
//		IdentityEntity expectedEntity1 = new IdentityEntity();
//		expectedEntity1.setId(vid1);
//		expectedEntity1.setDemographicData(getDemoData(idData));
//		expectedEntity1.setBiometricData(getBioData(idData));
//		expectedEntity1.setExpiryTimestamp(expiryTimestamp1);
//		expectedEntity1.setTransactionLimit(1);
//		
//		IdentityEntity expectedEntity2 = new IdentityEntity();
//		expectedEntity2.setId(vid2);
//		expectedEntity2.setDemographicData(getDemoData(idData));
//		expectedEntity2.setBiometricData(getBioData(idData));
//		expectedEntity2.setExpiryTimestamp(expiryTimestamp2);
//		expectedEntity2.setTransactionLimit(1);
//		
//		List<IdentityEntity> expectedEntities = new ArrayList<>();
//		expectedEntities.add(expectedEntity1);
//		expectedEntities.add(expectedEntity2);
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		List<IdentityEntity> entities = new ArrayList<>();
//		entities.add(existingVidEntity1);
//		entities.add(existingVidEntity2);
//		
//		Mockito.when(identityCacheRepo.findAllById(Mockito.any())).thenReturn(entities);		
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(expectedEntities));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestUpdateVidEventWithUin() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		EventDTO event = new EventDTO();
//		event.setEventType(EventType.UPDATE_VID);
//		String vid = "112233445566778899";
//		String uin = "1122334455";
//		event.setUin(uin);
//		event.setVid(vid);
//		LocalDateTime expiryTimestamp = LocalDateTime.of(9999, 1, 1, 0, 0);
//		event.setExpiryTimestamp(expiryTimestamp);
//		event.setTransactionLimit(1);
//		events.add(event);
//		
//		Map<String, Object> idData = createIdData();
//		
//		IdentityEntity existingUinEntity = new IdentityEntity();
//		existingUinEntity.setId(uin);
//		existingUinEntity.setDemographicData(getDemoData(idData));
//		existingUinEntity.setBiometricData(getBioData(idData));
//		
//				
//		IdentityEntity existingVidEntity = new IdentityEntity();
//		existingVidEntity.setId(vid);
//		existingVidEntity.setDemographicData("{}".getBytes());
//		existingVidEntity.setBiometricData("<test/>".getBytes());
//		existingVidEntity.setExpiryTimestamp(LocalDateTime.now());
//
//		IdentityEntity expectedEntity = new IdentityEntity();
//		expectedEntity.setId(vid);
//		byte[] demoData = getDemoData(idData);
//		expectedEntity.setDemographicData(demoData);
//		byte[] bioData = getBioData(idData);
//		expectedEntity.setBiometricData(bioData);
//		expectedEntity.setExpiryTimestamp(expiryTimestamp);
//		expectedEntity.setTransactionLimit(1);
//		
//		Mockito.when(idService.getDemoData(Mockito.any())).thenReturn(demoData);
//		Mockito.when(idService.getBioData(Mockito.any())).thenReturn(bioData);
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		List<IdentityEntity> entities = new ArrayList<>();
//		entities.add(existingVidEntity);
//		Mockito.when(identityCacheRepo.findAllById(Arrays.asList(vid))).thenReturn(entities);
//		Mockito.when(identityCacheRepo.findById(uin)).thenReturn(Optional.of(existingUinEntity));
//		Mockito.when(securityManager.decryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		
//		
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(Arrays.asList(expectedEntity)));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//	
//	@Test
//	public void TestMultipleUpdateVidEventWithUin() throws IdAuthenticationBusinessException {
//		List<EventDTO> events = new ArrayList<>();
//		String uin = "1122334455";
//
//		EventDTO event1 = new EventDTO();
//		event1.setEventType(EventType.UPDATE_VID);
//		String vid1 = "112233445566778899";
//		event1.setVid(vid1);
//		event1.setUin(uin);
//		LocalDateTime expiryTimestamp1 = LocalDateTime.of(9999, 1, 1, 0, 0);
//		event1.setExpiryTimestamp(expiryTimestamp1);
//		event1.setTransactionLimit(1);
//		events.add(event1);
//		
//		EventDTO event2 = new EventDTO();
//		event2.setEventType(EventType.UPDATE_VID);
//		String vid2 = "223344556677889911";
//		event2.setVid(vid2);
//		event2.setUin(uin);
//		LocalDateTime expiryTimestamp2 = LocalDateTime.of(8888, 1, 1, 0, 0);
//		event2.setExpiryTimestamp(expiryTimestamp2);
//		event2.setTransactionLimit(1);
//		events.add(event2);
//		
//		Map<String, Object> idData = createIdData();
//		
//		IdentityEntity existingUinEntity = new IdentityEntity();
//		existingUinEntity.setId(uin);
//		existingUinEntity.setDemographicData(getDemoData(idData));
//		existingUinEntity.setBiometricData(getBioData(idData));
//		
//		IdentityEntity existingVidEntity1 = new IdentityEntity();
//		existingVidEntity1.setId(vid1);
//		existingVidEntity1.setDemographicData("{}".getBytes());
//		existingVidEntity1.setBiometricData("<test/>".getBytes());
//		existingVidEntity1.setExpiryTimestamp(LocalDateTime.now());
//		existingVidEntity1.setTransactionLimit(1);
//
//		
//		IdentityEntity existingVidEntity2 = new IdentityEntity();
//		existingVidEntity2.setId(vid2);
//		existingVidEntity2.setDemographicData("{}".getBytes());
//		existingVidEntity2.setBiometricData("<test/>".getBytes());
//		existingVidEntity2.setExpiryTimestamp(LocalDateTime.now());
//		
//		byte[] demoData = getDemoData(idData);
//		byte[] bioData = getBioData(idData);
//
//		IdentityEntity expectedEntity1 = new IdentityEntity();
//		expectedEntity1.setId(vid1);
//		expectedEntity1.setDemographicData(demoData);
//		expectedEntity1.setBiometricData(bioData);
//		expectedEntity1.setExpiryTimestamp(expiryTimestamp1);
//		expectedEntity1.setTransactionLimit(1);
//		
//		IdentityEntity expectedEntity2 = new IdentityEntity();
//		expectedEntity2.setId(vid2);
//		expectedEntity2.setDemographicData(demoData);
//		expectedEntity2.setBiometricData(bioData);
//		expectedEntity2.setExpiryTimestamp(expiryTimestamp2);
//		expectedEntity2.setTransactionLimit(1);
//		
//		Mockito.when(idService.getDemoData(Mockito.any())).thenReturn(demoData);
//		Mockito.when(idService.getBioData(Mockito.any())).thenReturn(bioData);
//		
//		List<IdentityEntity> expectedEntities = new ArrayList<>();
//		expectedEntities.add(expectedEntity1);
//		expectedEntities.add(expectedEntity2);
//		
//		Mockito.when(securityManager.hash(Mockito.anyString())).thenAnswer(answerToReturnArg(0));
//		List<IdentityEntity> entities = new ArrayList<>();
//		entities.add(existingVidEntity1);
//		entities.add(existingVidEntity2);
//		
//		Mockito.when(identityCacheRepo.findAllById(Mockito.any())).thenReturn(entities);		
//		Mockito.when(securityManager.encryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//		Mockito.when(identityCacheRepo.findById(uin)).thenReturn(Optional.of(existingUinEntity));
//		Mockito.when(securityManager.decryptWithAES(Mockito.anyString(), Mockito.any())).thenAnswer(answerToReturnArg(1));
//
//		Mockito.when(identityCacheRepo.save(Mockito.any())).then(createEntityVerifyingAnswer(expectedEntities));
//		
//		idChengeEventHandlerServiceImpl.handleIdEvent(events);
//		
//	}
//
//	private void assertEntityEquals(IdentityEntity expectedEntity, IdentityEntity actualEntity)
//			throws UnsupportedEncodingException {
//		 assertEquals(expectedEntity.getId(), actualEntity.getId());
//		 assertEquals(expectedEntity.getExpiryTimestamp(), actualEntity.getExpiryTimestamp());
//		 assertEquals(expectedEntity.getTransactionLimit(), actualEntity.getTransactionLimit());
//		 assertEquals(new String(expectedEntity.getDemographicData(), "utf-8"), 
//				 new String(actualEntity.getDemographicData(), "utf-8"));
//		 assertEquals(new String(expectedEntity.getBiometricData(), "utf-8"), 
//				 new String(actualEntity.getBiometricData(), "utf-8"));
//	}
//	
//	private Answer<?> createEntityVerifyingAnswer(List<IdentityEntity> expectedEntities) {
//		return new Answer() {
//			   public Object answer(InvocationOnMock invocation) throws UnsupportedEncodingException {
//				     Object[] args = invocation.getArguments();
//				     //Object mock = invocation.getMock();
//				     List<IdentityEntity> actualEntities = (List<IdentityEntity>)args[0];
//				     actualEntities.forEach(actualEntity -> {
//				    	 IdentityEntity expectedEntity = expectedEntities.stream()
//									.filter(entity -> entity.getId().equals(actualEntity.getId()))
//									.findFirst()
//									.get();
//				    	 try {
//							assertEntityEquals(expectedEntity, actualEntity);
//						} catch (UnsupportedEncodingException e) {
//							throw new RuntimeException(e);
//						}
//				     });
//				     return null;
//				   }
//
//				};
//	}
//
//	private static Answer answerToReturnArg(int argIndex) {
//		return new Answer() {
//			   public Object answer(InvocationOnMock invocation) {
//				     Object[] args = invocation.getArguments();
//				     //Object mock = invocation.getMock();
//				     return args[argIndex];
//				   }
//				};
//	}
//	
//	private Map<String, Object> createIdData() {
//		try {
//			return mapper.readValue(CryptoUtil.decodeBase64(env.getProperty("mocked.idrepo-data")), Map.class);
//		} catch (IOException e) {
//			return new HashMap<>();
//		}
//	}
//
//	
//	
//	
//	/**
//	 * Gets the demo data.
//	 *
//	 * @param identity the identity
//	 * @return the demo data
//	 */
//	@SuppressWarnings("unchecked")
//	private byte[] getDemoData(Map<String, Object> identity) {
//		return Optional.ofNullable(identity.get("response"))
//								.filter(obj -> obj instanceof Map)
//								.map(obj -> ((Map<String, Object>)obj).get("identity"))
//								.filter(obj -> obj instanceof Map)
//								.map(obj -> {
//									try {
//										return mapper.writeValueAsBytes(obj);
//									} catch (JsonProcessingException e) {
//										e.printStackTrace();
//									}
//									return new byte[0];
//								})
//								.orElse(new byte[0]);
//	}
//	
//	/**
//	 * Gets the bio data.
//	 *
//	 * @param identity the identity
//	 * @return the bio data
//	 */
//	@SuppressWarnings("unchecked")
//	private byte[] getBioData(Map<String, Object> identity) {
//		return Optional.ofNullable(identity.get("response"))
//								.filter(obj -> obj instanceof Map)
//								.map(obj -> ((Map<String, Object>)obj).get("documents"))
//								.filter(obj -> obj instanceof List)
//								.flatMap(obj -> 
//										((List<Map<String, Object>>)obj)
//											.stream()
//											.filter(map -> map.containsKey("category") 
//															&& map.get("category").toString().equalsIgnoreCase("individualBiometrics")
//															&& map.containsKey("value"))
//											.map(map -> (String)map.get("value"))
//											.findAny())
//								.map(CryptoUtil::decodeBase64)
//								.orElse(new byte[0]);
//	}

}

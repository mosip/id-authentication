package io.mosip.authentication.internal.service.controller;

//@Ignore
//@RunWith(SpringRunner.class)
//@WebMvcTest
//@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class KeymanagerControllerTest {
	
//	@InjectMocks
//	KeymanagerController keymanagerController;
//	
//	@Autowired
//	private EnvPropertyResolver env;
//	
//	@Mock
//	KeyServiceManager keymanagerService;
//	
//	@Before
//	public void before() {
//		ReflectionTestUtils.setField(keymanagerController, "env", env);
//		ReflectionTestUtils.setField(keymanagerController, "keymanagerService", keymanagerService);
//	}
//	
//	@Test
//	public void TestPublickKey() throws IdAuthenticationBusinessException {
//		java.util.Optional<String> refId = java.util.Optional.of("refId");		
//		keymanagerController.getPublicKey("appId", "2001-05-01T10:00:00.000Z",refId);
//	}
//	
//	@Test
//	public void TestPublickKey_S001() throws IdAuthenticationBusinessException {
//		java.util.Optional<String> refId = java.util.Optional.of("ida");		
//		keymanagerController.getPublicKey("ida", "2001-05-01T10:00:00.000Z",refId);
//	}
//	
//	@Test
//	public void TestPublickKey_S002() throws IdAuthenticationBusinessException {
//		java.util.Optional<String> refId = java.util.Optional.of("ida");		
//		keymanagerController.getPublicKey("appId", "2001-05-01T10:00:00.000Z",refId);
//	}
//	
//	@Test
//	public void TestPublickKey_S003() throws IdAuthenticationBusinessException {
//		java.util.Optional<String> refId = java.util.Optional.of("refId");		
//		keymanagerController.getPublicKey("appId", "2001-05-01T10:00:00.000Z",refId);
//	}
//	
//	@Test
//	public void TestEncrypt() throws IdAuthenticationBusinessException {
//		CryptomanagerRequestDTO requestDto = new CryptomanagerRequestDTO();
//		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
//		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
//		request.setApplicationId("IDA");
//		request.setData("Test");
//		request.setReferenceId("REFID");
//		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
//		request.setTimeStamp(LocalDateTime.now());
//		requestDto.setRequest(request);
//		keymanagerController.encrypt(requestDto);
//	}
//
//	@Test
//	public void TestDecrypt() throws IdAuthenticationBusinessException {
//		CryptomanagerRequestDTO requestDto = new CryptomanagerRequestDTO();
//		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
//		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
//		request.setApplicationId("IDA");
//		request.setData("Test");
//		request.setReferenceId("REFID");
//		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
//		request.setTimeStamp(LocalDateTime.now());
//		requestDto.setRequest(request);
//		keymanagerController.decrypt(requestDto);
//	}
//	
//	@Test
//	public void Testverify() throws IdAuthenticationBusinessException {		
//		keymanagerController.verify("wertyuiolkjbvfgvfghjoihgfdrthvcxdfg");
//	}
//	
//	@Test
//	public void TestValidate() throws IdAuthenticationBusinessException {
//		TimestampRequestDTO request = new TimestampRequestDTO();
//		String data="test";
//		String signedData="rhjhgfghuhgvfgh";
//		TimestampRequestDto dto= new TimestampRequestDto(signedData,data,LocalDateTime.now(ZoneId.of("UTC")));
//		request.setRequest(dto);
//		keymanagerController.validate(request);
//	}
//	
}

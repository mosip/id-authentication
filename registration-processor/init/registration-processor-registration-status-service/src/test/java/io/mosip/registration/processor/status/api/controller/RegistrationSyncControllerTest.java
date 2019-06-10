
	/** The sync registration dto. */
	@MockBean
	SyncRegistrationDto syncRegistrationDto;

	RegistrationStatusRequestDTO registrationStatusRequestDTO;
	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The list. */
	private List<SyncRegistrationDto> list;

	/** The SyncResponseDtoList. */
	private List<SyncResponseDto> syncResponseDtoList;

	/** The array to json. */
	private String arrayToJson;

	/** The ridValidator. */
	@MockBean
	private RidValidator<String> ridValidator;

	@Mock
	private Environment env;

	RegistrationSyncRequestDTO registrationSyncRequestDTO;

	@Mock
	RegistrationSyncRequestValidator registrationSyncRequestValidator;
	
	@Mock
	private TokenValidator tokenValidator;

	Gson gson = new GsonBuilder().serializeNulls().create();


	@Autowired
	private WebApplicationContext wac;

	/**
	 * Sets the up.
	 *
	 * @throws JsonProcessingException
	 */
	@Before
	public void setUp() throws JsonProcessingException {
		when(env.getProperty("mosip.registration.processor.registration.sync.id"))
				.thenReturn("mosip.registration.sync");
		when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		when(env.getProperty("mosip.registration.processor.application.version")).thenReturn("1.0");

		list = new ArrayList<>();
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setRegistrationId("1002");
		syncRegistrationDto.setLangCode("eng");
		syncRegistrationDto.setIsActive(true);
		list.add(syncRegistrationDto);

		registrationSyncRequestDTO = new RegistrationSyncRequestDTO();
		registrationSyncRequestDTO.setRequest(list);
		registrationSyncRequestDTO.setId("mosip.registration.sync");
		registrationSyncRequestDTO.setVersion("1.0");
		registrationSyncRequestDTO
				.setRequesttime(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		arrayToJson = registrationSyncRequestDTO.toString();
		// arrayToJson = gson.toJson(registrationSyncRequestDTO);
		SyncResponseSuccessDto syncResponseDto = new SyncResponseSuccessDto();
		SyncResponseFailureDto syncResponseFailureDto = new SyncResponseFailureDto();
		syncResponseDto.setRegistrationId("1001");

		syncResponseDto.setStatus("SUCCESS");
		syncResponseFailureDto.setRegistrationId("1001");

		syncResponseFailureDto.setMessage("Registartion Id's are successfully synched in Sync table");
		syncResponseFailureDto.setStatus("FAILURE");
		syncResponseFailureDto.setErrorCode("Test");
		syncResponseDtoList = new ArrayList<>();
		syncResponseDtoList.add(syncResponseDto);
		syncResponseDtoList.add(syncResponseFailureDto);
		Mockito.doNothing().when(tokenValidator).validate(ArgumentMatchers.any(), ArgumentMatchers.any());


	/*	signatureResponse=Mockito.mock(SignatureResponse.class);
		when(signatureUtil.signResponse(Mockito.any(String.class))).thenReturn(signatureResponse);
		when(signatureResponse.getData()).thenReturn("gdshgsahjhghgsad");
*/

	}

	/**
	 * Test creation of A new project succeeds.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void syncRegistrationControllerSuccessTest() throws Exception {
		Mockito.when(syncRegistrationService.decryptAndGetSyncRequest(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(registrationSyncRequestDTO);
		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(syncResponseDtoList);
		Mockito.when(registrationSyncRequestValidator.validate(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(Boolean.TRUE);

		this.mockMvc.perform(post("/sync").accept(MediaType.APPLICATION_JSON_VALUE)
				.cookie(new Cookie("Authorization", arrayToJson)).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(arrayToJson.getBytes()).header("Center-Machine-RefId", "10011_10011")
				.header("timestamp", "2019-05-07T05:13:55.704Z")).andExpect(status().isOk());
	}

	/**
	 * Sync registration controller failure check.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void syncRegistrationControllerFailureTest() throws Exception {

		Mockito.when(syncRegistrationService.sync(ArgumentMatchers.any())).thenReturn(syncResponseDtoList);
		this.mockMvc
				.perform(post("/sync").accept(MediaType.APPLICATION_JSON_VALUE)
						.cookie(new Cookie("Authorization", arrayToJson)).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest());
	}

}
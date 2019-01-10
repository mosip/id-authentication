package io.mosip.preregistration.datasync.test.service;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DataSyncServiceTest {/*

	@Mock
	private InterfaceDataSyncRepo interfaceDataSyncRepo;

	@Mock
	private ProcessedDataSyncRepo processedDataSyncRepo;

	@Autowired
	private DataSyncService dataSyncService;
	//
	// @Value("${preRegResourceUrl}")
	// private String preRegResourceUrl;
	//
	// @Value("${docRegResourceUrl}")
	// private String docRegResourceUrl;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	*//**
	 * Autowired reference for $link{DataSyncServiceUtil}
	 *//*
	@Autowired
	DataSyncServiceUtil serviceUtil;

	String preid = "";
	List<ExceptionJSONInfoDTO> errlist = new ArrayList<>();
	ExceptionJSONInfoDTO exceptionJSONInfo = new ExceptionJSONInfoDTO("", "");
	MainResponseDTO<PreRegistrationIdsDTO> dataSyncResponseDTO = new MainResponseDTO<>();
	MainResponseDTO<String> storeResponseDTO = new MainResponseDTO<>();
	String resTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
	// DataSyncDTO requestDto = new DataSyncDTO();
	PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
	DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
	private JSONObject jsonTestObject;
	// ReverseDataSyncDTO reverseDto = new ReverseDataSyncDTO();
	MainRequestDTO<ReverseDataSyncRequestDTO> reverseRequestDTO = new MainRequestDTO<>();
	MainResponseDTO<ReverseDatasyncReponseDTO> reverseResponseDTO = new MainResponseDTO<>();
	Map<String, String> requestMap = new HashMap<>();
	Map<String, String> requiredRequestMap = new HashMap<>();
	byte[] pFile = null;
	private Object toDate;
	private Object fromDate;

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;
	*//**
	 * Reference for ${demographic.resource.url} from property file
	 *//*
	@Value("${demographic.resource.url}")
	private String demographicResourceUrl;

	*//**
	 * Reference for ${document.resource.url} from property file
	 *//*
	@Value("${document.resource.url}")
	private String documentResourceUrl;

	*//**
	 * Reference for ${booking.resource.url} from property file
	 *//*
	@Value("${booking.resource.url}")
	private String bookingResourceUrl;

	@Before
	public void setUp() throws URISyntaxException, IOException, org.json.simple.parser.ParseException, ParseException {
		preid = "23587986034785";

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();
		URI uri = new URI(
				classLoader.getResource("pre-registration-test.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File jsonFileTest = new File(uri.getPath());
		jsonTestObject = (JSONObject) parser.parse(new FileReader(jsonFileTest));
		pFile = Files.readAllBytes(jsonFileTest.toPath());

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		// demography = new PreRegistrationEntity();
		// demography.setCr_appuser_id("Rajath");
		// demography.setCreateDateTime(times);
		// demography.setStatusCode("SAVE");
		// demography.setLangCode("12L");
		// demography.setPreRegistrationId(preid);
		// demography.setApplicantDetailJson(jsonTestObject.toString().getBytes("UTF-8"));

		byte[] dFile = null;

		File file = new File(classLoader.getResource("Doc.pdf").getFile());
		uri = new URI(classLoader.getResource("Doc.pdf").getFile().trim().replaceAll("\\u0020", "%20"));
		file = new File(uri.getPath());
		dFile = Files.readAllBytes(file.toPath());

		// DocumentEntity documentEntity = new DocumentEntity(1, "75391783729406",
		// "Doc.pdf", "address", "POA", "PDF",
		// dFile, "Draft", "ENG", "Jagadishwari", new
		// Timestamp(System.currentTimeMillis()), "Jagadishwari",
		// new Timestamp(System.currentTimeMillis()));
		//
		// docEntityList.add(documentEntity);

		List<Object> responseList = new ArrayList<>();
		dataSyncResponseDTO.setStatus(Boolean.TRUE);
		dataSyncResponseDTO.setErr(exceptionJSONInfo);
		dataSyncResponseDTO.setResTime(resTime);

		Map<String, String> list = new HashMap<>();
		list.put("1", "2018-12-28T13:04:53.117Z");
		preRegistrationIdsDTO.setPreRegistrationIds(list);
		preRegistrationIdsDTO.setTransactionId("1111");
		dataSyncResponseDTO.setResponse(preRegistrationIdsDTO);

		// PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		// preRegistrationEntity.setCreateDateTime(times);
		// preRegistrationEntity.setPreRegistrationId("23587986034785");
		// userDetails.add(preRegistrationEntity);

		Date date1 = dateFormat.parse("08/10/2018");
		Date date2 = dateFormat.parse("01/11/2018");
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		Timestamp from = new Timestamp(time1);
		Timestamp to = new Timestamp(time2);

		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate("2018-01-17 00:00:00");
		dataSyncRequestDTO.setToDate("2018-12-17 00:00:00");
		dataSyncRequestDTO.setUserId("256752365832");
		// ex.add(exceptionJSONInfo);
		dataSyncResponseDTO.setResponse(preRegistrationIdsDTO);
		dataSyncResponseDTO.setErr(null);
		dataSyncResponseDTO.setStatus(Boolean.TRUE);
		dataSyncResponseDTO.setResTime(resTime);

		List<String> preRegIds = new ArrayList<String>();
		preRegIds.add("23587986034785");
		ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
		// request.setPre_registration_ids(preRegIds);

		// reverseDto.setReqTime(new Date());
		// reverseDto.setRequest(request);

		MockitoAnnotations.initMocks(this);
		// preRegResourceUrl="http://localhost:9093/v0.1/pre-registration/applicationDataByDateTime";

		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

		ReverseDataSyncRequestDTO reverseDataSyncRequestDTO = new ReverseDataSyncRequestDTO();
		List<String> preRegistrationIds = new ArrayList<>();
		preRegistrationIds.add(preid);
		reverseDataSyncRequestDTO.setPreRegistrationIds(preRegistrationIds);
		reverseDataSyncRequestDTO.setLangCode("AR");
		reverseDataSyncRequestDTO.setCreatedBy("5766477466");
		reverseDataSyncRequestDTO.setCreatedDateTime(times);
		reverseDataSyncRequestDTO.setUpdateBy("5766477466");
		reverseDataSyncRequestDTO.setUpdateDateTime(times);
		reverseRequestDTO.setRequest(reverseDataSyncRequestDTO);
		reverseRequestDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		reverseRequestDTO.setId(idUrl);
		reverseRequestDTO.setVer(versionUrl);

		requestMap.put("id", reverseRequestDTO.getId());
		requestMap.put("ver", reverseRequestDTO.getVer());
		requestMap.put("reqTime", reverseRequestDTO.getReqTime().toString());
		requestMap.put("request", reverseRequestDTO.getRequest().toString());

	}

	// @Test
	// public void successGetPreRegistration() throws Exception {
	// PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
	// dataSyncResponseDTO = new DataSyncResponseDTO<>();
	// dataSyncResponseDTO.setStatus("true");
	//
	// preRegArchiveDTO.setZipBytes(pFile);
	// //
	// preRegArchiveDTO.setFileName(demography.getPreRegistrationId().toString());
	// dataSyncResponseDTO.setResponse(preRegArchiveDTO);
	// dataSyncResponseDTO.setErr(errlist);
	// dataSyncResponseDTO.setResTime(resTime);
	//
	// Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
	// Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);
	//
	// DataSyncResponseDTO<PreRegArchiveDTO> response =
	// dataSyncService.getPreRegistration(preid);
	//
	// assertEquals(response.getResponse().getFileName(),
	// preRegArchiveDTO.getFileName());
	// }

	// @Test(expected = DataSyncRecordNotFoundException.class)
	// public void failureGetPreRegistration() throws Exception {
	// DataSyncRecordNotFoundException exception = new
	// DataSyncRecordNotFoundException(
	// ErrorMessages.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
	// Mockito.when(dataSyncRepository.findDemographyByPreId(null)).thenThrow(exception);
	// dataSyncService.getPreRegistration(" ");
	// }

	// @Test(expected = ZipFileCreationException.class)
	// public void failurezipcreation() throws Exception {
	// ZipFileCreationException exception = new ZipFileCreationException(
	// ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString());
	// demography.setApplicantDetailJson(null);
	// Mockito.when(dataSyncRepository.findDemographyByPreId(preid)).thenReturn(demography);
	// Mockito.when(dataSyncRepository.findDocumentByPreId(preid)).thenReturn(docEntityList);
	// Mockito.when(dataSyncService.getPreRegistration(preid)).thenThrow(exception);
	// }

	@Test
	public void storeConsumedPreIdsSuccessTest() {

		MainResponseDTO<ReverseDatasyncReponseDTO> expRes = new MainResponseDTO<>();
		ReverseDatasyncReponseDTO reverseDatasyncReponseDTO = new ReverseDatasyncReponseDTO();
		reverseDatasyncReponseDTO.setCountOfStoredPreRegIds("1");
		InterfaceDataSyncEntity interfaceDataSyncEntity = new InterfaceDataSyncEntity();
		ProcessedPreRegEntity processedEntity = new ProcessedPreRegEntity();
		storeResponseDTO.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		storeResponseDTO.setStatus(Boolean.TRUE);
		storeResponseDTO.setResTime(resTime);
		storeResponseDTO.setErr(null);
		expRes.setResponse(reverseDatasyncReponseDTO);

		interfaceDataSyncEntity.setLangCode("AR");
		processedEntity.setLangCode("AR");
		processedEntity.setCrBy("5766477466");
		processedEntity.setStatusCode("");
		List<InterfaceDataSyncEntity> savedList = new ArrayList<>();
		savedList.add(interfaceDataSyncEntity);

		Mockito.when(interfaceDataSyncRepo.saveAll(ArgumentMatchers.any())).thenReturn(savedList);
		Mockito.when(processedDataSyncRepo.existsById(preid)).thenReturn(Mockito.anyBoolean());
		Mockito.when(processedDataSyncRepo.save(processedEntity)).thenReturn(processedEntity);
		MainResponseDTO<ReverseDatasyncReponseDTO> actRes = dataSyncService
				.storeConsumedPreRegistrations(reverseRequestDTO);
		assertEquals(actRes.getResponse().getCountOfStoredPreRegIds(),
				expRes.getResponse().getCountOfStoredPreRegIds());

	}

	// @Test(expected = ReverseDataFailedToStoreException.class)
	// public void reverseDataSyncFailureTest() {
	// InterfaceDataSyncEntity interfaceDataSyncEntity = new
	// InterfaceDataSyncEntity();
	// List<InterfaceDataSyncEntity> savedList = new ArrayList<>();
	// interfaceDataSyncEntity.setLangCode("AR");
	// MainResponseDTO<ReverseDatasyncReponseDTO> expRes = new MainResponseDTO<>();
	// expRes.setErr(null);
	// expRes.setStatus("false");
	// ReverseDatasyncReponseDTO reverseDatasyncReponseDTO = new
	// ReverseDatasyncReponseDTO();
	// reverseDatasyncReponseDTO.setCountOfStoredPreRegIds("1");
	//
	// expRes.setResponse(reverseDatasyncReponseDTO);
	// InterfaceDataSyncTablePK ipprlst_PK = new InterfaceDataSyncTablePK();
	// ipprlst_PK.setPreregId("23587986034785");
	//
	// interfaceDataSyncEntity.setIpprlst_PK(ipprlst_PK);
	//
	// savedList.add(interfaceDataSyncEntity);
	// ReverseDataFailedToStoreException exception = new
	// ReverseDataFailedToStoreException(
	// ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),
	// ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
	//
	// List<String> preRegIds = new ArrayList<String>();
	// ReverseDataSyncRequestDTO request = new ReverseDataSyncRequestDTO();
	//
	// Mockito.when(interfaceDataSyncRepo.saveAll(null)).thenThrow(exception);
	// Mockito.when(processedDataSyncRepo.existsById(ArgumentMatchers.any())).thenReturn(true);
	// Mockito.when(processedDataSyncRepo.save(ArgumentMatchers.any())).thenReturn(true);
	//
	// MainResponseDTO<ReverseDatasyncReponseDTO> actRes = dataSyncService
	// .storeConsumedPreRegistrations(reverseRequestDTO);
	// System.out.println("size 1: " + actRes.getStatus());
	// System.out.println("size 2: " + expRes.getStatus());
	// assertEquals(actRes.getStatus(), expRes.getStatus());
	// }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void retrieveAllPreRegIdsWithTodateTest() throws ParseException {
		System.out.println("inside retrieveAllPreRegIdsWithTodateTest");
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		ResponseEntity<MainListResponseDTO> response = mock(ResponseEntity.class); 
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.HEAD),
				Mockito.<HttpEntity<?>> any(), Mockito.<Class<MainListResponseDTO>> any())).thenReturn(response);
		
		
		MainListResponseDTO<String> mainListResponseDTO = new MainListResponseDTO();
		List<String> pre_registration_ids = new ArrayList<>();
		pre_registration_ids.add("23587986034785");
		mainListResponseDTO.setResponse(pre_registration_ids);
		
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(demographicResourceUrl + "/applicationDataByDateTime").queryParam("fromDate",  "2018-01-17 00:00:00")
				.queryParam("toDate", "2018-12-17 00:00:00");
		String uriBuilder = builder.build().encode().toUriString();
		System.out.println(uriBuilder);
		ResponseEntity<MainListResponseDTO> resp = new ResponseEntity<>(mainListResponseDTO, HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<MainListResponseDTO<?>> httpEntity1 = new HttpEntity<>(headers);
		Mockito.when(restTemplate.exchange(uriBuilder, HttpMethod.GET,
				httpEntity1,MainListResponseDTO.class)).thenReturn(resp);
		
		PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
		preRegIdsByRegCenterIdDTO.setRegistrationCenterId("59276903416082");
		preRegIdsByRegCenterIdDTO.setPreRegistrationIds(pre_registration_ids);
		MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDto = new MainRequestDTO<>();
		requestDto.setRequest(preRegIdsByRegCenterIdDTO);

		ResponseEntity<PreRegIdsByRegCenterIdDTO> respRegCenter = new ResponseEntity<>(preRegIdsByRegCenterIdDTO,
				HttpStatus.OK);
		builder = UriComponentsBuilder
				.fromHttpUrl(bookingResourceUrl + "/bookedPreIdsByRegId");
		HttpEntity<MainResponseDTO<?>> httpEntity = new HttpEntity(Mockito.any(), Mockito.any());
		uriBuilder = builder.build().encode().toUriString();
		Mockito.when(restTemplate.exchange(uriBuilder, ArgumentMatchers.any(HttpMethod.class),
				httpEntity, ArgumentMatchers.<Class<PreRegIdsByRegCenterIdDTO>>any()))
				.thenReturn(respRegCenter);

		
		PreRegistrationIdsDTO preRegResponse = new PreRegistrationIdsDTO();
		Map<String, String> listOfPreIds = new HashMap<>();
		listOfPreIds.put("23587986034785", "2018-12-28T13:04:53.117Z");
		preRegResponse.setPreRegistrationIds(listOfPreIds);
		preRegResponse.setTransactionId("09876543");
		MainRequestDTO<DataSyncRequestDTO> mainReq = new MainRequestDTO<>();
		mainReq.setId("mosip.pre-registration.datasync");
		mainReq.setVer("1.0");
		mainReq.setReqTime(new Date());
		mainReq.setRequest(dataSyncRequestDTO);
		MainResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegIds(mainReq);
		assertEquals(actualRes.getResponse().getPreRegistrationIds().get(0),
				dataSyncResponseDTO.getResponse().getPreRegistrationIds().get(0));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void retrieveAllPreRegIdsWithoutTodateTest() throws ParseException {

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

		dataSyncRequestDTO.setToDate("");

		List<String> responseList = new ArrayList<String>();
		MainResponseDTO responseDTO = new MainResponseDTO<>();
		responseList.add("23587986034785");
		responseDTO.setStatus(Boolean.TRUE);
		responseDTO.setErr(null);
		responseDTO.setResponse(responseList);
		ResponseEntity<MainResponseDTO> resp = new ResponseEntity<>(responseDTO, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
				ArgumentMatchers.any(), ArgumentMatchers.<Class<MainResponseDTO>>any())).thenReturn(resp);

		PreRegistrationIdsDTO preRegResponse = new PreRegistrationIdsDTO();
		Map<String, String> listOfPreIds = new HashMap<>();
		listOfPreIds.put("23587986034785", "2018-12-28T13:04:53.117Z");

		preRegResponse.setPreRegistrationIds(listOfPreIds);
		preRegResponse.setTransactionId("09876543");

		MainRequestDTO<DataSyncRequestDTO> mainReq = new MainRequestDTO<>();
		mainReq.setId("mosip.pre-registration.datasync");
		mainReq.setVer("1.0");
		mainReq.setReqTime(new Date());
		mainReq.setRequest(dataSyncRequestDTO);

		MainResponseDTO<PreRegistrationIdsDTO> actualRes = dataSyncService.retrieveAllPreRegIds(mainReq);
		assertEquals(actualRes.getResponse().getPreRegistrationIds().get(0),
				dataSyncResponseDTO.getResponse().getPreRegistrationIds().get(0));
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// @Test(expected = RecordNotFoundForDateRange.class)
	// public void retrieveAllPreRegIdsFailure() throws ParseException {
	// RecordNotFoundForDateRange exception = new
	// RecordNotFoundForDateRange(ErrorCodes.PRG_DATA_SYNC_001.toString(),
	// ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
	//
	// RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
	// Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
	//
	// dataSyncRequestDTO.setFromDate("");
	// dataSyncRequestDTO.setToDate("");
	//
	// List<String> responseList = new ArrayList<String>();
	// ResponseDTO responseDTO = new ResponseDTO<>();
	// responseList.add("23587986034785");
	// responseDTO.setStatus("true");
	// responseDTO.setErr(null);
	// responseDTO.setResponse(responseList);
	// ResponseEntity<ResponseDTO> resp = new ResponseEntity<>(responseDTO,
	// HttpStatus.OK);
	// Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(),
	// ArgumentMatchers.any(HttpMethod.class),
	// ArgumentMatchers.any(),
	// ArgumentMatchers.<Class<ResponseDTO>>any())).thenReturn(resp);
	//
	// DataSyncResponseDTO<PreRegistrationIdsDTO> responseDSDTO = dataSyncService
	// .retrieveAllPreRegid(dataSyncRequestDTO);
	// assertEquals(responseDSDTO.getErr().get(0).toString(),
	// errlist.get(0).toString());
	// }

	//
	// @Test(expected = TablenotAccessibleException.class)
	// public void retriveAllPreRegIdTableNotAccessCheck() throws ParseException {
	// TablenotAccessibleException exception = new TablenotAccessibleException();
	// Mockito.when(dataSyncRepo.findBycreateDateTimeBetween(ArgumentMatchers.any(),
	// ArgumentMatchers.any()))
	// .thenThrow(exception);
	// dataSyncService.retrieveAllPreRegid(dataSyncRequestDTO);
	//
	// }

*/}

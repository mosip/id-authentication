package io.mosip.preregistration.datasync.test.controller;

/**
 * @author M1046129
 *
 */
//@RunWith(SpringRunner.class)
//@WebMvcTest(DataSyncController.class)
public class DataSyncControllerTest {/*

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataSyncService dataSyncService;

	String preId = "";
	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = null;
	String status = "";
	@SuppressWarnings("rawtypes")
	MainResponseDTO responseDto = new MainResponseDTO<>();
	Timestamp resTime = null;
	String filename = "";
	byte[] bytes = null;
	MainRequestDTO<ReverseDataSyncRequestDTO> reverseDataSyncDTO = new MainRequestDTO<>();
	private Object jsonObject = null;
	private Object jsonObjectRev = null;

	@Before
	public void setUp() throws URISyntaxException, FileNotFoundException, IOException, ParseException {
		preId = "29107415046379";
		status = "true";
		resTime = new Timestamp(System.currentTimeMillis());
		bytes = new byte[1024];
		filename = "Doc.pdf";

		ReverseDataSyncRequestDTO requestDTO = new ReverseDataSyncRequestDTO();
		List<String> pre_registration_ids = new ArrayList<>();
		pre_registration_ids.add("75391783729406");
		pre_registration_ids.add("75391783729407");
		pre_registration_ids.add("75391783729408");
		requestDTO.setPreRegistrationIds(pre_registration_ids);
		reverseDataSyncDTO.setRequest(requestDTO);

		preId = "29107415046379";
		status = "true";
		resTime = new Timestamp(System.currentTimeMillis());

		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI dataSyncUri = new URI(
				classLoader.getResource("data-sync.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(dataSyncUri.getPath());
		jsonObject = parser.parse(new FileReader(file));

		URI reverseDataSyncUri = new URI(
				classLoader.getResource("reverse-data-sync.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file1 = new File(reverseDataSyncUri.getPath());

		jsonObjectRev = parser.parse(new FileReader(file1));

	}

	@Test
	public void successRetrievePreidsTest() throws Exception {

		exceptionJSONInfo = new ExceptionJSONInfo("", "");
		PreRegArchiveDTO responseList = new PreRegArchiveDTO();
		responseList.setZipBytes(bytes);
		responseList.setFileName(filename);
		errlist.add(exceptionJSONInfo);
		responseDto.setResponse(responseList);

		Mockito.when(dataSyncService.getPreRegistrationData(preId)).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON).param("preId", "29107415046379");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());

	}

	
	@Test
	public void retrieveAllpregIdSuccessTest(){
		MainRequestDTO<DataSyncRequestDTO> dataSyncDTO = new MainRequestDTO<>();
		DataSyncRequestDTO dataSyncRequestDTO = new DataSyncRequestDTO();
		dataSyncRequestDTO.setRegClientId("59276903416082");
		dataSyncRequestDTO.setFromDate("01/01/2011 00:00:00");
		dataSyncRequestDTO.setToDate("01/01/2013 00:00:00");
		dataSyncRequestDTO.setUserId("User1");
		dataSyncDTO.setId("mosip.pre-registration.datasync");
		dataSyncDTO.setRequest(dataSyncRequestDTO);
		dataSyncDTO.setReqTime(new Timestamp(System.currentTimeMillis()));
		dataSyncDTO.setVer("1.0");

		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		Map<String,String> list = new HashMap<>();
		list.put("1","2018-12-28T13:04:53.117Z");

		preRegistrationIdsDTO.setPreRegistrationIds(list);
		preRegistrationIdsDTO.getTransactionId();

		responseDto.setErr(null);
		responseDto.setStatus("true");
		responseDto.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
		responseDto.setResponse(preRegistrationIdsDTO);

		Mockito.when(dataSyncService.retrieveAllPreRegIds(Mockito.any())).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/data-sync/datasync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObject.toString());
		System.out.println(requestBuilder);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void reverseDatasyncSuccessTest(){
		MainResponseDTO<ReverseDatasyncReponseDTO> responseDto = new MainResponseDTO<>();	
		List responseList = new ArrayList<>();
		responseList.add(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		responseDto.setErr(null);
		responseDto.setStatus(true);
		responseDto.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
		responseDto.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
		Mockito.when(dataSyncService.storeConsumedPreRegistrations(Mockito.any())).thenReturn(responseDto);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/data-sync/reverseDataSync")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(jsonObjectRev.toString());
		System.out.println(requestBuilder);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

*/}

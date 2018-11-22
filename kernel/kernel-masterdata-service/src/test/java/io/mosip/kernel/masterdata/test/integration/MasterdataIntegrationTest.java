package io.mosip.kernel.masterdata.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.GenderType;
import io.mosip.kernel.masterdata.entity.GenderTypeId;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.HolidayId;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.ReasonList;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistoryId;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.TitleId;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;
import io.mosip.kernel.masterdata.repository.ReasonCategoryRepository;
import io.mosip.kernel.masterdata.repository.ReasonListRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterdataIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BlacklistedWordsRepository wordsRepository;

	@MockBean
	private DocumentCategoryRepository documentCategoryRepository;

	@Autowired
	DocumentCategoryService documentCategoryService;

	List<DocumentCategory> entities;

	DocumentCategory category;

	List<BlacklistedWords> words;

	@MockBean
	private GenderTypeRepository genderTypeRepository;

	private List<GenderType> genderTypes;

	private List<GenderType> genderTypesNull;

	private GenderTypeId genderId;

	@MockBean
	private HolidayRepository holidayRepository;

	private List<Holiday> holidays;

	@MockBean
	IdTypeRepository idTypeRepository;

	IdType idType;

	@MockBean
	ReasonCategoryRepository reasonRepository;

	@MockBean
	ReasonListRepository reasonListRepository;

	private List<ReasonCategory> reasoncategories;

	private List<ReasonList> reasonList;

	private static final String REASON_LIST_REQUEST="{ \"reasonList\": [ { \"code\": \"RL1\", \"name\": \"reas_list\", \"description\": \"reason List\", \"reasonCategoryCode\": \"RC5\", \"langCode\": \"ENG\", \"isActive\": true, \"deleted\": false }] }";
	private static final String REASON_EMPTY_LIST_REQUEST="{ \"reasonList\": [] }";
	private static final String REASON_CATEGORY_REQUEST= "{ \"reasonCategories\": [ { \"code\": \"RC9\", \"name\": \"reason_category\", \"description\": \"reason categroy\", \"langCode\": \"ENG\" } ] }";
	private static final String REASON_EMPTY_CATEGORY_LIST="{ \"reasonCategories\": [] }";
	

	@MockBean
	RegistrationCenterHistoryRepository repository;

	RegistrationCenterHistory center;

	List<RegistrationCenterHistory> centers = new ArrayList<>();

	@MockBean
	RegistrationCenterRepository registrationCenterRepository;

	RegistrationCenter registrationCenter;
	RegistrationCenter banglore;
	RegistrationCenter chennai;

	List<RegistrationCenter> registrationCenters = new ArrayList<>();

	@MockBean
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	RegistrationCenterUserMachineHistory registrationCenterUserMachineHistory;

	RegistrationCenterUserMachineHistoryId registrationCenterUserMachineHistoryId;

	List<RegistrationCenterUserMachineHistory> registrationCenterUserMachineHistories = new ArrayList<>();

	@MockBean
	private TitleRepository titleRepository;

	private List<Title> titleList;

	private List<Title> titlesNull;

	private TitleId titleId;

	@Before
	public void setUp() {
		blacklistedSetup();

		genderTypeSetup();

		holidaySetup();

		idTypeSetup();

		packetRejectionSetup();

		registrationCenterHistorySetup();

		registrationCenterSetup();

		registrationCenterUserMachineSetup();

		titleIntegrationSetup();

		documentCategorySetUp();
	}

	private void documentCategorySetUp() {
		category = new DocumentCategory();
		category.setCode("DC001");
		entities = new ArrayList<>();
		entities.add(category);
	}

	private void titleIntegrationSetup() {
		titleList = new ArrayList<>();
		Title title = new Title();
		titleId = new TitleId();
		titleId.setLanguageCode("ENG");
		titleId.setTitleCode("ABC");
		title.setIsActive(true);
		title.setCreatedBy("Ajay");
		title.setCreatedtimes(null);
		title.setId(titleId);
		title.setTitleDescription("AAAAAAAAAAAA");
		title.setTitleName("HELLO");
		title.setUpdatedBy("XYZ");
		title.setUpdatedtimes(null);
		titleList.add(title);
	}

	private void registrationCenterUserMachineSetup() {
		registrationCenterUserMachineHistoryId = new RegistrationCenterUserMachineHistoryId("1", "1", "1");
		registrationCenterUserMachineHistory = new RegistrationCenterUserMachineHistory();
		registrationCenterUserMachineHistory.setId(registrationCenterUserMachineHistoryId);
		registrationCenterUserMachineHistory.setEffectivetimes(LocalDateTime.now().minusDays(1));
	}

	private void registrationCenterSetup() {
		registrationCenter = new RegistrationCenter();
		registrationCenter.setId("1");
		registrationCenter.setName("bangalore");
		registrationCenter.setLatitude("12.9180722");
		registrationCenter.setLongitude("77.5028792");
		registrationCenter.setLanguageCode("ENG");
		registrationCenters.add(registrationCenter);

		banglore = new RegistrationCenter();
		banglore.setId("1");
		banglore.setName("bangalore");
		banglore.setLatitude("12.9180722");
		banglore.setLongitude("77.5028792");
		banglore.setLanguageCode("ENG");
		banglore.setLocationCode("BLR");
		chennai = new RegistrationCenter();
		chennai.setId("2");
		chennai.setName("Bangalore Central");
		chennai.setLanguageCode("ENG");
		chennai.setLocationCode("BLR");
		registrationCenters.add(banglore);
		registrationCenters.add(chennai);

	}

	private void registrationCenterHistorySetup() {
		center = new RegistrationCenterHistory();
		center.setId("1");
		center.setName("bangalore");
		center.setLatitude("12.9180722");
		center.setLongitude("77.5028792");
		center.setLanguageCode("ENG");
		center.setLocationCode("BLR");
		centers.add(center);
	}

	private void packetRejectionSetup() {
		ReasonCategory reasonCategory = new ReasonCategory();
		ReasonList reasonListObj = new ReasonList();
		reasonList = new ArrayList<>();
		reasonListObj.setCode("RL1");
		reasonListObj.setLangCode("ENG");
		reasonListObj.setDescription("reasonList");
		reasonList.add(reasonListObj);
		reasonCategory.setReasonList(reasonList);
		reasonCategory.setCode("RC1");
		reasonCategory.setName("reasonCategory");
		reasonCategory.setDescription("reason_category");
		reasonCategory.setLangCode("ENG");
		reasonCategory.setIsActive(true);
		reasonCategory.setIsDeleted(false);
		reasoncategories = new ArrayList<>();
		reasoncategories.add(reasonCategory);
	}

	private void idTypeSetup() {
		idType = new IdType();
		idType.setActive(true);
		idType.setCrBy("testCreation");
		idType.setLangCode("ENG");
		idType.setCode("POA");
		idType.setDescr("Proof Of Address");
	}

	private void holidaySetup() {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		holidays = new ArrayList<>();
		Holiday holiday = new Holiday();
		holiday.setHolidayId(new HolidayId(1, "KAR", date, "ENG"));
		holiday.setHolidayName("Diwali");
		holiday.setCreatedBy("John");
		holiday.setCreatedtimes(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(new HolidayId(1, "KAH", date, "ENG"));
		holiday2.setHolidayName("Durga Puja");
		holiday2.setCreatedBy("John");
		holiday2.setCreatedtimes(specificDate);
		holiday2.setHolidayDesc("Diwali");
		holiday2.setIsActive(true);

		holidays.add(holiday);
		holidays.add(holiday2);
	}

	private void genderTypeSetup() {
		genderTypes = new ArrayList<>();
		genderTypesNull = new ArrayList<>();
		GenderType genderType = new GenderType();
		genderId = new GenderTypeId();
		genderId.setGenderCode("123");
		genderId.setGenderName("Raj");
		genderType.setIsActive(true);
		genderType.setCreatedBy("John");
		genderType.setCreatedtimes(null);
		genderType.setIsDeleted(true);
		genderType.setDeletedtimes(null);
		genderType.setId(genderId);
		genderType.setLanguageCode("ENG");
		genderType.setUpdatedBy("Dom");
		genderType.setUpdatedtimes(null);
		genderTypes.add(genderType);
	}

	private void blacklistedSetup() {
		words = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("abc");
		blacklistedWords.setLangCode("ENG");
		blacklistedWords.setDescription("no description available");

		words.add(blacklistedWords);
	}

	// -----------------------------BlacklistedWordsTest----------------------------------
	@Test
	public void testGetAllWordsBylangCodeSuccess() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(words);
		mockMvc.perform(get("/blacklistedwords/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void testGetAllWordsBylangCodeNullResponse() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(null);
		mockMvc.perform(get("/blacklistedwords/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void testGetAllWordsBylangCodeEmptyArrayResponse() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/blacklistedwords/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void testGetAllWordsBylangCodeFetchException() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/blacklistedwords/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetAllWordsBylangCodeNullArgException() throws Exception {
		mockMvc.perform(get("/blacklistedwords/{langcode}", " ")).andExpect(status().isNotFound());
	}

	// -----------------------------GenderTypeTest----------------------------------
	@Test
	public void testGetGenderByLanguageCodeFetchException() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLanguageCode("ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/gendertype/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetGenderByLanguageCodeNotFoundException() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLanguageCode("ENG")).thenReturn(genderTypesNull);

		mockMvc.perform(get("/gendertype/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void testGetAllGenderFetchException() throws Exception {

		Mockito.when(genderTypeRepository.findAll(GenderType.class)).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/gendertype").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetAllGenderNotFoundException() throws Exception {

		Mockito.when(genderTypeRepository.findAll(GenderType.class)).thenReturn(genderTypesNull);

		mockMvc.perform(get("/gendertype").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}

	@Test
	public void testGetGenderByLanguageCode() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLanguageCode(Mockito.anyString())).thenReturn(genderTypes);
		mockMvc.perform(get("/gendertype/{languageCode}", "ENG")).andExpect(status().isOk());

	}

	@Test
	public void testGetAllGenders() throws Exception {
		Mockito.when(genderTypeRepository.findAll(GenderType.class)).thenReturn(genderTypes);
		mockMvc.perform(get("/gendertype")).andExpect(status().isOk());

	}

	// -----------------------------HolidayTest----------------------------------

	@Test
	public void testGetHolidayAllHolidaysSuccess() throws Exception {
		when(holidayRepository.findAll(Holiday.class)).thenReturn(holidays);
		mockMvc.perform(get("/holidays")).andExpect(status().isOk());
	}

	@Test
	public void testGetAllHolidaNoHolidayFound() throws Exception {
		mockMvc.perform(get("/holidays")).andExpect(status().isNotFound());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllHolidaysHolidayFetchException() throws Exception {
		when(holidayRepository.findAll(Mockito.any(Class.class))).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetHolidayByIdSuccess() throws Exception {
		when(holidayRepository.findAllByHolidayIdId(any(Integer.class))).thenReturn(holidays);
		mockMvc.perform(get("/holidays/{holidayId}", 1)).andExpect(status().isOk());
	}

	@Test
	public void testGetHolidayByIdHolidayFetchException() throws Exception {
		when(holidayRepository.findAllByHolidayIdId(any(Integer.class))).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays/{holidayId}", 1)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetHolidayByIdNoHolidayFound() throws Exception {
		mockMvc.perform(get("/holidays/{holidayId}", 1)).andExpect(status().isNotFound());
	}

	@Test
	public void testGetHolidayByIdAndLangCodeSuccess() throws Exception {
		when(holidayRepository.findHolidayByHolidayIdIdAndHolidayIdLangCode(any(Integer.class), anyString()))
				.thenReturn(holidays);
		mockMvc.perform(get("/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isOk());
	}

	@Test
	public void testGetHolidayByIdAndLangCodeHolidayFetchException() throws Exception {
		when(holidayRepository.findHolidayByHolidayIdIdAndHolidayIdLangCode(any(Integer.class), anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays/{holidayId}/{languagecode}", 1, "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetHolidayByIdAndLangCodeHolidayNoDataFound() throws Exception {
		mockMvc.perform(get("/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isNotFound());
	}

	// -----------------------------IdTypeTest----------------------------------

	@Test
	public void getIdTypesByLanguageCodeFetchExceptionTest() throws Exception {
		when(idTypeRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse("ENG"))
				.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/idtypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getIdTypesByLanguageCodeNotFoundExceptionTest() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		when(idTypeRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse("ENG")).thenReturn(idTypeList);
		mockMvc.perform(get("/idtypes/HIN").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	public void getIdTypesByLanguageCodeTest() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		when(idTypeRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse("ENG")).thenReturn(idTypeList);
		MvcResult result = mockMvc.perform(get("/idtypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		IdTypeResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				IdTypeResponseDto.class);
		assertThat(returnResponse.getIdtypes().get(0).getCode(), is("POA"));
	}

	// -----------------------------PacketRejectionTest----------------------------------
	@Test
	public void getAllRjectionReasonTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse()).thenReturn(reasoncategories);
		mockMvc.perform(get("/packetrejectionreasons")).andExpect(status().isOk());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(reasoncategories);
		mockMvc.perform(get("/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getAllRjectionReasonFetchExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse())
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/packetrejectionreasons")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeFetchExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRjectionReasonRecordsNotFoundTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse()).thenReturn(null);
		mockMvc.perform(get("/packetrejectionreasons")).andExpect(status().isNotFound());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsNotFoundExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
		mockMvc.perform(get("/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/packetrejectionreasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getAllRjectionReasonRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse())
				.thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/packetrejectionreasons")).andExpect(status().isNotFound());
	}

	@Test
	public void createReasonCateogryTest() throws Exception{
		Mockito.when(reasonRepository.saveAll(Mockito.any())).thenReturn(reasoncategories);
		mockMvc.perform(post("/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON).content(REASON_CATEGORY_REQUEST.getBytes())).andExpect(status().isOk());
	}
	
	@Test
	public void createReasonList() throws Exception{
		Mockito.when(reasonListRepository.saveAll(Mockito.any())).thenReturn(reasonList);
		mockMvc.perform(post("/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON).content(REASON_LIST_REQUEST.getBytes())).andExpect(status().isOk());
	}

	@Test
	public void createReasonCateogryFetchExceptionTest() throws Exception{
		Mockito.when(reasonRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(post("/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON).content("{ \"reasonCategories\": [ { \"code\": \"RC9\", \"name\": \"reason_category\", \"description\": \"reason categroy\", \"langCode\": \"ENG\" } ] }".getBytes())).andExpect(status().isInternalServerError());
	}

	@Test
	public void createReasonCateogryDataNotFoundTest() throws Exception{
		Mockito.when(reasonRepository.saveAll(Mockito.any())).thenReturn(reasoncategories);
		mockMvc.perform(post("/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON).content(REASON_EMPTY_CATEGORY_LIST.getBytes())).andExpect(status().isNotFound());
	}
	@Test
	public void createReasonCateogryDataNotFoundInDbTest() throws Exception{
		Mockito.when(reasonRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(post("/packetrejectionreasons/reasoncategory").contentType(MediaType.APPLICATION_JSON).content(REASON_CATEGORY_REQUEST.getBytes())).andExpect(status().isNotFound());
	}

	@Test
	public void createReasonListFetchExceptionTest() throws Exception{
		Mockito.when(reasonListRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(post("/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON).content(REASON_LIST_REQUEST.getBytes())).andExpect(status().isInternalServerError());
	}

	@Test
	public void createReasonListDataNotFoundTest() throws Exception{
		Mockito.when(reasonListRepository.saveAll(Mockito.any())).thenReturn(reasonList);
		mockMvc.perform(post("/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON).content(REASON_EMPTY_LIST_REQUEST.getBytes())).andExpect(status().isNotFound());
	}
	

	@Test
	public void createReasonListDataNotFoundInDbTest() throws Exception{
		Mockito.when(reasonListRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ReasonList>());
		mockMvc.perform(post("/packetrejectionreasons/reasonlist").contentType(MediaType.APPLICATION_JSON).content(REASON_LIST_REQUEST.getBytes())).andExpect(status().isNotFound());
	}

	// -----------------------------RegistrationCenterTest----------------------------------

	@Test
	public void getSpecificRegistrationCenterByIdTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(centers);

		MvcResult result = mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ObjectMapper mapper = new ObjectMapper();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);

		assertThat(returnResponse.getRegistrationCenters().get(0).getId(), is("1"));
	}

	@Test
	public void getRegistrationCentersHistoryNotFoundExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenReturn(null);
		mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryEmptyExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenReturn(new ArrayList<RegistrationCenterHistory>());
		mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersHistoryFetchExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse("1",
				"ENG", LocalDateTime.parse("2018-10-30T19:20:30.45"))).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(
				get("/registrationcentershistory/1/ENG/2018-10-30T19:20:30.45").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andReturn();
	}
	
	@Test
	public void getRegistrationCenterByHierarchylevelAndTextAndLanguageCodeTest() throws Exception {
		centers.add(center);
		when(registrationCenterRepository.findRegistrationCenterHierarchyLevelName("COUNTRY", "INDIA", "ENG"))
				.thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/registrationcenters/COUNTRY/INDIA/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterHierarchyLevelResponseDto returnResponse = mapper.readValue(
				result.getResponse().getContentAsString(), RegistrationCenterHierarchyLevelResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	@Test
	public void getSpecificRegistrationCenterHierarchyLevelFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findRegistrationCenterHierarchyLevelName("ENG", "CITY", "BANGALORE"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/registrationcenters/ENG/CITY/BANGALORE").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getRegistrationCenterHierarchyLevelNotFoundExceptionTest() throws Exception {

		List<RegistrationCenter> emptyList = new ArrayList<>();
		when(registrationCenterRepository.findRegistrationCenterHierarchyLevelName("ENG", "CITY", "BANGALORE"))
				.thenReturn(emptyList);

		mockMvc.perform(get("/registrationcenters/ENG/CITY/BANGALORE").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	// -----------------------------RegistrationCenterIntegrationTest----------------------------------

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findByIdAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("1", "ENG"))
				.thenReturn(null);

		mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findByIdAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("1", "ENG"))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenReturn(new ArrayList<>());
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterFetchExceptionTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersNumberFormatExceptionTest() throws Exception {
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/ae")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("ENG",
				"BLR")).thenReturn(null);

		mockMvc.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeFetchExceptionTest() throws Exception {

		when(registrationCenterRepository.findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("BLR",
				"ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void getAllRegistrationCentersNotFoundExceptionTest() throws Exception {
		when(registrationCenterRepository.findAllByIsActiveTrueAndIsDeletedFalse(RegistrationCenter.class))
				.thenReturn(new ArrayList<RegistrationCenter>());

		mockMvc.perform(get("/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getAllRegistrationCentersFetchExceptionTest() throws Exception {
		when(registrationCenterRepository.findAllByIsActiveTrueAndIsDeletedFalse(RegistrationCenter.class))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getSpecificRegistrationCenterByIdTestSuccess() throws Exception {
		when(registrationCenterRepository.findByIdAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("1", "ENG"))
				.thenReturn(banglore);

		MvcResult result = mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		ObjectMapper mapper = new ObjectMapper();

		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);

		assertThat(returnResponse.getRegistrationCenters().get(0).getId(), is("1"));
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersTest() throws Exception {
		when(registrationCenterRepository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getLatitude(), is("12.9180722"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getLongitude(), is("77.5028792"));
	}

	@Test
	public void getLocationSpecificRegistrationCentersTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("BLR",
				"ENG")).thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(1).getLongitude(), is("77.5028792"));
	}

	@Test
	public void getLocationSpecificMultipleRegistrationCentersTest() throws Exception {
		when(registrationCenterRepository.findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse("BLR",
				"ENG")).thenReturn(registrationCenters);
		MvcResult result = mockMvc
				.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	@Test
	public void getAllRegistrationCenterTest() throws Exception {
		when(registrationCenterRepository.findAllByIsActiveTrueAndIsDeletedFalse(RegistrationCenter.class))
				.thenReturn(registrationCenters);
		MvcResult result = mockMvc.perform(get("/registrationcenters").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				RegistrationCenterResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(1).getName(), is("bangalore"));
		assertThat(returnResponse.getRegistrationCenters().get(2).getName(), is("Bangalore Central"));
	}

	// -----------------------------RegistrationCenterIntegrationTest----------------------------------

	@Test
	public void getRegistrationCentersMachineUserMappingNotFoundExceptionTest() throws Exception {
		when(registrationCenterUserMachineHistoryRepository.findByIdAndEffectivetimesLessThanEqual(
				registrationCenterUserMachineHistoryId, LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenReturn(registrationCenterUserMachineHistories);
		mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getRegistrationCentersMachineUserMappingFetchExceptionTest() throws Exception {
		when(registrationCenterUserMachineHistoryRepository.findByIdAndEffectivetimesLessThanEqual(
				registrationCenterUserMachineHistoryId, LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersDateTimeParseExceptionTest() throws Exception {
		mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45+5:30/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	public void getRegistrationCentersMachineUserMappingTest() throws Exception {
		registrationCenterUserMachineHistories.add(registrationCenterUserMachineHistory);
		when(registrationCenterUserMachineHistoryRepository.findByIdAndEffectivetimesLessThanEqual(
				registrationCenterUserMachineHistoryId, LocalDateTime.parse("2018-10-30T19:20:30.45")))
						.thenReturn(registrationCenterUserMachineHistories);
		MvcResult result = mockMvc.perform(get("/getregistrationmachineusermappinghistory/2018-10-30T19:20:30.45/1/1/1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		RegistrationCenterUserMachineMappingHistoryResponseDto returnResponse = mapper.readValue(
				result.getResponse().getContentAsString(),
				RegistrationCenterUserMachineMappingHistoryResponseDto.class);
		assertThat(returnResponse.getRegistrationCenters().get(0).getCntrId(), is("1"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getUsrId(), is("1"));
		assertThat(returnResponse.getRegistrationCenters().get(0).getMachineId(), is("1"));
	}

	// -----------------------------TitleIntegrationTest----------------------------------
	@Test
	public void testGetTitleByLanguageCodeNotFoundException() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.getThroughLanguageCode("ENG")).thenReturn(titlesNull);

		mockMvc.perform(get("/title/ENG").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}

	@Test
	public void testGetAllTitleFetchException() throws Exception {

		Mockito.when(titleRepository.findAll(Title.class)).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/title").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetAllTitleNotFoundException() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.findAll(Title.class)).thenReturn(titlesNull);

		mockMvc.perform(get("/title").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}

	@Test
	public void testGetAllTitles() throws Exception {
		Mockito.when(titleRepository.findAll(Title.class)).thenReturn(titleList);
		mockMvc.perform(get("/title")).andExpect(status().isOk());

	}

	@Test
	public void testGetTitleByLanguageCode() throws Exception {

		Mockito.when(titleRepository.getThroughLanguageCode(Mockito.anyString())).thenReturn(titleList);
		mockMvc.perform(get("/title/{languageCode}", "ENG")).andExpect(status().isOk());

	}
	// ----------------------------------------document
	// category----------------------------------------

	@Test
	public void addDocumentCategoryListTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"documentCategories\":[{\"name\":\"POI\",\"langCode\":\"ENG\",\"code\":\"D001\",\"description\":\"Proof Of Identity\"}]}}";
		when(documentCategoryRepository.saveAll(Mockito.any())).thenReturn(entities);
		mockMvc.perform(post("/documentcategories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	public void addDocumentCategoryDatabaseConnectionExceptionTest() throws Exception {
		String json = "{\"id\":\"mosip.documentcategories.create\",\"ver\":\"1.0\",\"timestamp\":\"\",\"request\":{\"documentCategories\":[{\"name\":\"POI\",\"langCode\":\"ENG\",\"code\":\"D001\",\"description\":\"Proof Of Identity\"}]}}";
		when(documentCategoryRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(post("/documentcategories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isInternalServerError());
	}

}

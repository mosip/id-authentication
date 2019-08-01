package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.IndividualType;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.ModuleDetail;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.IndividualTypeRepository;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;
import io.mosip.kernel.masterdata.test.TestBootApplication;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterDataPaginationIntegrationTest {

	CodeAndLanguageCodeID codeAndLanguageCodeID;
	CodeAndLanguageCodeID codeAndLanguageCodeIdIndType;

	BlacklistedWords blacklistedWords;
	Title title;
	DocumentCategory documentCategory;
	DocumentType documentType;
	ValidDocument validDocument;
	IndividualType individualType;
	Location location;
	Template template;
	Holiday holiday;
	RegistrationCenterType registrationCenterType;
	Gender gender;
	MachineType machineType;
	MachineSpecification machineSpecification;
	DeviceType deviceType;
	DeviceSpecification deviceSpecification;

	List<BlacklistedWords> blackListedWordsList;
	List<Title> titleList;
	List<DocumentCategory> documentCategorylist;
	List<DocumentType> documentTypelist;
	List<ValidDocument> validDocumentList;
	List<IndividualType> individualTypelist;
	List<Location> locationlist;
	List<Template> templatelist;
	List<Holiday> holidaylist;
	List<RegistrationCenterType> registrationCenterTypelist;
	List<Gender> genderlist;
	List<MachineType> machineTypelist;
	List<MachineSpecification> machineSpecificationlist;
	List<DeviceType> deviceTypelist;
	List<DeviceSpecification> deviceSpecificationlist;

	@MockBean
	BlacklistedWordsRepository wordsRepository;

	@MockBean
	TitleRepository titleRepository;

	@MockBean
	DocumentCategoryRepository documentCategoryRepository;

	@MockBean
	DocumentTypeRepository documentTypeRepository;

	@MockBean
	ValidDocumentRepository validDocumentRepository;

	@MockBean
	IndividualTypeRepository individualTypeRepository;

	@MockBean
	LocationRepository locationRepository;

	@MockBean
	TemplateRepository templateRepository;

	@MockBean
	HolidayRepository holidayRepository;

	@MockBean
	RegistrationCenterTypeRepository registrationCenterTypeRepository;

	@MockBean
	GenderTypeRepository genderTypeRepository;

	@MockBean
	MachineTypeRepository machineTypeRepository;

	@MockBean
	MachineSpecificationRepository machineSpecificationRepository;

	@MockBean
	DeviceTypeRepository deviceTypeRepository;

	@MockBean
	DeviceSpecificationRepository deviceSpecificationRepository;

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		blackListedWordSetUp();
		titleSetUp();
		documentCategorySetUp();
		documentTypeSetUp();
		validDocumentSetUp();
		individualTypeSetUp();
		locationSetUp();
		templateSetUp();
		holidaySetUp();
		genderSetUp();
		machineTypeSetUp();
		machineSpecificationSetUp();
		deviceTypeSetUp();
		deviceSpecificationSetUp();
		registrationCenterTypeSetUp();
	}

	private void blackListedWordSetUp() {
		blacklistedWords = new BlacklistedWords();

		blacklistedWords.setCreatedBy("TEST_CREATOR");
		blacklistedWords.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		blacklistedWords.setDeletedDateTime(null);
		blacklistedWords.setDescription("TEST_BLACK_WORD_DESC");
		blacklistedWords.setIsActive(true);
		blacklistedWords.setIsDeleted(false);
		blacklistedWords.setLangCode("eng");
		blacklistedWords.setUpdatedBy(null);
		blacklistedWords.setWord("TEST_BLACK_WORD");
		blacklistedWords.setUpdatedDateTime(null);

		blackListedWordsList = new ArrayList<>();

		blackListedWordsList.add(blacklistedWords);

	}

	private void titleSetUp() {

		codeAndLanguageCodeID = new CodeAndLanguageCodeID();
		codeAndLanguageCodeID.setCode("TEST_CODE");
		codeAndLanguageCodeID.setLangCode("eng");

		title = new Title();
		title.setCreatedBy("TEST_CREATOR");
		title.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		title.setDeletedDateTime(null);
		title.setIsActive(true);
		title.setIsDeleted(false);
		title.setCode("TEST_CODE");
		title.setLangCode("eng");
		title.setTitleDescription(null);
		title.setTitleName("TEST_TITLE");
		title.setUpdatedBy(null);
		title.setUpdatedDateTime(null);

		titleList = new ArrayList<>();
		titleList.add(title);

	}

	private void documentCategorySetUp() {
		documentCategory = new DocumentCategory();
		documentCategory.setCreatedBy("TEST_CREATOR");
		documentCategory.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		documentCategory.setDeletedDateTime(null);
		documentCategory.setIsActive(true);
		documentCategory.setIsDeleted(false);
		documentCategory.setCode("TEST_CODE");
		documentCategory.setDescription("TEST_DESCRIPTION");
		documentCategory.setLangCode("eng");
		documentCategory.setName("TEST_DOC_CAT");
		documentCategory.setUpdatedBy(null);
		documentCategory.setUpdatedDateTime(null);

		documentCategorylist = new ArrayList<>();
		documentCategorylist.add(documentCategory);

	}

	private void documentTypeSetUp() {
		documentType = new DocumentType();
		documentType.setCreatedBy("TEST_CREATOR");
		documentType.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		documentType.setDeletedDateTime(null);
		documentType.setIsActive(true);
		documentType.setIsDeleted(false);
		documentType.setCode("TEST_CODE");
		documentType.setDescription("TEST_DESCRIPTION");
		documentType.setLangCode("eng");
		documentType.setName("TEST_DOC_TYPE");
		documentType.setUpdatedBy(null);
		documentType.setUpdatedDateTime(null);

		documentTypelist = new ArrayList<>();
		documentTypelist.add(documentType);

	}

	private void validDocumentSetUp() {

		DocumentType documentType = new DocumentType();
		documentType.setCreatedBy("TEST_CREATOR");
		documentType.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		documentType.setDeletedDateTime(null);
		documentType.setIsActive(true);
		documentType.setIsDeleted(false);
		documentType.setLangCode("eng");
		documentType.setUpdatedBy(null);
		documentType.setUpdatedDateTime(null);
		documentType.setLangCode("eng");

		validDocument = new ValidDocument();
		validDocument.setCreatedBy("TEST_CREATOR");
		validDocument.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		validDocument.setDeletedDateTime(null);
		validDocument.setIsActive(true);
		validDocument.setIsDeleted(false);
		validDocument.setLangCode("eng");
		validDocument.setUpdatedBy(null);
		validDocument.setUpdatedDateTime(null);
		validDocument.setDocCategoryCode("TEST_CODE");
		validDocument.setDocumentType(documentType);
		validDocument.setLangCode("eng");

		validDocumentList = new ArrayList<>();
		validDocumentList.add(validDocument);

	}

	private void individualTypeSetUp() {

		codeAndLanguageCodeIdIndType = new CodeAndLanguageCodeID();
		codeAndLanguageCodeIdIndType.setCode("TEST_CODE");
		codeAndLanguageCodeIdIndType.setLangCode("eng");

		individualType = new IndividualType();
		individualType.setCreatedBy("TEST_CREATOR");
		individualType.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		individualType.setDeletedDateTime(null);
		individualType.setIsActive(true);
		individualType.setIsDeleted(false);
		individualType.setName("TEST_IND_TYPE");
		individualType.setUpdatedBy(null);
		individualType.setUpdatedDateTime(null);
		individualType.setCode("TEST_CODE");
		individualType.setLangCode("eng");

		individualTypelist = new ArrayList<>();
		individualTypelist.add(individualType);

	}
	private void locationSetUp() {

		codeAndLanguageCodeIdIndType = new CodeAndLanguageCodeID();
		codeAndLanguageCodeIdIndType.setCode("TEST_CODE");
		codeAndLanguageCodeIdIndType.setLangCode("eng");

		location = new Location();
		location.setCreatedBy("TEST_CREATOR");
		location.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		location.setDeletedDateTime(null);
		location.setIsActive(true);
		location.setIsDeleted(false);
		location.setName("TEST_LOC");
		location.setUpdatedBy(null);
		location.setUpdatedDateTime(null);
		location.setCode("TEST_CODE");
		location.setHierarchyLevel((short) 1);
		location.setHierarchyName("TEST_HIE_NAME");
		location.setLangCode("eng");
		location.setParentLocCode("P_LOC");
		location.setRegistrationCenters(null);

		locationlist = new ArrayList<>();
		locationlist.add(location);

	}

	private void templateSetUp() {
		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		moduleDetail.setDeletedDateTime(null);
		moduleDetail.setIsActive(true);
		moduleDetail.setIsDeleted(false);
		moduleDetail.setName("TEST_MOD_DETAIL");
		moduleDetail.setUpdatedBy(null);
		moduleDetail.setUpdatedDateTime(null);
		moduleDetail.setLangCode("eng");
		moduleDetail.setDescription("TEST_DESC");
		moduleDetail.setId("TEST_ID");

		template = new Template();
		template.setCreatedBy("TEST_CREATOR");
		template.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		template.setDeletedDateTime(null);
		template.setIsActive(true);
		template.setIsDeleted(false);
		template.setName("TEST_LOC");
		template.setUpdatedBy(null);
		template.setUpdatedDateTime(null);
		template.setLangCode("eng");
		template.setDescription("TEMP_TYPE_DESC");
		template.setFileFormatCode(".pdf");
		template.setFileText("TEST_TEXT");
		template.setId("ID");
		template.setModel("");
		template.setModuleDetail(moduleDetail);
		template.setModuleId("");
		template.setModuleName("");
		template.setTemplateFileFormat(null);
		template.setTemplateType(null);
		template.setTemplateTypeCode("TEMPLATE_TYPE_CODE");

		templatelist = new ArrayList<>();
		templatelist.add(template);
	}

	private void holidaySetUp() {
		HolidayID holidayID = new HolidayID();
		holidayID.setHolidayDate(LocalDate.now());
		holidayID.setHolidayName("HOLI");
		holidayID.setLangCode("eng");
		holidayID.setLocationCode("LOC");
		holiday = new Holiday();
		holiday.setCreatedBy("TEST_CREATOR");
		holiday.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		holiday.setDeletedDateTime(null);
		holiday.setIsActive(true);
		holiday.setIsDeleted(false);
		holiday.setUpdatedBy(null);
		holiday.setUpdatedDateTime(null);
		holiday.setId(1);
		holiday.setHolidayDesc("HOLIDAY_DESC");
		holiday.setHolidayId(holidayID);

		holidaylist = new ArrayList<>();
		holidaylist.add(holiday);
	}

	private void genderSetUp() {
		gender = new Gender();
		gender.setCreatedBy("TEST_CREATOR");
		gender.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		gender.setDeletedDateTime(null);
		gender.setIsActive(true);
		gender.setIsDeleted(false);
		gender.setCode("TEST_CODE");
		gender.setLangCode("eng");
		gender.setUpdatedBy(null);
		gender.setUpdatedDateTime(null);
		gender.setGenderName("MLE");

		genderlist = new ArrayList<>();
		genderlist.add(gender);
	}

	private void machineTypeSetUp() {
		machineType = new MachineType();
		machineType.setCreatedBy("TEST_CREATOR");
		machineType.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		machineType.setDeletedDateTime(null);
		machineType.setIsActive(true);
		machineType.setIsDeleted(false);
		machineType.setCode("TEST_CODE");
		machineType.setLangCode("eng");
		machineType.setUpdatedBy(null);
		machineType.setUpdatedDateTime(null);
		machineType.setDescription("MACHINE_TYPE_DESC");
		machineType.setName("M_TYPE");

		machineTypelist = new ArrayList<>();
		machineTypelist.add(machineType);
	}

	private void machineSpecificationSetUp() {
		machineSpecification = new MachineSpecification();
		machineSpecification.setCreatedBy("TEST_CREATOR");
		machineSpecification.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		machineSpecification.setDeletedDateTime(null);
		machineSpecification.setIsActive(true);
		machineSpecification.setIsDeleted(false);
		machineSpecification.setLangCode("eng");
		machineSpecification.setUpdatedBy(null);
		machineSpecification.setUpdatedDateTime(null);
		machineSpecification.setDescription("MACHINE_SPEC_DESC");
		machineSpecification.setName("M_SPEC_TYPE");
		machineSpecification.setBrand("M_SPEC_BRAND");
		machineSpecification.setId("ID");
		machineSpecification.setModel("MAC_SPEC_MODEL");
		machineSpecification.setMachineType(machineType);
		machineSpecification.setMachineTypeCode("M_TYPE_CODE");
		machineSpecification.setMinDriverversion("0.0");

		machineSpecificationlist = new ArrayList<>();
		machineSpecificationlist.add(machineSpecification);
	}

	private void deviceTypeSetUp() {
		deviceType = new DeviceType();
		deviceType.setCreatedBy("TEST_CREATOR");
		deviceType.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		deviceType.setDeletedDateTime(null);
		deviceType.setIsActive(true);
		deviceType.setIsDeleted(false);
		deviceType.setLangCode("eng");
		deviceType.setUpdatedBy(null);
		deviceType.setUpdatedDateTime(null);
		deviceType.setDescription("DEV_TYPE_DESC");
		deviceType.setName("DEV_TYPE");
		deviceType.setCode("DEV_TYPE_CODE");

		deviceTypelist = new ArrayList<>();
		deviceTypelist.add(deviceType);
	}

	private void deviceSpecificationSetUp() {
		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setCreatedBy("TEST_CREATOR");
		deviceSpecification.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		deviceSpecification.setDeletedDateTime(null);
		deviceSpecification.setIsActive(true);
		deviceSpecification.setIsDeleted(false);
		deviceSpecification.setLangCode("eng");
		deviceSpecification.setUpdatedBy(null);
		deviceSpecification.setUpdatedDateTime(null);
		deviceSpecification.setDescription("DEV_SPEC_DESC");
		deviceSpecification.setName("DEV_SPEC");
		deviceSpecification.setBrand("DEV_SPEC_BRAND");
		deviceSpecification.setDeviceType(deviceType);
		deviceSpecification.setDeviceTypeCode("");
		deviceSpecification.setId("ID");
		deviceSpecification.setMinDriverversion("0.0");
		deviceSpecification.setModel("DEV_SPEC_MODEL");

		deviceSpecificationlist = new ArrayList<>();
		deviceSpecificationlist.add(deviceSpecification);
	}

	private void registrationCenterTypeSetUp() {
		registrationCenterType = new RegistrationCenterType();
		registrationCenterType.setCreatedBy("TEST_CREATOR");
		registrationCenterType.setCreatedDateTime(LocalDateTime.of(2019, 01, 2, 14, 5));
		registrationCenterType.setDeletedDateTime(null);
		registrationCenterType.setIsActive(true);
		registrationCenterType.setIsDeleted(false);
		registrationCenterType.setLangCode("eng");
		registrationCenterType.setUpdatedBy(null);
		registrationCenterType.setUpdatedDateTime(null);
		registrationCenterType.setName("REG_CEN_TYPE");
		registrationCenterType.setCode("REG_CEN_TYPE_CODE");
		registrationCenterType.setRegistrationCenters(null);
		registrationCenterType.setDescr("REG_CEN_TYPE_DESCR");

		registrationCenterTypelist = new ArrayList<>();
		registrationCenterTypelist.add(registrationCenterType);
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllBlackListedWordsTest() throws Exception {
		Page<BlacklistedWords> page = new PageImpl<>(blackListedWordsList);
		when(wordsRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenReturn(page);
		mockMvc.perform(get("/blacklistedwords/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllTitlesTest() throws Exception {
		Page<Title> page = new PageImpl<>(titleList);
		when(titleRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenReturn(page);
		mockMvc.perform(get("/title/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDocumentCategoryTest() throws Exception {
		Page<DocumentCategory> page = new PageImpl<>(documentCategorylist);
		when(documentCategoryRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/documentcategories/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDocumentTypeTest() throws Exception {
		Page<DocumentType> page = new PageImpl<>(documentTypelist);
		when(documentTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/documenttypes/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllValidDocumentTest() throws Exception {
		Page<ValidDocument> page = new PageImpl<>(validDocumentList);
		when(validDocumentRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/validdocuments/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllIndividualTypeTest() throws Exception {
		Page<IndividualType> page = new PageImpl<>(individualTypelist);
		when(individualTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/individualtypes/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllLocationTest() throws Exception {
		Page<Location> page = new PageImpl<>(locationlist);
		when(locationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/locations/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllTemplateTest() throws Exception {
		Page<Template> page = new PageImpl<>(templatelist);
		when(templateRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/templates/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllHolidayTest() throws Exception {
		Page<Holiday> page = new PageImpl<>(holidaylist);
		when(holidayRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenReturn(page);
		mockMvc.perform(get("/holidays/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllRegistrationCenterTypeTest() throws Exception {
		Page<RegistrationCenterType> page = new PageImpl<>(registrationCenterTypelist);
		when(registrationCenterTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/registrationcentertypes/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllGendersTest() throws Exception {
		Page<Gender> page = new PageImpl<>(genderlist);
		when(genderTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/gendertypes/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllMachineTypeTest() throws Exception {
		Page<MachineType> page = new PageImpl<>(machineTypelist);
		when(machineTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/machinetypes/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllMachineSpecificationTest() throws Exception {
		Page<MachineSpecification> page = new PageImpl<>(machineSpecificationlist);
		when(machineSpecificationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/machinespecifications/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDeviceTypeTest() throws Exception {
		Page<DeviceType> page = new PageImpl<>(deviceTypelist);
		when(deviceTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/devicetypes/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDeviceSpecTest() throws Exception {
		Page<DeviceSpecification> page = new PageImpl<>(deviceSpecificationlist);
		when(deviceSpecificationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(page);
		mockMvc.perform(get("/devicespecifications/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllBlackListedWordsDataAccessExceptionTest() throws Exception {
		when(wordsRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/blacklistedwords/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllBlackListedWordsNotFoundExceptionTest() throws Exception {
		when(wordsRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenReturn(null);
		mockMvc.perform(get("/blacklistedwords/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllTitlesDataAccessExceptionTest() throws Exception {
		when(titleRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/title/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllTitlesNotFoundExceptionTest() throws Exception {
		when(titleRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenReturn(null);
		mockMvc.perform(get("/title/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllDocumentCategoriesDataAccessExceptionTest() throws Exception {
		when(documentCategoryRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/documentcategories/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDocumentCategoriesNotFoundExceptionTest() throws Exception {
		when(documentCategoryRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/documentcategories/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllDocumentTypesDataAccessExceptionTest() throws Exception {
		when(documentTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/documenttypes/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDocumentTypesNotFoundExceptionTest() throws Exception {
		when(documentTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/documenttypes/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllValidDocumentsDataAccessExceptionTest() throws Exception {
		when(validDocumentRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/validdocuments/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllValidDocumentsNotFoundExceptionTest() throws Exception {
		when(validDocumentRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/validdocuments/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllIndividualTypesDataAccessExceptionTest() throws Exception {
		when(individualTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/individualtypes/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllIndividualTypesNotFoundExceptionTest() throws Exception {
		when(individualTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/individualtypes/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllLocationsDataAccessExceptionTest() throws Exception {
		when(locationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/locations/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllLocationsNotFoundExceptionTest() throws Exception {
		when(locationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/locations/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllTemplatesDataAccessExceptionTest() throws Exception {
		when(templateRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/templates/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllTemplatesNotFoundExceptionTest() throws Exception {
		when(templateRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/templates/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllHolidaysDataAccessExceptionTest() throws Exception {
		when(holidayRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllHolidaysNotFoundExceptionTest() throws Exception {
		when(holidayRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
				.thenReturn(null);
		mockMvc.perform(get("/holidays/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllRegistrationCenterTypesDataAccessExceptionTest() throws Exception {
		when(registrationCenterTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/registrationcentertypes/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllRegistrationCenterTypesNotFoundExceptionTest() throws Exception {
		when(registrationCenterTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/registrationcentertypes/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllGenderTypesDataAccessExceptionTest() throws Exception {
		when(genderTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/gendertypes/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllGenderTypesNotFoundExceptionTest() throws Exception {
		when(genderTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/gendertypes/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllMachineTypesDataAccessExceptionTest() throws Exception {
		when(machineTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/machinetypes/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllMachineTypesNotFoundExceptionTest() throws Exception {
		when(machineTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/machinetypes/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllMachineSpecificationsDataAccessExceptionTest() throws Exception {
		when(machineSpecificationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/machinespecifications/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllMachineSpecificationsNotFoundExceptionTest() throws Exception {
		when(machineSpecificationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/machinespecifications/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllDeviceTypesDataAccessExceptionTest() throws Exception {
		when(deviceTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/devicetypes/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDeviceTypesNotFoundExceptionTest() throws Exception {
		when(deviceTypeRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/devicetypes/all")).andExpect(status().isOk());
	}

	@Test()
	@WithUserDetails("zonal-admin")
	public void getAllDeviceSpecificationsDataAccessExceptionTest() throws Exception {
		when(deviceSpecificationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/devicespecifications/all")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getAllDeviceSpecificationsExceptionTest() throws Exception {
		when(deviceSpecificationRepository
				.findAll(PageRequest.of(0, 10, Sort.by(Direction.fromString("desc"), "createdDateTime"))))
						.thenReturn(null);
		mockMvc.perform(get("/devicespecifications/all")).andExpect(status().isOk());
	}
}
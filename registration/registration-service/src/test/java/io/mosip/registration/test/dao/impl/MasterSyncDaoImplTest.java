package io.mosip.registration.test.dao.impl;

import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dao.impl.MasterSyncDaoImpl;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.BiometricAttributeResponseDto;
import io.mosip.registration.dto.mastersync.BiometricTypeDto;
import io.mosip.registration.dto.mastersync.BiometricTypeResponseDto;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.DocumentTypeDto;
import io.mosip.registration.dto.mastersync.GenderTypeDto;
import io.mosip.registration.dto.mastersync.GenderTypeResponseDto;
import io.mosip.registration.dto.mastersync.IdTypeDto;
import io.mosip.registration.dto.mastersync.LanguageDto;
import io.mosip.registration.dto.mastersync.LanguageResponseDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MasterSyncDto;
import io.mosip.registration.dto.mastersync.ReasonCategoryDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.dto.mastersync.TitleResponseDto;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.mastersync.BiometricAttribute;
import io.mosip.registration.entity.mastersync.BiometricType;
import io.mosip.registration.entity.mastersync.BlacklistedWords;
import io.mosip.registration.entity.mastersync.DocumentCategory;
import io.mosip.registration.entity.mastersync.DocumentType;
import io.mosip.registration.entity.mastersync.GenderType;
import io.mosip.registration.entity.mastersync.IdType;
import io.mosip.registration.entity.mastersync.Language;
import io.mosip.registration.entity.mastersync.Location;
import io.mosip.registration.entity.mastersync.ReasonCategory;
import io.mosip.registration.entity.mastersync.Title;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.repositories.SyncJobRepository;
import io.mosip.registration.repositories.mastersync.BiometricAttributeReposiotry;
import io.mosip.registration.repositories.mastersync.BiometricTypeRepository;
import io.mosip.registration.repositories.mastersync.BlacklistedWordsRepository;
import io.mosip.registration.repositories.mastersync.DocumentCategoryRepo;
import io.mosip.registration.repositories.mastersync.DocumnetTypesRepository;
import io.mosip.registration.repositories.mastersync.GenderRepostry;
import io.mosip.registration.repositories.mastersync.IdTypeReposiotry;
import io.mosip.registration.repositories.mastersync.LanguageRepository;
import io.mosip.registration.repositories.mastersync.LocationMasterRepository;
import io.mosip.registration.repositories.mastersync.ReasonCatogryReposiotry;
import io.mosip.registration.repositories.mastersync.TitleRepository;
import io.mosip.registration.service.impl.MasterSyncServiceImpl;
import ma.glasnost.orika.MapperFacade;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@RunWith(PowerMockRunner.class)
//@PrepareForTest({ CustomObjectMapper.class})
@PrepareForTest({ MapperFacade.class })
public class MasterSyncDaoImplTest {

	// private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	/** The master sync repository. */
	@Mock
	private BlacklistedWordsRepository blackListedWordsRepository;

	/** The master sync repository. */
	@Mock
	private DocumnetTypesRepository documnetTypesRepository;

	/** The master sync repository. */
	@Mock
	private BiometricAttributeReposiotry biometricAttributeReposiotry;

	/** The master sync repository. */
	@Mock
	private BiometricTypeRepository biometricTypeRepository;

	/** The master sync repository. */
	@Mock
	private DocumentCategoryRepo documentCategoryrepository;

	/** The master sync repository. */
	@Mock
	private IdTypeReposiotry idTypeRepository;

	/** The master sync repository. */
	@Mock
	private GenderRepostry genderRepository;

	/** The master sync repository. */
	@Mock
	private LanguageRepository languageRepository;

	/** The master sync repository. */
	@Mock
	private ReasonCatogryReposiotry reasonCatogryReposiotry;

	/** The master sync repository. */
	@Mock
	private LocationMasterRepository locationRepository;

	/** Object for Sync Status Repository. */
	@Mock
	private SyncJobRepository syncStatusRepository;

	/** Object for Sync Status Repository. */
	@Mock
	private TitleRepository titleRepository;

	@Mock
	private MasterSyncDao masterSyncDao;

	@InjectMocks
	private MasterSyncServiceImpl masterSyncServiceImpl;

	@InjectMocks
	private MasterSyncDaoImpl masterSyncDaoImpl;

	@Mock
	private ReasonCategory reasonCatogryy;

	@Mock
	private MapperFacade MAPPER_FACADE;

	@Mock
	private ReasonCategoryDto reasonCatogryType;

	private static ApplicationContext applicationContext = ApplicationContext.getInstance();

	@BeforeClass
	public static void beforeClass() {

		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		applicationContext.setApplicationMessagesBundle();
	}

	@Test
	public void testMasterSyncDaoSucess() throws RegBaseCheckedException {

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		Mockito.when(syncStatusRepository.findBySyncJobId(Mockito.anyString())).thenReturn(masterSyncDetails);

		masterSyncDaoImpl.getMasterSyncStatus("MDS_J00001");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMasterSyncExceptionThrown() throws RegBaseCheckedException {

		Mockito.when(masterSyncDaoImpl.getMasterSyncStatus(Mockito.anyString()))
				.thenThrow(RegBaseCheckedException.class);

	}

	@Test
	public void testMasterSyncDao() throws RegBaseCheckedException {

		PowerMockito.mockStatic(CustomObjectMapper.class);
		PowerMockito.mockStatic(MapperFacade.class);

		MasterSyncDto masterSyncDto = new MasterSyncDto();

		LanguageResponseDto lanugageRespDto = new LanguageResponseDto();
		List<Language> langList = new ArrayList<>();

		Language languageDto = new Language();

		languageDto.setLanguageCode("eng");
		languageDto.setLanguageFamily("english");
		languageDto.setLanguageName("english");
		languageDto.setNativeName("eng");

		LanguageDto languageDto1 = new LanguageDto();

		languageDto1.setLanguageCode("eng");
		languageDto1.setLanguageFamily("english");
		languageDto1.setLanguageName("english");
		languageDto1.setNativeName("eng");

		langList.add(languageDto);

		List<LanguageDto> language = new ArrayList<>();
		language.add(languageDto1);

		lanugageRespDto.setLanguage(language);

		List<LanguageResponseDto> languageResoList = new ArrayList<>();

		languageResoList.add(lanugageRespDto);

		masterSyncDto.setLanguages(languageResoList);

		//

		BiometricTypeResponseDto BiometricTypeRespDto = new BiometricTypeResponseDto();
		List<BiometricTypeDto> biometrictype = new ArrayList<>();

		BiometricType bioType = new BiometricType();
		bioType.setCode("1");
		bioType.setDescription("FigerPrint..");
		bioType.setLangCode("eng");
		bioType.setName("FigerPrint");

		List<BiometricType> bioTypeList = new ArrayList<>();
		bioTypeList.add(bioType);

		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();

		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");

		biometrictype.add(biometricTypeDto);

		BiometricTypeRespDto.setBiometrictype(biometrictype);

		List<BiometricTypeResponseDto> listBioType = new ArrayList<>();
		listBioType.add(BiometricTypeRespDto);

		masterSyncDto.setBiometrictypes(listBioType);

		//

		BiometricAttributeResponseDto BiometricAttriRespDto = new BiometricAttributeResponseDto();
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();

		BiometricAttribute attribute = new BiometricAttribute();

		attribute.setCode("1");
		attribute.setDescription("FigerPrint..");
		attribute.setLangCode("eng");
		attribute.setName("FigerPrint");

		List<BiometricAttribute> bioAttList = new ArrayList<>();
		bioAttList.add(attribute);

		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();

		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");

		biometricattribute.add(biometricAttriDto);

		BiometricAttriRespDto.setBiometricattribute(biometricattribute);

		List<BiometricAttributeResponseDto> listBioAttType = new ArrayList<>();
		listBioAttType.add(BiometricAttriRespDto);

		masterSyncDto.setBiometricattributes(listBioAttType);

		//

		List<BlacklistedWords> BlacklistedWordsList = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();

		blacklistedWords.setWord("agshasa");
		blacklistedWords.setDescription("FigerPrint..");
		blacklistedWords.setLangCode("eng");

		BlacklistedWordsList.add(blacklistedWords);

		BlacklistedWordsDto blacklistedWordsDto = new BlacklistedWordsDto();
		blacklistedWordsDto.setWord("agshasa");
		blacklistedWordsDto.setDescription("FigerPrint..");
		blacklistedWordsDto.setLangCode("eng");

		List<BlacklistedWordsDto> listBlackListedWords = new ArrayList<>();
		listBlackListedWords.add(blacklistedWordsDto);

		masterSyncDto.setBlacklistedwords(listBlackListedWords);

		//

		GenderTypeResponseDto genderTypeResponseDtoDto = new GenderTypeResponseDto();

		List<GenderTypeDto> gender = new ArrayList<>();

		GenderTypeDto genderTypeDto = new GenderTypeDto();

		genderTypeDto.setGenderCode("1");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");

		gender.add(genderTypeDto);

		Gender genderEntity = new Gender();
		genderEntity.setLanguageCode("eng");
		genderEntity.setName("english");

		List<Gender> listGen = new ArrayList<>();
		listGen.add(genderEntity);

		GenderType genderTypeEntity = new GenderType();
		genderTypeEntity.setGenderName("male");
		genderTypeEntity.setIsActive(true);

		List<GenderType> listTypeGen = new ArrayList<>();
		listTypeGen.add(genderTypeEntity);

		genderTypeResponseDtoDto.setGender(gender);

		List<GenderTypeResponseDto> listGenderTypeResponseDtos = new ArrayList<>();
		listGenderTypeResponseDtos.add(genderTypeResponseDtoDto);

		masterSyncDto.setGenders(listGenderTypeResponseDtos);

		//

		TitleResponseDto titleResponseDto = new TitleResponseDto();

		List<TitleDto> title = new ArrayList<>();

		TitleDto titleTypeDto = new TitleDto();

		titleTypeDto.setTitleCode("1");
		titleTypeDto.setTitleDescription("dsddsd");
		titleTypeDto.setTitleName("admin");
		titleTypeDto.setLangCode("eng");

		title.add(titleTypeDto);

		titleResponseDto.setTitle(title);

		List<TitleResponseDto> listTitleResp = new ArrayList<>();
		listTitleResp.add(titleResponseDto);

		Title titleEntity = new Title();
		titleEntity.setTitleDescription("dsddsd");
		titleEntity.setTitleName("admin");

		List<Title> listTitle = new ArrayList<>();
		listTitle.add(titleEntity);

		masterSyncDto.setTitles(listTitleResp);

		//

		IdTypeDto idTypeResponseDto = new IdTypeDto();

		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setName("admin");
		idTypeResponseDto.setDescription("admin");
		idTypeResponseDto.setLangCode("eng");

		List<IdTypeDto> idTypeList = new ArrayList<>();
		idTypeList.add(idTypeResponseDto);
		masterSyncDto.setIdtypes(idTypeList);

		IdType idTypeDto = new IdType();
		idTypeDto.setName("test");
		idTypeDto.setLangCode("eng");
		idTypeDto.setIsActive(true);

		List<IdType> idTypeEntity = new ArrayList<>();
		idTypeEntity.add(idTypeDto);

		//

		DocumentCategoryDto titleResponseDto1 = new DocumentCategoryDto();
		titleResponseDto1.setCode("1");
		titleResponseDto1.setName("POA");
		titleResponseDto1.setDescription("ajkskjska");
		titleResponseDto1.setLangCode("eng");
		List<DocumentCategoryDto> listDocCat = new ArrayList<>();
		listDocCat.add(titleResponseDto1);
		masterSyncDto.setDocumentcategories(listDocCat);

		DocumentCategory docCatogery = new DocumentCategory();
		docCatogery.setCode("1");
		docCatogery.setName("POA");
		docCatogery.setDescription("ajkskjska");
		docCatogery.setLangCode("eng");
		List<DocumentCategory> listDocCatogery = new ArrayList<>();
		listDocCatogery.add(docCatogery);
		//

		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");

		List<DocumentTypeDto> listDocType = new ArrayList<>();
		listDocType.add(titleDocumentTypeDto);

		masterSyncDto.setDocumenttypes(listDocType);

		DocumentType doctype = new DocumentType();
		doctype.setCode("1");
		doctype.setName("Passport");
		doctype.setDescription("ajkskjska");
		doctype.setLangCode("eng");

		List<DocumentType> listDoccType = new ArrayList<>();
		listDoccType.add(doctype);

		//

		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");

		List<LocationDto> listLocation = new ArrayList<>();
		listLocation.add(locationDto);

		masterSyncDto.setLocations(listLocation);

		Location locattion = new Location();
		locattion.setCode("1");
		locattion.setName("english");
		locattion.setLangCode("eng");
		locattion.setHierarchyLevel(1);
		locattion.setHierarchyName("english");
		locattion.setParentLocCode("english");

		List<Location> listLocationList = new ArrayList<>();
		listLocationList.add(locattion);

		ReasonListDto reasonListDto = new ReasonListDto();
		reasonListDto.setCode("1");
		reasonListDto.setLangCode("eng");
		reasonListDto.setDescription("asas");
		reasonListDto.setName("sdjsd");
		reasonListDto.setReasonCategoryCode("asassas");

		Set<ReasonListDto> reasonListDtos = new HashSet<>();
		reasonListDtos.add(reasonListDto);
		List<ReasonListDto> categorieList = new ArrayList<>();
		categorieList.add(reasonListDto);

		ReasonCategoryDto reasonCategoryDto = new ReasonCategoryDto();
		reasonCategoryDto.setCode("1");
		reasonCategoryDto.setLangCode("eng");
		reasonCategoryDto.setDescription("asbasna");
		reasonCategoryDto.setReasonLists(categorieList);

		ReasonCategory reson = new ReasonCategory();
		reson.setCode("1");
		reson.setName("ssdsd");
		List<ReasonCategory> categorie = new ArrayList<>();
		categorie.add(reson);
		masterSyncDto.setReasonCategory(reasonCategoryDto);

		List<LanguageResponseDto> languageType = languageResoList;
		List<BiometricTypeResponseDto> biometricTypeResponseDtos = listBioType;
		List<BiometricAttributeResponseDto> biometricAttribute = listBioAttType;
		List<BlacklistedWordsDto> blacklistedWord = listBlackListedWords;
		List<GenderTypeResponseDto> genderType = listGenderTypeResponseDtos;
		List<TitleResponseDto> titlesList = listTitleResp;
		List<IdTypeDto> idType = idTypeList;
		List<DocumentCategoryDto> documnetCatogry = listDocCat;
		List<DocumentTypeDto> documentsType = listDocType;
		List<LocationDto> locationsList = listLocation;

		Mockito.when(MAPPER_FACADE.mapAsList(languageType, Language.class)).thenReturn(langList);
		Mockito.when(MAPPER_FACADE.mapAsList(biometricTypeResponseDtos, BiometricType.class)).thenReturn(bioTypeList);
		Mockito.when(MAPPER_FACADE.mapAsList(biometricAttribute, BiometricAttribute.class)).thenReturn(bioAttList);
		Mockito.when(MAPPER_FACADE.mapAsList(blacklistedWord, BlacklistedWords.class)).thenReturn(BlacklistedWordsList);
		Mockito.when(MAPPER_FACADE.mapAsList(genderType, Gender.class)).thenReturn(listGen);
		Mockito.when(MAPPER_FACADE.mapAsList(titlesList, Title.class)).thenReturn(listTitle);
		Mockito.when(MAPPER_FACADE.mapAsList(idType, IdTypeDto.class)).thenReturn(idType);
		Mockito.when(MAPPER_FACADE.mapAsList(documnetCatogry, DocumentCategory.class)).thenReturn(listDocCatogery);
		Mockito.when(MAPPER_FACADE.mapAsList(documentsType, DocumentType.class)).thenReturn(listDoccType);
		Mockito.when(MAPPER_FACADE.mapAsList(locationsList, Location.class)).thenReturn(listLocationList);

		Mockito.when(languageRepository.saveAll(Mockito.any())).thenReturn(langList);
		Mockito.when(biometricTypeRepository.saveAll(Mockito.any())).thenReturn(bioTypeList);
		Mockito.when(biometricAttributeReposiotry.saveAll(Mockito.any())).thenReturn(bioAttList);
		Mockito.when(blackListedWordsRepository.saveAll(Mockito.any())).thenReturn(BlacklistedWordsList);
		Mockito.when(genderRepository.saveAll(Mockito.any())).thenReturn(listTypeGen);
		Mockito.when(titleRepository.saveAll(Mockito.any())).thenReturn(listTitle);
		Mockito.when(idTypeRepository.saveAll(Mockito.any())).thenReturn(idTypeEntity);
		Mockito.when(documentCategoryrepository.saveAll(Mockito.any())).thenReturn(listDocCatogery);
		Mockito.when(documnetTypesRepository.saveAll(Mockito.any())).thenReturn(listDoccType);
		Mockito.when(locationRepository.saveAll(Mockito.any())).thenReturn(listLocationList);
		Mockito.when(reasonCatogryReposiotry.saveAll(Mockito.any())).thenReturn(categorie);

		Mockito.when(reasonCatogryType.getReasonLists()).thenReturn(categorieList);

		masterSyncDaoImpl.insertMasterSyncData(masterSyncDto);

	}

}

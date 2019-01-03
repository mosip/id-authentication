package io.mosip.registration.test.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.demo.DemographicInfo;
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
import io.mosip.registration.dto.mastersync.TitleDto;
import io.mosip.registration.dto.mastersync.TitleResponseDto;
import io.mosip.registration.entity.mastersync.BiometricAttribute;
import io.mosip.registration.entity.mastersync.BiometricType;
import io.mosip.registration.entity.mastersync.BlacklistedWords;
import io.mosip.registration.entity.mastersync.DocumentCategory;
import io.mosip.registration.entity.mastersync.GenderType;
import io.mosip.registration.entity.mastersync.IdType;
import io.mosip.registration.entity.mastersync.Language;
import io.mosip.registration.entity.mastersync.Location;
import io.mosip.registration.entity.mastersync.Title;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.test.util.datastub.DataProvider;
import ma.glasnost.orika.MapperFacade;

public class CustomObjectMapperTest {

	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;
	private static RegistrationDTO registrationDTO;
	private static DemographicInfoDTO demographicInfoDTO;
	private static DemographicDTO demographicDTO;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		// RegistrationDTO
		registrationDTO = DataProvider.getPacketDTO();

		// Set DemographicDTO
		demographicDTO = registrationDTO.getDemographicDTO();

		// DemographicInfoDTO
		demographicInfoDTO = demographicDTO.getDemoInLocalLang();

	}

	@Test
	public void testLanguageDto() {

		LanguageResponseDto lanugageRespDto = new LanguageResponseDto();
		List<LanguageDto> langList = new ArrayList<>();

		LanguageDto languageDto = new LanguageDto();

		languageDto.setLanguageCode("eng");
		languageDto.setLanguageFamily("english");
		languageDto.setLanguageName("english");
		languageDto.setNativeName("eng");

		langList.add(languageDto);

		lanugageRespDto.setLanguage(langList);

		Language convertedTime = mapperFacade.map(lanugageRespDto, Language.class);
		assertEquals(lanugageRespDto.getLanguage().get(0).getLanguageCode(), convertedTime.getLanguageCode());
	}

	@Test
	public void testBiometricTypeDto() {

		BiometricTypeResponseDto BiometricTypeRespDto = new BiometricTypeResponseDto();
		List<BiometricTypeDto> biometrictype = new ArrayList<>();

		BiometricTypeDto biometricTypeDto = new BiometricTypeDto();

		biometricTypeDto.setCode("1");
		biometricTypeDto.setDescription("FigerPrint..");
		biometricTypeDto.setLangCode("eng");
		biometricTypeDto.setName("FigerPrint");

		biometrictype.add(biometricTypeDto);

		BiometricTypeRespDto.setBiometrictype(biometrictype);

		BiometricType biometricType = mapperFacade.map(BiometricTypeRespDto, BiometricType.class);
		assertEquals(BiometricTypeRespDto.getBiometrictype().get(0).getLangCode(), biometricType.getLangCode());
	}

	@Test
	public void testBiometricAttributeResponseDto() {

		BiometricAttributeResponseDto BiometricAttriRespDto = new BiometricAttributeResponseDto();
		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();

		BiometricAttributeDto biometricAttriDto = new BiometricAttributeDto();

		biometricAttriDto.setCode("1");
		biometricAttriDto.setDescription("FigerPrint..");
		biometricAttriDto.setLangCode("eng");
		biometricAttriDto.setName("FigerPrint");

		biometricattribute.add(biometricAttriDto);

		BiometricAttriRespDto.setBiometricattribute(biometricattribute);

		BiometricAttribute biometricType = mapperFacade.map(BiometricAttriRespDto, BiometricAttribute.class);
		assertEquals(BiometricAttriRespDto.getBiometricattribute().get(0).getLangCode(), biometricType.getLangCode());
	}

	@Test
	public void testBlacklistedWordsDto() {

		BlacklistedWordsDto blacklistedWordsDtoRespDto = new BlacklistedWordsDto();

		blacklistedWordsDtoRespDto.setWord("agshasa");
		blacklistedWordsDtoRespDto.setDescription("FigerPrint..");
		blacklistedWordsDtoRespDto.setLangCode("eng");

		BlacklistedWords biometricType = mapperFacade.map(blacklistedWordsDtoRespDto, BlacklistedWords.class);
		assertEquals(blacklistedWordsDtoRespDto.getLangCode(), biometricType.getLangCode());
	}

	@Test
	public void testGenderTypeResponseDto() {

		GenderTypeResponseDto genderTypeResponseDtoDto = new GenderTypeResponseDto();

		List<GenderTypeDto> gender = new ArrayList<>();

		GenderTypeDto genderTypeDto = new GenderTypeDto();

		genderTypeDto.setGenderCode("1");
		genderTypeDto.setGenderName("Male");
		genderTypeDto.setLangCode("eng");

		gender.add(genderTypeDto);

		genderTypeResponseDtoDto.setGender(gender);

		GenderType genderType = mapperFacade.map(genderTypeResponseDtoDto, GenderType.class);
		assertEquals(genderTypeResponseDtoDto.getGender().get(0).getGenderName(), genderType.getGenderName());
	}

	@Test
	public void testTitleResponseDto() {

		TitleResponseDto titleResponseDto = new TitleResponseDto();

		List<TitleDto> title = new ArrayList<>();

		TitleDto titleTypeDto = new TitleDto();

		titleTypeDto.setTitleCode("1");
		titleTypeDto.setTitleDescription("dsddsd");
		titleTypeDto.setTitleName("admin");
		titleTypeDto.setLangCode("eng");

		title.add(titleTypeDto);

		titleResponseDto.setTitle(title);

		Title titleIdType = mapperFacade.map(titleResponseDto, Title.class);
		assertEquals(titleResponseDto.getTitle().get(0).getTitleName(), titleIdType.getTitleName());
	}

	@Test
	public void testIdTypeDtoResponse() {

		IdTypeDto idTypeResponseDto = new IdTypeDto();

		idTypeResponseDto.setCode("1");
		idTypeResponseDto.setName("admin");
		;
		idTypeResponseDto.setDescription("admin");
		idTypeResponseDto.setLangCode("eng");

		IdType idType = mapperFacade.map(idTypeResponseDto, IdType.class);
		assertEquals(idTypeResponseDto.getLangCode(), idType.getLangCode());
	}

	@Test
	public void testDocumentCategoryDto() {

		DocumentCategoryDto titleResponseDto = new DocumentCategoryDto();
		titleResponseDto.setCode("1");
		titleResponseDto.setName("POA");
		titleResponseDto.setDescription("ajkskjska");
		titleResponseDto.setLangCode("eng");

		DocumentCategory documentCategoryType = mapperFacade.map(titleResponseDto, DocumentCategory.class);
		assertEquals(titleResponseDto.getLangCode(), documentCategoryType.getLangCode());
	}

	@Test
	public void testDocumentTypeDto() {

		DocumentTypeDto titleDocumentTypeDto = new DocumentTypeDto();
		titleDocumentTypeDto.setCode("1");
		titleDocumentTypeDto.setName("Passport");
		titleDocumentTypeDto.setDescription("ajkskjska");
		titleDocumentTypeDto.setLangCode("eng");

		DocumentCategory documentCategoryType = mapperFacade.map(titleDocumentTypeDto, DocumentCategory.class);
		assertEquals(titleDocumentTypeDto.getLangCode(), documentCategoryType.getLangCode());
	}

	@Test
	public void testLocationDto() {

		LocationDto locationDto = new LocationDto();
		locationDto.setCode("1");
		locationDto.setName("english");
		locationDto.setLangCode("eng");
		locationDto.setHierarchyLevel(1);
		locationDto.setHierarchyName("english");
		locationDto.setParentLocCode("english");

		Location locationType = mapperFacade.map(locationDto, Location.class);
		assertEquals(locationDto.getLangCode(), locationType.getLangCode());
	}

	@Test
	public void testOffsetDateTimeConversion() {
		OffsetDateTime time = OffsetDateTime.now();
		OffsetDateTime convertedTime = mapperFacade.map(time, OffsetDateTime.class);
		assertEquals(time, convertedTime);
	}

	@Test
	public void testDemographicInfoConversion() {
		assertDemographicInfo(mapperFacade.map(demographicInfoDTO, DemographicInfo.class));
	}

	@Test
	public void testDemographicConversion() {
		Demographic demographic = mapperFacade.map(demographicDTO, Demographic.class);
		assertDemographicInfo(demographic.getDemoInLocalLang());
		assertDemographicInfo(demographic.getDemoInUserLang());
	}

	private void assertDemographicInfo(DemographicInfo demographicInfo) {
		assertEquals(demographicInfoDTO.getDateOfBirth(), demographicInfo.getDateOfBirth());
		assertEquals(demographicInfoDTO.getGender(), demographicInfo.getGender());
		assertEquals(demographicInfoDTO.getAddressDTO().getAddressLine1(),
				demographicInfo.getAddressDTO().getAddressLine1());
		assertEquals(demographicInfoDTO.getAddressDTO().getAddressLine2(),
				demographicInfo.getAddressDTO().getAddressLine2());
		assertNull(demographicInfo.getAddressDTO().getAddressLine3());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getRegion(),
				demographicInfo.getAddressDTO().getLocationDTO().getRegion());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getProvince(),
				demographicInfo.getAddressDTO().getLocationDTO().getProvince());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getCity(),
				demographicInfo.getAddressDTO().getLocationDTO().getCity());
		assertEquals(demographicInfoDTO.getEmailId(), demographicInfo.getEmailId());
		assertEquals(demographicInfoDTO.getMobile(), demographicInfo.getMobile());
		assertEquals(demographicInfoDTO.isChild(), demographicInfo.isChild());
	}

}

package io.mosip.registration.mapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.Address;
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
import io.mosip.registration.entity.mastersync.DocumentType;
import io.mosip.registration.entity.mastersync.GenderType;
import io.mosip.registration.entity.mastersync.GenderTypeId;
import io.mosip.registration.entity.mastersync.IdType;
import io.mosip.registration.entity.mastersync.Language;
import io.mosip.registration.entity.mastersync.Location;
import io.mosip.registration.entity.mastersync.Title;
import io.mosip.registration.entity.mastersync.TitleId;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

/**
 * Class for Orika Object Mapper
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class CustomObjectMapper extends ConfigurableMapper {

	/**
	 * Instance of MapperFacade for object mapping
	 */
	public static final MapperFacade MAPPER_FACADE = new CustomObjectMapper();

	/**
	 * Method to configure the Orika Mapper for object conversions
	 * 
	 * @param mapperFactory the Orika MapperFactory
	 */
	@Override
	public void configure(MapperFactory mapperFactory) {

		String createdBy = SessionContext.getInstance().getUserContext().getUserId();
		boolean isActive = true;

		ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter(new PassThroughConverter(LocalDateTime.class));
		converterFactory.registerConverter(new PassThroughConverter(OffsetDateTime.class));
		converterFactory.registerConverter("packetMetaInfo", new PacketMetaInfoConverter());

		mapperFactory.classMap(DemographicInfoDTO.class, DemographicInfo.class).byDefault().register();

		mapperFactory.classMap(AddressDTO.class, Address.class).byDefault().register();

		mapperFactory.classMap(DemographicDTO.class, Demographic.class).exclude("applicantDocumentDTO")
				.exclude("introducerRID").exclude("introducerUIN").byDefault().register();

		mapperFactory.classMap(LanguageResponseDto.class, Language.class).byDefault()
				.customize(new CustomMapper<LanguageResponseDto, Language>() {

					@Override
					public void mapAtoB(LanguageResponseDto a, Language b, MappingContext context) {

						for (LanguageDto result : a.getLanguage()) {

							b.setLanguageCode(result.getLanguageCode());
							b.setLanguageFamily(result.getLanguageFamily());
							b.setLanguageName(result.getLanguageName());
							b.setNativeName(result.getNativeName());

						}

						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(BiometricTypeResponseDto.class, BiometricType.class).byDefault()
				.customize(new CustomMapper<BiometricTypeResponseDto, BiometricType>() {
					@Override
					public void mapAtoB(BiometricTypeResponseDto a, BiometricType b, MappingContext context) {

						for (BiometricTypeDto result : a.getBiometrictype()) {

							b.setCode(result.getCode());
							b.setDescription(result.getDescription());
							b.setLangCode(result.getLangCode());
							b.setName(result.getName());

						}

						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(BiometricAttributeResponseDto.class, BiometricAttribute.class).byDefault()
				.customize(new CustomMapper<BiometricAttributeResponseDto, BiometricAttribute>() {
					@Override
					public void mapAtoB(BiometricAttributeResponseDto a, BiometricAttribute b, MappingContext context) {

						for (BiometricAttributeDto result : a.getBiometricattribute()) {

							b.setCode(result.getCode());
							b.setDescription(result.getDescription());
							b.setLangCode(result.getLangCode());
							b.setName(result.getName());
							b.setBiometricTypeCode(result.getBiometricTypeCode());

						}

						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(BlacklistedWordsDto.class, BlacklistedWords.class).byDefault()
				.customize(new CustomMapper<BlacklistedWordsDto, BlacklistedWords>() {

					@Override
					public void mapAtoB(BlacklistedWordsDto a, BlacklistedWords b, MappingContext context) {
						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(GenderTypeResponseDto.class, GenderType.class).byDefault()
				.customize(new CustomMapper<GenderTypeResponseDto, GenderType>() {

					@Override
					public void mapAtoB(GenderTypeResponseDto a, GenderType b, MappingContext context) {

						GenderTypeId genderID = new GenderTypeId();

						for (GenderTypeDto result : a.getGender()) {

							genderID.setGenderCode(result.getGenderCode());
							genderID.setLangCode(result.getLangCode());

							b.setId(genderID);
							b.setGenderName(result.getGenderName());

						}

						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(TitleResponseDto.class, Title.class).byDefault()
				.customize(new CustomMapper<TitleResponseDto, Title>() {

					@Override
					public void mapAtoB(TitleResponseDto a, Title b, MappingContext context) {

						TitleId titleId = new TitleId();

						for (TitleDto result : a.getTitle()) {

							titleId.setTitleCode(result.getTitleCode());
							titleId.setLangCode(result.getLangCode());

							b.setId(titleId);
							b.setTitleDescription(result.getTitleDescription());
							b.setTitleName(result.getTitleName());

						}

						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(IdTypeDto.class, IdType.class).byDefault()
				.customize(new CustomMapper<IdTypeDto, IdType>() {

					@Override
					public void mapAtoB(IdTypeDto a, IdType b, MappingContext context) {
						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(DocumentCategoryDto.class, DocumentCategory.class).byDefault()
				.customize(new CustomMapper<DocumentCategoryDto, DocumentCategory>() {

					@Override
					public void mapAtoB(DocumentCategoryDto a, DocumentCategory b, MappingContext context) {
						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(DocumentTypeDto.class, DocumentType.class).byDefault()
				.customize(new CustomMapper<DocumentTypeDto, DocumentType>() {

					@Override
					public void mapAtoB(DocumentTypeDto a, DocumentType b, MappingContext context) {
						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

		mapperFactory.classMap(LocationDto.class, Location.class).byDefault()
				.customize(new CustomMapper<LocationDto, Location>() {

					@Override
					public void mapAtoB(LocationDto a, Location b, MappingContext context) {
						b.setIsActive(isActive);
						b.setCrBy(createdBy);
						b.setCrDtime(new Timestamp(System.currentTimeMillis()));
					}
				}).register();

	}

}

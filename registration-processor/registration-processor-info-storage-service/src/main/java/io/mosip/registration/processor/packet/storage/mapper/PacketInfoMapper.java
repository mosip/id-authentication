package io.mosip.registration.processor.packet.storage.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.packet.storage.entity.AbisApplicationEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponsePKEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefPkEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegDemoDedupeListEntity;
import io.mosip.registration.processor.packet.storage.entity.RegDemoDedupeListPKEntity;
import io.mosip.registration.processor.packet.storage.exception.DateParseException;

/**
 * The Class PacketInfoMapper.
 */
public class PacketInfoMapper {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketInfoMapper.class);

	/** The Constant REGISTRATION_ID. */
	private static final String REGISTRATION_ID = "registrationId";

	/** The Constant PRE_REGISTRATION_ID. */
	private static final String PRE_REGISTRATION_ID = "preRegistrationId";

	/**
	 * Instantiates a new packet info mapper.
	 */
	private PacketInfoMapper() {
		super();
	}

	/**
	 * Convert reg abis ref to entity.
	 *
	 * @param regAbisRefDto
	 *            the reg abis ref dto
	 * @return the reg abis ref entity
	 */
	public static RegAbisRefEntity convertRegAbisRefToEntity(RegAbisRefDto regAbisRefDto) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regAbisRefDto.getReg_id(), "PacketInfoMapper::convertRegAbisRefToEntity()::entry");

		RegAbisRefEntity regAbisRefEntity = new RegAbisRefEntity();

		RegAbisRefPkEntity regAbisRefPkEntity = new RegAbisRefPkEntity();

		regAbisRefPkEntity.setRegId(regAbisRefDto.getReg_id());
		regAbisRefEntity.setAbisRefId(regAbisRefDto.getAbis_ref_id());
		regAbisRefEntity.setId(regAbisRefPkEntity);
		regAbisRefEntity.setIsActive(true);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regAbisRefDto.getReg_id(), "PacketInfoMapper::convertRegAbisRefToEntity()::exit");
		return regAbisRefEntity;
	}

	/**
	 * Gets the json values.
	 *
	 * @param jsonNode
	 *            the json node
	 * @param language
	 *            the language
	 * @return the json values
	 */
	private static String getJsonValues(JsonValue[] jsonNode, String language) {
		String value = null;
		if (jsonNode != null) {
			for (int i = 0; i < jsonNode.length; i++) {
				if (jsonNode[i].getLanguage().equals(language)) {
					value = jsonNode[i].getValue();
				}
			}
		}

		return value;
	}

	/**
	 * Gets the languages.
	 *
	 * @param jsonNode
	 *            the json node
	 * @return the languages
	 */
	private static String[] getLanguages(JsonValue[] jsonNode,StringBuilder languages) {
		if (jsonNode != null) {
			for (int i = 0; i < jsonNode.length; i++) {
				if (!(languages.toString().contains(jsonNode[i].getLanguage())))
					languages = languages.append(jsonNode[i].getLanguage()).append(",");

			}
		}

		return languages.toString().split(",");
	}

	/**
	 * Conver demographic dedupe dto to entity.
	 *
	 * @param demoDto
	 *            the demo dto
	 * @param regId
	 *            the reg id
	 * @return the list
	 */
	public static List<IndividualDemographicDedupeEntity> converDemographicDedupeDtoToEntity(
			IndividualDemographicDedupe demoDto, String regId) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
				"PacketInfoMapper::converDemographicDedupeDtoToEntity()::entry");
		IndividualDemographicDedupeEntity entity;
		IndividualDemographicDedupePKEntity applicantDemographicPKEntity;
		List<IndividualDemographicDedupeEntity> demogrphicDedupeEntities = new ArrayList<>();
		StringBuilder languages = new StringBuilder();
		if (demoDto.getName() != null) {
			getLanguages(demoDto.getName(),languages);
		}
		String[] languageArray = getLanguages(demoDto.getGender(),languages);
		for (int i = 0; i < languageArray.length; i++) {
			entity = new IndividualDemographicDedupeEntity();
			applicantDemographicPKEntity = new IndividualDemographicDedupePKEntity();

			applicantDemographicPKEntity.setRegId(regId);
			applicantDemographicPKEntity.setLangCode(languageArray[i]);

			entity.setId(applicantDemographicPKEntity);
			entity.setIsActive(true);
			entity.setIsDeleted(false);
			String applicantName = null;
			if (demoDto.getName() != null) {
				applicantName = getJsonValues(demoDto.getName(), languageArray[i]);
				entity.setName(applicantName!=null?getHMACHashCode(applicantName.trim().toUpperCase()):null);
			}

			if (demoDto.getDateOfBirth() != null) {
				try {
					Date date = new SimpleDateFormat("yyyy/MM/dd").parse(demoDto.getDateOfBirth());

					entity.setDob(getHMACHashCode(demoDto.getDateOfBirth()));
				} catch (ParseException e) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), regId,
							e.getMessage() + ExceptionUtils.getStackTrace(e));
					throw new DateParseException(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getMessage(), e);
				}
			}
			entity.setGender(getHMACHashCode(getJsonValues(demoDto.getGender(), languageArray[i])));
			demogrphicDedupeEntities.add(entity);

		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
				"PacketInfoMapper::converDemographicDedupeDtoToEntity()::exit");
		return demogrphicDedupeEntities;
	}

	public static String getHMACHashCode(String value) {
		if(value==null) return null;
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(value.getBytes()));

	}

	public static List<AbisRequestDto> convertAbisRequestEntityListToDto(
			List<AbisRequestEntity> abisRequestEntityList) {
		List<AbisRequestDto> abisRequestDtoList = new ArrayList<>();
		if (abisRequestEntityList == null)
			return abisRequestDtoList;
		if (abisRequestEntityList.isEmpty())
			return abisRequestDtoList;
		for (AbisRequestEntity entity : abisRequestEntityList) {
			abisRequestDtoList.add(convertAbisRequestEntityToDto(entity));
		}
		return abisRequestDtoList;
	}

	public static AbisRequestDto convertAbisRequestEntityToDto(AbisRequestEntity entity) {
		AbisRequestDto abisReqDto = new AbisRequestDto();
		abisReqDto.setId(entity.getId().getId());
		abisReqDto.setAbisAppCode(entity.getAbisAppCode());
		abisReqDto.setBioRefId(entity.getBioRefId());
		abisReqDto.setIsDeleted(entity.getIsDeleted());
		abisReqDto.setCrBy(entity.getCrBy());

		abisReqDto.setIsDeleted(entity.getIsDeleted());
		abisReqDto.setLangCode(entity.getLangCode());
		abisReqDto.setRefRegtrnId(entity.getRefRegtrnId());

		abisReqDto.setReqBatchId(entity.getReqBatchId());
		abisReqDto.setReqText(entity.getReqText());
		abisReqDto.setRequestDtimes(entity.getRequestDtimes());
		abisReqDto.setRequestType(entity.getRequestType());

		abisReqDto.setStatusCode(entity.getStatusCode());
		abisReqDto.setStatusComment(entity.getStatusComment());

		return abisReqDto;

	}

	public static List<RegBioRefDto> convertRegBioRefEntityListToDto(List<RegBioRefEntity> regBioRefEntityList) {
		List<RegBioRefDto> regBioRefDtoList = new ArrayList<>();
		for (RegBioRefEntity entity : regBioRefEntityList) {
			regBioRefDtoList.add(convertBioRefEntityToDto(entity));
		}
		return regBioRefDtoList;
	}

	public static RegBioRefDto convertBioRefEntityToDto(RegBioRefEntity regBioRefEntity) {
		RegBioRefDto bioRefDto = new RegBioRefDto();
		bioRefDto.setBioRefId(regBioRefEntity.getBioRefId());
		bioRefDto.setCrBy(regBioRefEntity.getCrBy());
		bioRefDto.setIsActive(regBioRefEntity.getIsActive());
		bioRefDto.setIsDeleted(regBioRefEntity.getIsDeleted());
		bioRefDto.setRegId(regBioRefEntity.getId().getRegId());
		bioRefDto.setUpdBy(regBioRefEntity.getUpdBy());
		return bioRefDto;
	}

	public static RegBioRefEntity convertBioRefDtoToEntity(RegBioRefDto regBioRefDto) {
		RegBioRefEntity entity = new RegBioRefEntity();
		entity.setBioRefId(regBioRefDto.getBioRefId());
		entity.setCrBy(regBioRefDto.getCrBy());
		entity.setIsActive(regBioRefDto.getIsActive());
		entity.setUpdBy(regBioRefDto.getUpdBy());
		RegBioRefPKEntity refPKEntity = new RegBioRefPKEntity();
		refPKEntity.setRegId(regBioRefDto.getRegId());
		entity.setId(refPKEntity);

		return entity;
	}

	public static List<AbisApplicationDto> convertAbisApplicationEntityListToDto(
			List<AbisApplicationEntity> abisApplicationEntityList) {
		List<AbisApplicationDto> abisApplicationDtos = new ArrayList<>();
		for (AbisApplicationEntity entity : abisApplicationEntityList) {
			abisApplicationDtos.add(convertAbisApplicationEntityToDto(entity));
		}
		return abisApplicationDtos;
	}

	private static AbisApplicationDto convertAbisApplicationEntityToDto(AbisApplicationEntity entity) {
		AbisApplicationDto applicationDto = new AbisApplicationDto();
		applicationDto.setCode(entity.getId().getCode());
		applicationDto.setCrBy(entity.getCrBy());
		applicationDto.setDescr(entity.getDescr());
		applicationDto.setIsDeleted(entity.getIsDeleted());
		applicationDto.setLangCode(entity.getId().getLangCode());
		applicationDto.setName(entity.getName());
		applicationDto.setStatusCode(entity.getStatusCode());
		applicationDto.setStatusUpdateDtimes(entity.getStatusUpdateDtimes());
		applicationDto.setUpdBy(entity.getUpdBy());

		return applicationDto;
	}

	public static AbisRequestEntity convertAbisRequestDtoToEntity(AbisRequestDto abisRequestDto) {
		AbisRequestEntity entity = new AbisRequestEntity();
		entity.setAbisAppCode(abisRequestDto.getAbisAppCode());
		entity.setBioRefId(abisRequestDto.getBioRefId());
		entity.setCrBy(abisRequestDto.getCrBy());
		AbisRequestPKEntity abisRequestPKEntity = new AbisRequestPKEntity();
		abisRequestPKEntity.setId(abisRequestDto.getId());
		entity.setId(abisRequestPKEntity);
		entity.setIsDeleted(abisRequestDto.getIsDeleted());
		entity.setLangCode(abisRequestDto.getLangCode());
		entity.setRefRegtrnId(abisRequestDto.getRefRegtrnId());
		entity.setReqBatchId(abisRequestDto.getReqBatchId());
		entity.setReqText(abisRequestDto.getReqText());
		entity.setRequestType(abisRequestDto.getRequestType());
		entity.setStatusCode(abisRequestDto.getStatusCode());
		entity.setStatusComment(abisRequestDto.getStatusComment());
		entity.setUpdBy(abisRequestDto.getUpdBy());

		return entity;
	}

	public static List<RegDemoDedupeListDto> convertDemoDedupeEntityListToDto(
			List<RegDemoDedupeListEntity> regDemoDedupeListEntityList) {
		List<RegDemoDedupeListDto> regDemoDedupeListDtoList = new ArrayList<>();
		for (RegDemoDedupeListEntity entity : regDemoDedupeListEntityList) {
			regDemoDedupeListDtoList.add(convertDemoDedupeEntityToDto(entity));
		}
		return regDemoDedupeListDtoList;
	}

	private static RegDemoDedupeListDto convertDemoDedupeEntityToDto(RegDemoDedupeListEntity entity) {
		RegDemoDedupeListDto regDemoDedupeListDto = new RegDemoDedupeListDto();
		regDemoDedupeListDto.setCrBy(entity.getCrBy());
		regDemoDedupeListDto.setIsDeleted(entity.getIsDeleted());
		regDemoDedupeListDto.setMatchedRegId(entity.getId().getMatchedRegId());
		regDemoDedupeListDto.setRegId(entity.getRegId());

		regDemoDedupeListDto.setRegtrnId(entity.getId().getRegtrnId());
		regDemoDedupeListDto.setUpdBy(entity.getUpdBy());

		return regDemoDedupeListDto;
	}

	public static RegDemoDedupeListEntity convertDemoDedupeEntityToDto(RegDemoDedupeListDto regDemoDedupeListDto) {
		RegDemoDedupeListEntity entity = new RegDemoDedupeListEntity();
		RegDemoDedupeListPKEntity pkEntity = new RegDemoDedupeListPKEntity();

		entity.setCrBy(regDemoDedupeListDto.getCrBy());
		entity.setCrDtimes(regDemoDedupeListDto.getCrDtimes());
		entity.setDelDtimes(regDemoDedupeListDto.getDelDtimes());
		entity.setIsDeleted(regDemoDedupeListDto.getIsDeleted());
		entity.setRegId(regDemoDedupeListDto.getRegId());
		entity.setUpdBy(regDemoDedupeListDto.getUpdBy());
		entity.setUpdDtimes(regDemoDedupeListDto.getUpdDtimes());

		pkEntity.setMatchedRegId(regDemoDedupeListDto.getMatchedRegId());
		pkEntity.setRegtrnId(regDemoDedupeListDto.getRegtrnId());
		entity.setId(pkEntity);

		return entity;
	}
	
	public static AbisResponseDto convertAbisResponseEntityToDto(AbisResponseEntity entity) {

		AbisResponseDto abisResDto = new AbisResponseDto();
		abisResDto.setId(entity.getId().getId());
		abisResDto.setCrBy(entity.getCrBy());
		abisResDto.setCrDtimes(entity.getCrDtimes());
		abisResDto.setDelDtimes(entity.getDelDtimes());
		abisResDto.setIsDeleted(entity.getIsDeleted());
		abisResDto.setLangCode(entity.getLangCode());
		abisResDto.setRespDtimes(entity.getRespDtimes());
		abisResDto.setRespText(entity.getRespText());
		abisResDto.setStatusCode(entity.getStatusCode());
		abisResDto.setStatusComment(entity.getStatusComment());
		abisResDto.setUpdBy(entity.getUpdBy());
		abisResDto.setUpdDtimes(entity.getUpdDtimes());
		abisResDto.setAbisRequest(entity.getAbisRequest());

		return abisResDto;

	}

	public static List<AbisResponseDto> convertAbisResponseEntityListToDto(
			List<AbisResponseEntity> abisResponseEntityList) {
		List<AbisResponseDto> abisResponseDtoList = new ArrayList<>();
		if (abisResponseEntityList == null)
			return abisResponseDtoList;
		if (abisResponseEntityList.isEmpty())
			return abisResponseDtoList;
		for (AbisResponseEntity entity : abisResponseEntityList) {
			abisResponseDtoList.add(convertAbisResponseEntityToDto(entity));
		}
		return abisResponseDtoList;
	}

	public static AbisResponseEntity convertAbisResponseEntityToDto(AbisResponseDto abisResDto) {

		AbisResponseEntity entity = new AbisResponseEntity();
		AbisResponsePKEntity resPkEntity = new AbisResponsePKEntity();
		resPkEntity.setId(abisResDto.getId());

		entity.setId(resPkEntity);
		entity.setCrBy(abisResDto.getCrBy());
		entity.setCrDtimes(abisResDto.getCrDtimes());
		entity.setDelDtimes(abisResDto.getDelDtimes());
		entity.setIsDeleted(abisResDto.getIsDeleted());
		entity.setLangCode(abisResDto.getLangCode());
		entity.setRespDtimes(abisResDto.getRespDtimes());
		entity.setRespText(abisResDto.getRespText());
		entity.setStatusCode(abisResDto.getStatusCode());
		entity.setStatusComment(abisResDto.getStatusComment());
		entity.setUpdBy(abisResDto.getUpdBy());
		entity.setUpdDtimes(abisResDto.getUpdDtimes());
		entity.setAbisRequest(abisResDto.getAbisRequest());

		return entity;

	}

	public static List<AbisResponseEntity> convertAbisResponseDtoToEntity(List<AbisResponseDto> abisResponsesDtoList) {
		List<AbisResponseEntity> abisResponseEntityList = new ArrayList<>();
		if (abisResponsesDtoList == null)
			return abisResponseEntityList;
		if (abisResponsesDtoList.isEmpty())
			return abisResponseEntityList;
		for (AbisResponseDto responsDto : abisResponsesDtoList) {
			abisResponseEntityList.add(convertAbisResponseEntityToDto(responsDto));
		}
		return abisResponseEntityList;
	}
	
	public static AbisResponseDetDto convertAbisResponseDetEntityToDto(AbisResponseDetEntity entity) {

		AbisResponseDetDto abisResDetDto = new AbisResponseDetDto();
		abisResDetDto.setAbiRespId(entity.getId().getAbisRespId());
		abisResDetDto.setMatchedBioRefId(entity.getId().getMatchedBioRefId());
		abisResDetDto.setCrBy(entity.getCrBy());
		abisResDetDto.setCrDtimes(entity.getCrDtimes());
		abisResDetDto.setDelDtimes(entity.getDelDtimes());
		abisResDetDto.setIsDeleted(entity.getIsDeleted());
		abisResDetDto.setScore(entity.getScore());
		abisResDetDto.setUpdBy(entity.getUpdBy());
		abisResDetDto.setUpdDtimes(entity.getUpdDtimes());
		return abisResDetDto;

	}
	
	public static AbisResponseDetEntity  convertAbisResponseDetEntityToDto(AbisResponseDetDto abisResponseDetDto ) {

		AbisResponseDetEntity entity = new AbisResponseDetEntity();
		AbisResponseDetPKEntity entityPk = new AbisResponseDetPKEntity();
		
		entityPk.setAbisRespId(abisResponseDetDto.getAbiRespId());
		entityPk.setMatchedBioRefId(abisResponseDetDto.getMatchedBioRefId());
		
		entity.setId(entityPk);
		entity.setCrBy(abisResponseDetDto.getCrBy());
		entity.setCrDtimes(abisResponseDetDto.getCrDtimes());
		entity.setDelDtimes(abisResponseDetDto.getDelDtimes());
		entity.setIsDeleted(abisResponseDetDto.getIsDeleted());
		entity.setScore(abisResponseDetDto.getScore());
		entity.setUpdBy(abisResponseDetDto.getUpdBy());
		entity.setUpdDtimes(abisResponseDetDto.getUpdDtimes());
		return entity;

	}

	public static List<AbisResponseDetDto> convertAbisResponseDetEntityListToDto(
			List<AbisResponseDetEntity> abisResponseDetEntity) {
		List<AbisResponseDetDto> abisResponseDetDtoList = new ArrayList<>();
		if (abisResponseDetEntity == null)
			return abisResponseDetDtoList;
		if (abisResponseDetEntity.isEmpty())
			return abisResponseDetDtoList;
		for (AbisResponseDetEntity abisResDetEntity : abisResponseDetEntity) {
			abisResponseDetDtoList.add(convertAbisResponseDetEntityToDto(abisResDetEntity));
		}
		return abisResponseDetDtoList;
	}

}

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
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefPkEntity;
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

	/** The languages. */
	private static StringBuilder languages = new StringBuilder();

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
	private static String[] getLanguages(JsonValue[] jsonNode) {
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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regId, "PacketInfoMapper::converDemographicDedupeDtoToEntity()::entry");
		IndividualDemographicDedupeEntity entity;
		IndividualDemographicDedupePKEntity applicantDemographicPKEntity;
		List<IndividualDemographicDedupeEntity> demogrphicDedupeEntities = new ArrayList<>();
		if (demoDto.getName() != null) {
			getLanguages(demoDto.getName());
		}
		String[] languageArray = getLanguages(demoDto.getGender());
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
			entity.setName(getHMACHashCode(applicantName.trim().toUpperCase()));
			}

			if (demoDto.getDateOfBirth() != null) {
				try {
					Date date = new SimpleDateFormat("yyyy/MM/dd").parse(demoDto.getDateOfBirth());

					entity.setDob(getHMACHashCode(demoDto.getDateOfBirth()));
				} catch (ParseException e) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
							regId, e.getMessage() + ExceptionUtils.getStackTrace(e));
					throw new DateParseException(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getMessage(), e);
				}
			}
			entity.setGender(getHMACHashCode(getJsonValues(demoDto.getGender(), languageArray[i])));
			demogrphicDedupeEntities.add(entity);

		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regId, "PacketInfoMapper::converDemographicDedupeDtoToEntity()::exit");
		return demogrphicDedupeEntities;
	}
	

	public static String getHMACHashCode(String value) {
		return  CryptoUtil.encodeBase64(HMACUtils.generateHash(value.getBytes()));
		
	}
	
	public static List<AbisRequestDto> convertAbisRequestEntityListToDto(List<AbisRequestEntity> abisRequestEntityList) {
		List<AbisRequestDto> abisRequestDtoList = new ArrayList<>();
		for(AbisRequestEntity entity : abisRequestEntityList) {
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
}

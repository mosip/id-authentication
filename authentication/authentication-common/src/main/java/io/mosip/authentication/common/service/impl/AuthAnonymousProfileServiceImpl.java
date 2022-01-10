package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIOMETRICS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_SUB_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_DOB_PATTERN;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DIGITAL_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.FAILURE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.QUALITY_SCORE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SUCCESS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_DATE_OF_BIRTH_PATTERN;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AnonymousProfileEntity;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.idevent.AnonymousAuthenticationProfile;
import io.mosip.authentication.common.service.impl.idevent.BiometricProfileInfo;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.repository.AuthAnonymousProfileRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.profile.AuthAnonymousProfileService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Loganathan Sekar
 *
 */
@Service
@Lazy
public class AuthAnonymousProfileServiceImpl implements AuthAnonymousProfileService {
	

	private Logger logger = IdaLogger.getLogger(AuthAnonymousProfileServiceImpl.class);
	
	@Autowired
	private IdInfoHelper idInfoHelper;
	
	@Autowired
	private AuthAnonymousEventPublisher authAnonymousEventPublisher;
	
	@Autowired
	private AuthAnonymousProfileRepository authAnonymousProfileRepository;
	
	@Value("${" + IdAuthConfigKeyConstants.DATE_TIME_PATTERN + "}")
	private String dateTimePattern;
	
	@Value("${" + IdAuthConfigKeyConstants.PREFERRED_LANG_ATTRIB_NAME + "}")
	private String preferredLangAttribName;
	
	@Value("${" + IdAuthConfigKeyConstants.LOCATION_PROFILE_ATTRIB_NAME + "}")
	private String locationProfileAttribName;
	
	@Value("${" + MOSIP_DATE_OF_BIRTH_PATTERN + ":" + DEFAULT_DOB_PATTERN + "}")
	private String dateOfBirthPattern;
	
	@Autowired
	private ObjectMapper mapper;
	

	@Override
	public void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> requestMetadata,
			Map<String, Object> responseMetadata, boolean status, List<AuthError> errors) {
		AnonymousAuthenticationProfile ananymousProfile = createAnonymousProfile(requestBody, requestMetadata, responseMetadata, status, errors);
		storeAnonymousProfile(ananymousProfile);
		authAnonymousEventPublisher.publishEvent(ananymousProfile);
	}

	private void storeAnonymousProfile(AnonymousAuthenticationProfile ananymousProfile) {
		AnonymousProfileEntity authAnonymousProfileEntity = new AnonymousProfileEntity();
		String id = UUID.randomUUID().toString();
		authAnonymousProfileEntity.setId(id);
		authAnonymousProfileEntity.setCrBy(IDA);
		LocalDateTime crDTimes = DateUtils.getUTCCurrentDateTime();
		authAnonymousProfileEntity.setCrDTimes(crDTimes);
		
		try {
			authAnonymousProfileEntity.setProfile(mapper.writeValueAsString(ananymousProfile));
		} catch (JsonProcessingException e) {
			logger.error("Error saving anonymous profile. %s", ExceptionUtils.getStackTrace(e));
		}
		
		authAnonymousProfileRepository.save(authAnonymousProfileEntity);
		authAnonymousProfileRepository.flush();
	}

	private AnonymousAuthenticationProfile createAnonymousProfile(Map<String, Object> requestBody,
			Map<String, Object> requestMetadata, Map<String, Object> responseMetadata, boolean status,
			List<AuthError> errorCodes) {
		AnonymousAuthenticationProfile ananymousProfile = new AnonymousAuthenticationProfile();
		
		Map<String, List<IdentityInfoDTO>> idInfo = getMapOfIdentityInfoDTOList(responseMetadata);

		if(idInfo != null && !idInfo.isEmpty()) {
			setYearOfBirth(ananymousProfile, idInfo);
			
			String preferredLang = idInfoHelper.getDynamicEntityInfoAsString(idInfo, null, preferredLangAttribName);
			if(preferredLang != null) {
				ananymousProfile.setPreferredLanguages(List.of(preferredLang));
			}
			
			String langCode = getProfileDataLangCode(idInfo, preferredLang);
			if(langCode != null) {
				setGender(ananymousProfile, idInfo, langCode);
				
				try {
					Map<String, String> locationInfo = idInfoHelper.getIdEntityInfoMap(DemoMatchType.DYNAMIC, idInfo, langCode, locationProfileAttribName);
					ananymousProfile.setLocation(new ArrayList<>(locationInfo.values()));
				} catch (IdAuthenticationBusinessException e) {
					logger.error("Error fetching %s for anonymous profile: %s", locationProfileAttribName, ExceptionUtils.getStackTrace(e));
				}
			}
			
			List<BiometricProfileInfo> biometricInfos = getBiometricInfos(requestBody);
			ananymousProfile.setBiometricInfo(biometricInfos);

		}
		
		setAuthFactors(responseMetadata, ananymousProfile);
		
		setDate(ananymousProfile);
		
		setErrorCodes(errorCodes, ananymousProfile);
		
		setPartnerName(requestMetadata, ananymousProfile);

		setStatus(status, ananymousProfile);
		
		return ananymousProfile;
	}

	@SuppressWarnings("unchecked")
	private List<BiometricProfileInfo> getBiometricInfos(Map<String, Object> requestBody) {
		Object requestObj = requestBody.get(REQUEST);
		if(requestObj instanceof Map) {
			Object biometricsObj = ((Map<String, Object>) requestObj).get(BIOMETRICS);
			if(biometricsObj instanceof List) {
				return ((List<Object>) biometricsObj).stream()
												.filter(obj -> obj instanceof Map)
												.map(obj -> ((Map<String, Object>)obj).get("data"))
												.filter(obj -> obj instanceof Map)
												.map(obj -> (Map<String, Object>)obj)
												.map(map -> getBiometricProfileInfo(map))
												.collect(Collectors.toList());
				
			}
			
		}
		return List.of();
	}

	private BiometricProfileInfo getBiometricProfileInfo(Map<String, Object> bioDataMap) {
		BiometricProfileInfo biometricProfileInfo = new BiometricProfileInfo();
		Object bioTypeObj = bioDataMap.get(BIO_TYPE);
		if(bioTypeObj instanceof String) {
			biometricProfileInfo.setType((String) bioTypeObj);
		}
		
		Object bioSubType = bioDataMap.get(BIO_SUB_TYPE);
		if(bioSubType instanceof String) {
			biometricProfileInfo.setSubtype((String) bioSubType);
		}
		
		Object digitalIdObj = bioDataMap.get(DIGITAL_ID);
		if(digitalIdObj instanceof Map) {
			try {
				biometricProfileInfo.setDigitalId(mapper.writeValueAsString(digitalIdObj));
			} catch (JsonProcessingException e) {
				logger.error("Error fetching %s for anonymous profile: %s", DIGITAL_ID, ExceptionUtils.getStackTrace(e));
			}
		}
		
		
		Object qualityScoreObj = bioDataMap.get(QUALITY_SCORE);
		if(qualityScoreObj instanceof Object) {
			biometricProfileInfo.setQualityScore(String.valueOf(qualityScoreObj));
		}
		
		return biometricProfileInfo;
	}

	private void setYearOfBirth(AnonymousAuthenticationProfile ananymousProfile, Map<String, List<IdentityInfoDTO>> idInfo) {
		// Year of Birth is Mandatory, rest are optional and will be set if fetched as
		// part of authentication / kyc request processing
		getEntityInfoString(DemoMatchType.DOB, idInfo).ifPresent(dob -> ananymousProfile.setYearOfBirth(getYear(dob)));
	}

	private String getYear(String dob) {
		Date date = DateUtils.parseToDate(dob, dateOfBirthPattern);
		return String.valueOf(DateUtils.parseDateToLocalDateTime(date).getYear());
	}

	private void setGender(AnonymousAuthenticationProfile ananymousProfile, Map<String, List<IdentityInfoDTO>> idInfo,
			String langCode) {
		Optional<String> genderInfo = getEntityInfoString(DemoMatchType.GENDER, idInfo,langCode);
		if(genderInfo.isEmpty()) {
			genderInfo = getEntityInfoString(DemoMatchType.GENDER, idInfo);
		}
		genderInfo.ifPresent(ananymousProfile::setGender);
	}

	private String getProfileDataLangCode(Map<String, List<IdentityInfoDTO>> idInfo, String preferredLang) {
		Optional<String> mandatoryLang = Arrays.stream(EnvUtil.getMandatoryLanguages().split(","))
				.filter(str -> str.trim().length() > 0)
				.findFirst();
		return mandatoryLang.orElse(preferredLang);
	}

	private Optional<String> getEntityInfoString(MatchType matchType, Map<String, List<IdentityInfoDTO>> idInfo) {
		return getEntityInfoString(matchType, idInfo, null);
	}
	
	private Optional<String> getEntityInfoString(MatchType matchType, Map<String, List<IdentityInfoDTO>> idInfo, String langCode) {
		if(langCode == null) {
			try {
				String entityInfoAsString = idInfoHelper.getEntityInfoAsString(matchType, idInfo);
				if(entityInfoAsString !=null && !entityInfoAsString.isEmpty()) {
					return Optional.of(entityInfoAsString);
				}
			} catch (IdAuthenticationBusinessException e) {
				logger.error("Error fetching %s for anonymous profile: %s", matchType.getIdMapping().getIdname(), ExceptionUtils.getStackTrace(e));
			}
		} else {
			try {
				String entityInfoAsString = idInfoHelper.getEntityInfoAsString(matchType, langCode, idInfo);
				if(entityInfoAsString !=null && !entityInfoAsString.isEmpty()) {
					return Optional.of(entityInfoAsString);
				}
			} catch (IdAuthenticationBusinessException e) {
				logger.error("Error fetching %s for anonymous profile: %s", matchType.getIdMapping().getIdname(), ExceptionUtils.getStackTrace(e));
			}
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<IdentityInfoDTO>> getMapOfIdentityInfoDTOList(Map<String, Object> responseMetadata) {
		if(responseMetadata != null) {
			Map<String, Object> mapOfObject = (Map<String, Object>) responseMetadata.get(IdAuthCommonConstants.IDENTITY_INFO);
			if(mapOfObject != null) {
				return mapOfObject.entrySet()
								.stream()
								.filter(entry -> entry.getValue() instanceof List)
								.collect(Collectors.toMap(Entry::getKey, 
										entry -> ((List<Object>)entry.getValue())
														.stream()
														.map(elem -> mapper.convertValue(elem, IdentityInfoDTO.class))
														.collect(Collectors.toList())));
			}
		}
		
		return Map.of();
	}

	private void setStatus(boolean status, AnonymousAuthenticationProfile ananymousProfile) {
		ananymousProfile.setStatus(status ? SUCCESS : FAILURE);
	}

	private void setDate(AnonymousAuthenticationProfile ananymousProfile) {
		LocalDateTime requestDateTime = DateUtils.getUTCCurrentDateTime();
		ananymousProfile.setDate(requestDateTime.toLocalDate().toString());
	}

	private void setAuthFactors(Map<String, Object> responseMetadata, AnonymousAuthenticationProfile ananymousProfile) {
		if(responseMetadata != null) {
			Object authTxnObj = responseMetadata.get(AutnTxn.class.getSimpleName());
			if(authTxnObj != null) {
				AutnTxn autnTxn = mapper.convertValue(authTxnObj, AutnTxn.class);
				List<String> authFactors = Arrays.stream(autnTxn.getAuthTypeCode().split(","))
											.distinct()
											.collect(Collectors.toList());
				ananymousProfile.setAuthFactors(authFactors);
			}
		}
	}

	private void setErrorCodes(List<AuthError> errors, AnonymousAuthenticationProfile ananymousProfile) {
		if(errors != null && errors.size() > 0) {
			List<String> errorCodes = errors.stream()
					.map(AuthError::getErrorCode)
					.collect(Collectors.toList());
			ananymousProfile.setErrorCode(errorCodes);
		}
	}

	private void setPartnerName(Map<String, Object> requestMetadata, AnonymousAuthenticationProfile ananymousProfile) {
		if(requestMetadata != null) {
			Object partnerIdObj = requestMetadata.get("partnerId");
			if(partnerIdObj instanceof String) {
				String partnerId = (String) partnerIdObj;
				Object partnerObj = requestMetadata.get(partnerId);
				if(partnerObj instanceof PartnerDTO) {
					String partnerNameObj = ((PartnerDTO)partnerObj).getPartnerName();
					ananymousProfile.setPartnerName(partnerNameObj);
				}
			}
		}
	}

}

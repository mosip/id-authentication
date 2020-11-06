package io.mosip.authentication.kyc.service.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The implementation of Kyc Authentication service which retrieves the identity
 * information of the individual id and construct the KYC information
 * 
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService {
	
	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(KycServiceImpl.class);	

	/** The env. */
	@Autowired
	Environment env;

	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/** The mapping config. */
	@Autowired
	private MappingConfig mappingConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.service.KycService#retrieveKycInfo(
	 * java.lang.String, java.util.List, java.lang.String, java.util.Map)
	 */
	@Override
	public KycResponseDTO retrieveKycInfo(List<String> allowedkycAttributes, String secLangCode,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException {
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		if (Objects.nonNull(identityInfo)) {
			Map<String, String> faceEntityInfoMap = idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, identityInfo,
					null);
			List<IdentityInfoDTO> bioValue = null;
			String face = Objects.nonNull(faceEntityInfoMap) ? faceEntityInfoMap.get(CbeffDocType.FACE.getType().value()) : null;
			if (Objects.nonNull(faceEntityInfoMap)) {
				bioValue = new ArrayList<>();
				IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
				identityInfoDTO.setValue(face);
				bioValue.add(identityInfoDTO);
				identityInfo.put(IdAuthCommonConstants.PHOTO, bioValue);
			}
			Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = filterIdentityInfo(allowedkycAttributes, identityInfo,
					secLangCode);
			if (Objects.nonNull(filteredIdentityInfo)) {
				setKycInfo(allowedkycAttributes, kycResponseDTO, bioValue, face, filteredIdentityInfo);
			}
		}
		return kycResponseDTO;
	}

	/**
	 * Set KYC info based on the ID Names (from ID Mapping). FACE is also included if required.
	 *
	 * @param allowedkycAttributes the allowedkyc attributes
	 * @param kycResponseDTO the kyc response DTO
	 * @param bioValue the bio value
	 * @param face the face
	 * @param filteredIdentityInfo the filtered identity info
	 */
	private void setKycInfo(List<String> allowedkycAttributes, KycResponseDTO kycResponseDTO,
			List<IdentityInfoDTO> bioValue, String face, Map<String, List<IdentityInfoDTO>> filteredIdentityInfo) {
		// Getting allowed demographic data - key will be the ID Name (from ID Mapping),
		// value will be the ID Entity value from ID Name
		Map<String, Object> idMappingIdentityInfo = Stream.of(DemoMatchType.values())
				.map(demoMatchType -> new SimpleEntry<>(demoMatchType.getIdMapping().getIdname(),
						getEntityForMatchType(demoMatchType, filteredIdentityInfo)))
				.filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		//Set null value to the idnames which don't have entry in filtered Identity Info map
		for (String kycAttribute : allowedkycAttributes) {
			String idname = IdaIdMapping.getIdNameForMapping(kycAttribute, mappingConfig).orElse("");
			if (!idname.isEmpty() && !idMappingIdentityInfo.containsKey(idname)) {
				idMappingIdentityInfo.put(idname, null);
			}
		}
		
		// Setting face biometrics as photo
		if (Objects.nonNull(filteredIdentityInfo) && filteredIdentityInfo.containsKey(IdAuthCommonConstants.PHOTO)) {
			idMappingIdentityInfo.put(CbeffDocType.FACE.getType().value(), Objects.nonNull(bioValue) ? face : null);
		}
		kycResponseDTO.setIdentity(idMappingIdentityInfo);
	}
	
	private String getEntityForMatchType(MatchType matchType, Map<String, List<IdentityInfoDTO>> filteredIdentityInfo) {
		try {
			return idInfoHelper.getEntityInfoAsString(matchType, filteredIdentityInfo);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getEntityForMatchType",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
		}
		return null;
	}

	/**
	 * Construct identity info - Method to filter the details to be printed.
	 *
	 * @param allowedKycType the attributes defined as per policy
	 * @param identity       the identity information of the resident
	 * @param secLangCode    the secondary language code to retrieve identity
	 *                       information detail in secondary language
	 * @return the map returns filtered information defined as per policy
	 */
	private Map<String, List<IdentityInfoDTO>> filterIdentityInfo(List<String> allowedKycType,
			Map<String, List<IdentityInfoDTO>> identity, String secLangCode) {
		Map<String, List<IdentityInfoDTO>> identityInfo = null;
		Map<String, List<IdentityInfoDTO>> identityInfos = null;
		if (Objects.nonNull(allowedKycType)) {
			identityInfo = identity.entrySet().stream().filter(id -> allowedKycType.contains(id.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		if (Objects.nonNull(identityInfo)) {
			Set<String> allowedLang = idInfoHelper.getAllowedLang();
			String secondayLangCode = allowedLang.contains(secLangCode) ? env.getProperty(IdAuthConfigKeyConstants.MOSIP_SECONDARY_LANGUAGE)
					: null;
			String primaryLanguage = env.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE);
			identityInfos = identityInfo.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry
					.getValue().stream()
					.filter((IdentityInfoDTO info) -> Objects.isNull(info.getLanguage())
							|| info.getLanguage().equalsIgnoreCase("null")
							|| info.getLanguage().equalsIgnoreCase(primaryLanguage)
							|| (secondayLangCode != null && info.getLanguage().equalsIgnoreCase(secondayLangCode)))
					.collect(Collectors.toList())));
		}
		return identityInfos;
	}

}
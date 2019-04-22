package io.mosip.authentication.kyc.service.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.service.KycService;

/**
 * The implementation of Kyc Authentication service which retrieves the identity
 * information of the individual id and construct the KYC information
 * 
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService {

	/** The Constant MOSIP_SECONDARY_LANG_CODE. */
	private static final String MOSIP_SECONDARY_LANG_CODE = "mosip.secondary-language";

	/** The Constant MOSIP_PRIMARY_LANG_CODE. */
	private static final String MOSIP_PRIMARY_LANG_CODE = "mosip.primary-language";

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
	@SuppressWarnings("unchecked")
	@Override
	public KycResponseDTO retrieveKycInfo(String uin, List<String> allowedkycAttributes, String secLangCode,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException {
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		Map<String, Object> filteredIdentityInfo = constructIdentityInfo(allowedkycAttributes, identityInfo,
				secLangCode);
		if (Objects.nonNull(filteredIdentityInfo) && filteredIdentityInfo.get("face") instanceof List) {
			List<IdentityInfoDTO> faceValue = (List<IdentityInfoDTO>) filteredIdentityInfo.get("face");
			List<BioIdentityInfoDTO> bioValue = new ArrayList<>();
			if (Objects.nonNull(faceValue)) {
				BioIdentityInfoDTO bioIdentityInfoDTO = null;
				for (IdentityInfoDTO identityInfoDTO : faceValue) {
					DataDTO dataDTO = new DataDTO();
					bioIdentityInfoDTO = new BioIdentityInfoDTO();
					dataDTO.setBioType("face");
					dataDTO.setBioValue(identityInfoDTO.getValue());
					bioIdentityInfoDTO.setData(dataDTO);
					bioValue.add(bioIdentityInfoDTO);
				}
			}
			filteredIdentityInfo.put("biometrics", bioValue);
		}
		if (Objects.nonNull(filteredIdentityInfo)) {
			Map<String, Object> idMappingIdentityInfo = filteredIdentityInfo.entrySet().stream()
					.filter(entry -> entry.getKey() != null)
					.map(entry -> new SimpleEntry<>(
							IdaIdMapping.getIdNameForMapping(entry.getKey(), mappingConfig).orElse(""),
							entry.getValue()))
					.filter(entry -> !entry.getKey().isEmpty())
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			for (String kycAttribute : allowedkycAttributes) {
				String idname = IdaIdMapping.getIdNameForMapping(kycAttribute, mappingConfig).orElse("");
				if (!idname.isEmpty() && !idMappingIdentityInfo.containsKey(idname)) {
					idMappingIdentityInfo.put(idname, null);
				}
			}
			kycResponseDTO.setIdentity(idMappingIdentityInfo);
		}
		return kycResponseDTO;
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
	private Map<String, Object> constructIdentityInfo(List<String> allowedKycType,
			Map<String, List<IdentityInfoDTO>> identity, String secLangCode) {
		Map<String, List<IdentityInfoDTO>> identityInfo = null;
		Map<String, Object> identityInfos = null;
		if (Objects.nonNull(allowedKycType)) {
			identityInfo = identity.entrySet().stream().filter(id -> allowedKycType.contains(id.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		if (Objects.nonNull(identityInfo)) {
			Set<String> allowedLang = idInfoHelper.getAllowedLang();
			String secondayLangCode = allowedLang.contains(secLangCode) ? env.getProperty(MOSIP_SECONDARY_LANG_CODE)
					: null;
			String primaryLanguage = env.getProperty(MOSIP_PRIMARY_LANG_CODE);
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
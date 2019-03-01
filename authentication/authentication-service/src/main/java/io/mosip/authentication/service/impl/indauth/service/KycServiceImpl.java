package io.mosip.authentication.service.impl.indauth.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.service.helper.IdInfoHelper;

/**
 * The implementation of Kyc Authentication service.
 * 
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService {

	/** The Constant EKYC_TYPE_FULLKYC. */
	private static final String EKYC_TYPE_FULLKYC = "ekyc.type.fullkyc";
	
	/** The Constant EKYC_TYPE_LIMITEDKYC. */
	private static final String EKYC_TYPE_LIMITEDKYC = "ekyc.type.limitedkyc";
	/** The env. */
	@Autowired
	Environment env;
	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/**
	 * This method will return the KYC info of the individual.
	 *
	 * @param uin                   the uin
	 * @param eKycType              the ekyctype full or limited
	 * @param secLangCode the sec lang code
	 * @param identityInfo          the identity info
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public KycResponseDTO retrieveKycInfo(String uin, KycType eKycType, String secLangCode,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException {
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		String kycTypeKey;
		if (eKycType == KycType.LIMITED) {
			kycTypeKey = EKYC_TYPE_LIMITEDKYC;
		} else {
			kycTypeKey = EKYC_TYPE_FULLKYC;
		}

		String kycType = env.getProperty(kycTypeKey);
		Map<String, Object> filteredIdentityInfo = constructIdentityInfo(kycType, identityInfo,
				secLangCode);
		if(Objects.nonNull(filteredIdentityInfo) && filteredIdentityInfo.get("face") instanceof List) {			
			List<IdentityInfoDTO> faceValue = (List<IdentityInfoDTO>) filteredIdentityInfo.get("face");
			List<BioIdentityInfoDTO> bioValue = new ArrayList<>();
			if(Objects.nonNull(faceValue)) {
				BioIdentityInfoDTO bioIdentityInfoDTO = null;
				for(IdentityInfoDTO identityInfoDTO : faceValue) {				
					bioIdentityInfoDTO = new BioIdentityInfoDTO();
					bioIdentityInfoDTO.setType("face");
					bioIdentityInfoDTO.setValue(identityInfoDTO.getValue());
					bioValue.add(bioIdentityInfoDTO);
				}
			}
			filteredIdentityInfo.put("biometrics", bioValue);
		}
		if (Objects.nonNull(filteredIdentityInfo)) {
			Object maskedUin = uin;
			Boolean maskRequired = env.getProperty("uin.masking.required", Boolean.class);
			Integer maskCount = env.getProperty("uin.masking.charcount", Integer.class);
			if (null != maskRequired && maskRequired.booleanValue() && null != maskCount) {
				maskedUin = MaskUtil.generateMaskValue(uin, maskCount);
			}
			filteredIdentityInfo.put("uin", maskedUin);
			kycResponseDTO.setIdentity(filteredIdentityInfo);
		}
		return kycResponseDTO;
	}

	/**
	 * Construct identity info - Method to filter the details to be printed.
	 *
	 * @param kycType               the kyc type
	 * @param identity              the identity
	 * @param secLangCode the sec lang code
	 * @return the map
	 */
	private Map<String, Object> constructIdentityInfo(String kycType,
			Map<String, List<IdentityInfoDTO>> identity, String secLangCode) {
		Map<String, List<IdentityInfoDTO>> identityInfo = null;
		Map<String, Object> identityInfos = null;
		if (Objects.nonNull(kycType)) {
			List<String> limitedKycDetail = Arrays.asList(kycType.split(","));
			identityInfo = identity.entrySet().stream().filter(id -> limitedKycDetail.contains(id.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		if (Objects.nonNull(identityInfo)) {
			Set<String> allowedLang = idInfoHelper.extractAllowedLang();
			String secondayLangCode = allowedLang.contains(secLangCode) ? secLangCode : null;
			String primaryLanguage = env.getProperty("mosip.primary.lang-code");
			identityInfos = identityInfo.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
							entry -> entry.getValue().stream()
									.filter((IdentityInfoDTO info) -> info.getLanguage() == null
											|| info.getLanguage().equalsIgnoreCase("null")
											|| info.getLanguage().equalsIgnoreCase(primaryLanguage)
											|| (secondayLangCode != null && info.getLanguage().equalsIgnoreCase(secondayLangCode)))
									.collect(Collectors.toList())));
		}
		return identityInfos;
	}

}

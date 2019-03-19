package io.mosip.authentication.service.impl.indauth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
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

	/** The Constant MOSIP_SECONDARY_LANG_CODE. */
	private static final String MOSIP_SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	/** The Constant UIN_MASKING_CHARCOUNT. */
	private static final String UIN_MASKING_CHARCOUNT = "uin.masking.charcount";

	/** The Constant UIN_MASKING_REQUIRED. */
	private static final String UIN_MASKING_REQUIRED = "uin.masking.required";

	/** The Constant MOSIP_PRIMARY_LANG_CODE. */
	private static final String MOSIP_PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The env. */
	@Autowired
	Environment env;
	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/**
	 * This method will return the KYC info of the individual.
	 *
	 * @param uin                  the uin
	 * @param allowedkycAttributes the ekyctype full or limited
	 * @param secLangCode          the sec lang code
	 * @param identityInfo         the identity info
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
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
			Object maskedUin = uin;
			Boolean maskRequired = env.getProperty(UIN_MASKING_REQUIRED, Boolean.class);
			Integer maskCount = env.getProperty(UIN_MASKING_CHARCOUNT, Integer.class);
			if (maskRequired.booleanValue()) {
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
	 * @param allowedKycType the kyc type
	 * @param identity       the identity
	 * @param secLangCode    the sec lang code
	 * @return the map
	 */
	private Map<String, Object> constructIdentityInfo(List<String> allowedKycType,
			Map<String, List<IdentityInfoDTO>> identity, String secLangCode) {
		Map<String, List<IdentityInfoDTO>> identityInfo = null;
		Map<String, Object> identityInfos = null;
		if (Objects.nonNull(allowedKycType)) {
			identityInfo = identity.entrySet().stream()
					.filter(id -> allowedKycType.contains(id.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		if (Objects.nonNull(identityInfo)) {
			Set<String> allowedLang = idInfoHelper.extractAllowedLang();
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

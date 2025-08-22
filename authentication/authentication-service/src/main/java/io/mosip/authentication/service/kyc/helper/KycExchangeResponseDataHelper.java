package io.mosip.authentication.service.kyc.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.mosip.authentication.common.service.util.EntityInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.authentication.service.kyc.util.LocaleMappingUtil;
import io.mosip.biometrics.util.ConvertRequestDto;
import io.mosip.biometrics.util.face.FaceDecoder;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Kyc Exchange Response Data Helper which formats the identity
 * information of the individual id and construct the KYC Exchange information based on language code.
 *
 * @author Mahammed Taheer
 */

@Component
public class KycExchangeResponseDataHelper {
    

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(KycExchangeResponseDataHelper.class);	

	@Value("${ida.idp.consented.picture.attribute.name:picture}")
	private String consentedFaceAttributeName;

	@Value("${ida.idp.consented.address.attribute.name:address}")
	private String consentedAddressAttributeName;

	@Value("${ida.idp.consented.name.attribute.name:name}")
	private String consentedNameAttributeName;

	@Value("${ida.idp.consented.individual_id.attribute.name:individual_id}")
	private String consentedIndividualAttributeName;

	@Value("${ida.idp.consented.picture.attribute.prefix:data:image/jpeg;base64,}")
	private String consentedPictureAttributePrefix;

	@Value("${mosip.ida.idp.consented.address.subset.attributes:}")
	private String[] addressSubsetAttributes;

	@Value("${ida.idp.consented.address.value.separator: }")
	private String addressValueSeparator;
	
	/** The env. */
	@Autowired
	EnvUtil env;

	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private CbeffUtil cbeffUtil;

	@Autowired
	private EntityInfoUtil entityInfoUtil;


    public void addEntityDataForLangCodes(Map<String, String> mappedConsentedLocales, Map<String, List<IdentityInfoDTO>> idInfo, 
				Map<String, Object> respMap, String consentedAttribute, List<String> idSchemaAttributes) 
				throws IdAuthenticationBusinessException {
		
		if (consentedAttribute.equals(consentedFaceAttributeName)) {
			addFaceClaim(idInfo, consentedAttribute, respMap);
			return;
		}

		if (idSchemaAttributes.size() == 1) {
			List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttributes.get(0));
			if (Objects.isNull(idInfoList)) {
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
					"Data not available in Identity Info for the claim. So not adding to response claims. Claim Name: " + idSchemaAttributes.get(0));
				return;
			}
			Map<String, String> mappedLangCodes = LocaleMappingUtil.langCodeMapping(idInfoList);
			List<String> availableLangCodes = getAvailableLangCodes(mappedConsentedLocales, mappedLangCodes);
			if (availableLangCodes.size() == 1){
				for (IdentityInfoDTO identityInfo : idInfoList) {
					String langCode = mappedLangCodes.get(availableLangCodes.get(0));
					if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
						respMap.put(consentedAttribute, identityInfo.getValue());
					}
				}
			} else {
				if (availableLangCodes.size() > 0) {
					for (IdentityInfoDTO identityInfo : idInfoList) {
						for (String availableLangCode : availableLangCodes) {
							String langCode = mappedLangCodes.get(availableLangCode);
							if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
								respMap.put(consentedAttribute + IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + availableLangCode, 
										identityInfo.getValue());
							}
						}
					}
				} else {
					respMap.put(consentedAttribute, idInfoList.get(0).getValue());
				}
			}
		} else {
			if (consentedAttribute.equals(consentedNameAttributeName)) {
				addNameClaim(mappedConsentedLocales, idInfo, respMap, consentedAttribute, idSchemaAttributes);
			}
			if (consentedAttribute.equals(consentedAddressAttributeName)) {
				if (mappedConsentedLocales.size() > 1) {
					for (String consentedLocale: mappedConsentedLocales.keySet()) {
						String consentedLocaleValue = mappedConsentedLocales.get(consentedLocale);
						if (addressSubsetAttributes.length == 0) {
							mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
									"No address subset attributes configured. Will return the address with formatted attribute.");
							addFormattedAddress(idSchemaAttributes, idInfo, consentedLocaleValue, respMap, true, 
								IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + consentedLocaleValue);
							continue;
						}
						addAddressClaim(addressSubsetAttributes, idInfo, consentedLocaleValue, respMap, true, 
								IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + consentedLocaleValue);
					}
				} else {
					String consentedLocale = mappedConsentedLocales.keySet().iterator().next();
					String consentedLocaleValue = mappedConsentedLocales.get(consentedLocale);
					if (addressSubsetAttributes.length == 0) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
								"No address subset attributes configured. Will return the address with formatted attribute.");
						addFormattedAddress(idSchemaAttributes, idInfo, consentedLocaleValue, respMap, false, "");
						return;
					}
					
					addAddressClaim(addressSubsetAttributes, idInfo, consentedLocaleValue, respMap, false, "");
				}
			}
		}
	}

	private void addFaceClaim(Map<String, List<IdentityInfoDTO>> idInfo, String consentedAttribute, Map<String, Object> respMap) 
					throws IdAuthenticationBusinessException {

		String faceIdName = BioMatchType.FACE.getIdMapping().getIdname();
		if (!idInfo.containsKey(faceIdName)) {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
				"Face Data not found in DB. Skipping face claim in response.");
			return;
		}

		Map<String, String> faceEntityInfoMap = entityInfoUtil.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null);
		if (Objects.isNull(faceEntityInfoMap)) {
			return;
		}

		try {
			String faceCbeff = faceEntityInfoMap.get(CbeffDocType.FACE.getType().value());
			String faceBdb = getEncodedFaceBDB(faceCbeff); 
			String faceJpeg = convertJP2ToJpeg(faceBdb);

			if (Objects.nonNull(faceJpeg)) {
				String faceWithPrefix = consentedPictureAttributePrefix + faceJpeg;
				respMap.put(consentedAttribute, faceWithPrefix);
			}
		} catch (Exception e) {
			// Continue processing other claims even if face processing fails
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), 
					"addFaceClaim", "Error processing face data: " + e.getMessage(), e);
		}
	}

    private void addFormattedAddress(List<String> idSchemaAttributes, Map<String, List<IdentityInfoDTO>> idInfo, String localeValue, 
								Map<String, Object> respMap, boolean addLocale, String localeAppendValue) throws IdAuthenticationBusinessException {
		boolean langCodeFound = false;
		Map<String, String> addressMap = new HashMap<>();
		StringBuilder identityInfoValue = new StringBuilder(); 
		for (String schemaAttrib: idSchemaAttributes) {
			List<String> idSchemaSubsetAttributes = idInfoHelper.getIdentityAttributesForIdName(schemaAttrib);
			for (String idSchemaAttribute : idSchemaSubsetAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);
				Map<String, String> mappedLangCodes = LocaleMappingUtil.langCodeMapping(idInfoList);
				if (identityInfoValue.length() > 0) {
					identityInfoValue.append(addressValueSeparator);
				}
				if (mappedLangCodes.keySet().contains(localeValue)) {
					String langCode = mappedLangCodes.get(localeValue);
					for (IdentityInfoDTO identityInfo : idInfoList) { 
						if (identityInfoValue.length() > 0) {
							identityInfoValue.append(addressValueSeparator);
						}
						if (identityInfo.getLanguage().equals(langCode)) {
							langCodeFound = true;
							identityInfoValue.append(identityInfo.getValue());
						}
					}
				} else {
					if (Objects.nonNull(idInfoList) && idInfoList.size() == 1 && langCodeFound) {
						identityInfoValue.append(idInfoList.get(0).getValue());
					}
				}
			}
		}
		if (identityInfoValue.toString().trim().length() == 0) 
			return;
		//String identityInfoValueStr = identityInfoValue.toString();
		//String trimmedValue = identityInfoValueStr.substring(0, identityInfoValueStr.lastIndexOf(addressValueSeparator));
		addressMap.put(IdAuthCommonConstants.ADDRESS_FORMATTED + localeAppendValue, identityInfoValue.toString().trim());
		if (langCodeFound && addLocale)
			respMap.put(consentedAddressAttributeName + localeAppendValue, addressMap);
		else
			respMap.put(consentedAddressAttributeName, addressMap);
	}

	private void addAddressClaim(String[] addressAttributes, Map<String, List<IdentityInfoDTO>> idInfo, String consentedLocaleValue,
			Map<String, Object> respMap, boolean addLocale, String localeAppendValue) throws IdAuthenticationBusinessException {
		boolean langCodeFound = false; //added for language data not available in identity info (Eg: fr)
		Map<String, String> addressMap = new HashMap<>();
		for (String addressAttribute : addressAttributes) {
			List<String> idSchemaSubsetAttributes = idInfoHelper.getIdentityAttributesForIdName(addressAttribute);
			StringBuilder identityInfoValue = new StringBuilder(); 
			for (String idSchemaAttribute : idSchemaSubsetAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);
				Map<String, String> mappedLangCodes = LocaleMappingUtil.langCodeMapping(idInfoList);
				if (identityInfoValue.length() > 0) {
					identityInfoValue.append(addressValueSeparator);
				}
				if (mappedLangCodes.keySet().contains(consentedLocaleValue)) {
					String langCode = mappedLangCodes.get(consentedLocaleValue);
					for (IdentityInfoDTO identityInfo : idInfoList) {
						if (identityInfoValue.length() > 0) {
							identityInfoValue.append(addressValueSeparator);
						}
						if (identityInfo.getLanguage().equals(langCode)) {
							langCodeFound = true;
							identityInfoValue.append(identityInfo.getValue());
						}
					}
				} else {
					if (Objects.nonNull(idInfoList) && idInfoList.size() == 1 && langCodeFound) {
						identityInfoValue.append(idInfoList.get(0).getValue());
					}
				}
			}
			// Added below condition to skip if the data is not available in DB. MOSIP-26472
			if (identityInfoValue.toString().trim().length() > 0)
				addressMap.put(addressAttribute + localeAppendValue, identityInfoValue.toString().trim());
		}
		if (addressMap.size() == 0) 
			return;

		if (langCodeFound && addLocale)
			respMap.put(consentedAddressAttributeName + localeAppendValue, addressMap);
		else 
			respMap.put(consentedAddressAttributeName, addressMap);
	}

	private void addNameClaim(Map<String, String> mappedConsentedLocales, Map<String, List<IdentityInfoDTO>> idInfo, 
				Map<String, Object> respMap, String consentedAttribute, List<String> idSchemaAttributes) throws IdAuthenticationBusinessException{
		if(mappedConsentedLocales.size() > 1) {
			for (String consentedLocale: mappedConsentedLocales.keySet()) {
				String consentedLocaleValue = mappedConsentedLocales.get(consentedLocale);
				StringBuilder nameBuffer = new StringBuilder();
				for (String idSchemaAttribute : idSchemaAttributes) {
					List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);

					if (Objects.isNull(idInfoList)) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
							"Data not available in Identity Info for the claim. So not adding to response claims. Claim Name: " + idSchemaAttribute);
						continue;
					}
					if (nameBuffer.length() > 0) {
						nameBuffer.append(" ");
					}
					Map<String, String> mappedLangCodes = LocaleMappingUtil.langCodeMapping(idInfoList);
					if (!mappedLangCodes.keySet().contains(consentedLocaleValue)) {
						break;
					}
					for (IdentityInfoDTO identityInfo : idInfoList) {
						String langCode = mappedLangCodes.get(consentedLocaleValue);
						if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
							nameBuffer.append(identityInfo.getValue());
						}
					}
				}
				if (nameBuffer.toString().trim().length() > 0)
					respMap.put(consentedAttribute + IdAuthCommonConstants.CLAIMS_LANG_SEPERATOR + consentedLocaleValue, nameBuffer.toString());
			}
		} else {
			StringBuilder nameBuffer = new StringBuilder();
			for (String idSchemaAttribute : idSchemaAttributes) {
				List<IdentityInfoDTO> idInfoList = idInfo.get(idSchemaAttribute);

				if (Objects.isNull(idInfoList)) {
					mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "addEntityForLangCodes",
						"Data not available in Identity Info for the claim. So not adding to response claims. Claim Name: " + idSchemaAttribute);
					continue;
				}
				if (nameBuffer.length() > 0) {
					nameBuffer.append(" ");
				}
				Map<String, String> mappedLangCodes = LocaleMappingUtil.langCodeMapping(idInfoList);
				List<String> availableLangCodes = getAvailableLangCodes(mappedConsentedLocales, mappedLangCodes);
				if (availableLangCodes.size() == 0) {
					continue;
				} 
				for (IdentityInfoDTO identityInfo : idInfoList) {
					String langCode = mappedLangCodes.get(availableLangCodes.get(0));
					if (identityInfo.getLanguage().equalsIgnoreCase(langCode)) {
						nameBuffer.append(identityInfo.getValue());
					}
				}
			}
			if (nameBuffer.toString().trim().length() > 0)
				respMap.put(consentedAttribute, nameBuffer.toString());
		}
	}

	private String convertJP2ToJpeg(String jp2Image) {
		try {
			ConvertRequestDto convertRequestDto = new ConvertRequestDto();
			convertRequestDto.setVersion(IdAuthCommonConstants.FACE_ISO_NUMBER);
			convertRequestDto.setInputBytes(CryptoUtil.decodeBase64(jp2Image));
			byte[] image = FaceDecoder.convertFaceISOToImageBytes(convertRequestDto);
			return CryptoUtil.encodeBase64(image);
		} catch(Exception exp) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "convertJP2ToJpeg",
					"Error Converting JP2 To JPEG. " + exp.getMessage(), exp);
		}
		return null;
	}

	private List<String> getAvailableLangCodes(Map<String, String> mappedLocales, Map<String, String> mappedLangCodes) {
		return mappedLocales.entrySet().stream()
				.map(Map.Entry::getValue)
				.filter(locale -> mappedLangCodes.keySet().contains(locale))
				.collect(Collectors.toList());
	}
	
	private String encodeBioBDB(String bioCbeff, String bioType) throws Exception {
		List<BIR> birDataFromXMLType = cbeffUtil.getBIRDataFromXMLType(bioCbeff.getBytes(), bioType);
		if(birDataFromXMLType.isEmpty()) {
			//This is unlikely as if empty the exception would have been thrown already
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		return CryptoUtil.encodeBase64(birDataFromXMLType.get(0).getBdb());
	}

    public String getEncodedFaceBDB(String faceCbeff) throws Exception {
        return encodeBioBDB(faceCbeff, CbeffDocType.FACE.getName());
    }
}

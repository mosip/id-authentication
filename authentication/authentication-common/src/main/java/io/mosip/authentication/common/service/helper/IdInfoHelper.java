/*
 * 
 */
package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_SUBTYPE_SEPARATOR;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_TYPE_SEPARATOR;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_DEFAULT_IDENTITY_FILTER_ATTRIBUTES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.SingleAnySubtypeType;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Helper class to build Authentication request.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdInfoHelper {

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdInfoHelper.class);
	
	/** The default identiy filter attributes. */
	@Value("${" + IDA_DEFAULT_IDENTITY_FILTER_ATTRIBUTES + ":#{null}" + "}")
	private String defaultIdentiyFilterAttributes;
	
	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;
	
	/** The env. */
	@Autowired
	private EnvUtil env;

	@Autowired
	private LanguageUtil languageUtil;

	@Autowired
	private SeparatorHelper seperatorHelper;

	@Autowired
	private IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper;

	@Autowired
	private MatchTypeHelper matchTypeHelper;

	@Autowired
	private EntityInfoUtil entityInfoUtil;


	/**
	 * To check Whether Match type is Enabled.
	 *
	 * @param matchType the match type
	 * @return true, if is matchtype enabled
	 */
	public boolean isMatchtypeEnabled(MatchType matchType) {
		List<String> mappings = matchType.getIdMapping().getMappingFunction().apply(idMappingConfig, matchType);
		return mappings != null && !mappings.isEmpty();
	}
	
	/**
	 * Match id data.
	 *
	 * @param authRequestDTO  the identity DTO
	 * @param identityEntity  the id entity
	 * @param listMatchInputs the list match inputs
	 * @param partnerId the partner id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<MatchOutput> matchIdentityData(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> identityEntity, Collection<MatchInput> listMatchInputs, String partnerId)
			throws IdAuthenticationBusinessException {
        mosipLogger.info("listMatchInputs {}", listMatchInputs);
		List<MatchOutput> matchOutputList = new ArrayList<>();
		for (MatchInput matchInput : listMatchInputs) {
            mosipLogger.info("getMatchType {} getAuthType {}", matchInput.getMatchType(), matchInput.getAuthType());
			MatchOutput matchOutput = matchTypeHelper.matchType(authRequestDTO, identityEntity, matchInput, partnerId);
			if (matchOutput != null) {
				matchOutputList.add(matchOutput);
			}
		}
		return matchOutputList;
	}

	
	/**
	 * Gets the dynamic entity info for ID Name.
	 *
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCode the lang code
	 * @param idName the id name
	 * @return the entity for match type
	 */
	public Map<String, String> getDynamicEntityInfoAsStringWithKey(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, String langCode, String idName) {
		try {
			Map<String, String> idEntityInfoMap = entityInfoUtil.getIdEntityInfoMap(DemoMatchType.DYNAMIC, filteredIdentityInfo, langCode, idName);
			return idEntityInfoMap.isEmpty() ? Map.of() : Map.of(languageUtil.computeKey(idName, idEntityInfoMap.keySet().iterator().next(), langCode), idEntityInfoMap.entrySet()
					.stream()
					.map(Entry::getValue)
					.collect(Collectors.joining(seperatorHelper.getSeparator(idName))));
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getEntityForMatchType",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
		}
		return Map.of();
	}
	
	public String getDynamicEntityInfoAsString(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, String langCode, String idName) {
		Map<String, String> entityInfoMap = getDynamicEntityInfoAsStringWithKey(filteredIdentityInfo, langCode, idName);
		if(entityInfoMap == null || entityInfoMap.isEmpty()) {
			return null;
		}
		return entityInfoMap.values().iterator().next();
	}
	
	/**
	 * Gets the dynamic entity info for ID Name.
	 *
	 * @param filteredIdentityInfo the filtered identity info
	 * @param langCode the lang code
	 * @param idName the id name
	 * @return the entity for match type
	 */
	public Map<String, String> getDynamicEntityInfo(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, String langCode, String idName) {
		try {
			return entityInfoUtil.getIdEntityInfoMap(DemoMatchType.DYNAMIC, filteredIdentityInfo, langCode, idName).entrySet()
					.stream()
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getEntityForMatchType",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
		}
		return null;
	}
	
	/**
	 * Gets the default identity filter attributes from configuration.
	 *
	 * @return the default filter attributes
	 */
	public Set<String> getDefaultFilterAttributes() {
		return Optional.ofNullable(defaultIdentiyFilterAttributes).stream()
				.flatMap(str -> Stream.of(str.split(",")))
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toSet());
	}
	
	/**
	 * Builds the demo attribute filters.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the sets the
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings("unchecked")
	public Set<String> buildDemoAttributeFilters(AuthRequestDTO authRequestDTO)
			throws IdAuthenticationBusinessException {
		Set<String> defaultFilterAttributes = getDefaultFilterAttributes();
		Set<String> demoFilterAttributes = new HashSet<>(defaultFilterAttributes);
		if (AuthTypeUtil.isDemo(authRequestDTO)) {
			IdentityDTO demographics = authRequestDTO.getRequest().getDemographics();
			Set<String> demoAttributesFromReq = new HashSet<>();
			//Add mapped attributes
			Map<String, Object> demoMap = objectMapper.convertValue(demographics, Map.class);
			List<String> inputMappedAttributes = demoMap.entrySet()
														.stream()
														.filter(entry -> entry.getValue() != null)
														.map(Entry::getKey)
														.collect(Collectors.toList());
			for (String attrib : inputMappedAttributes) {
				if(!attrib.equals(IdAuthCommonConstants.METADATA)) {
					demoAttributesFromReq.addAll(getIdentityAttributesForIdName(attrib, false));
				}
			}
			
			//Add dynamic attributes
			Map<String, Object> dynamicAttributes = demographics.getMetadata();
			if(dynamicAttributes != null && !dynamicAttributes.isEmpty()) {
				Set<String> inputUnmappedAttributes = dynamicAttributes.keySet();
				for (String attrib : inputUnmappedAttributes) {
					if(dynamicAttributes.get(attrib) != null) {
						demoAttributesFromReq.addAll(getIdentityAttributesForIdName(attrib, true));
					}
				}
			}
			
			demoFilterAttributes.addAll(demoAttributesFromReq);
		}
		return demoFilterAttributes;
	}
	
	/**
	 * To build the bio filters. 
	 * These are used to decrypt only required bio attributes
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the sets the
	 */
	public Set<String> buildBioFilters(AuthRequestDTO authRequestDTO) {
		Set<String> bioFilters = new HashSet<String>();
		if (authRequestDTO.getRequest().getBiometrics() != null) {
			if (isBiometricDataNeeded(authRequestDTO)) {
				if (AuthTransactionHelper.isFingerAuth(authRequestDTO, env)) {
					List<BioIdentityInfoDTO> bioFingerInfo = getBioIds(authRequestDTO, BioAuthType.FGR_IMG.getType());
					if (!bioFingerInfo.isEmpty()) {
						List<DataDTO> bioFingerData = bioFingerInfo.stream().map(BioIdentityInfoDTO::getData)
								.collect(Collectors.toList());
						// for UNKNOWN getting all the subtypes
						if (bioFingerData.stream()
								.anyMatch(bio -> bio.getBioSubType().equals(IdAuthCommonConstants.UNKNOWN_BIO))) {
							bioFilters.addAll(getBioSubTypes(BiometricType.FINGER));
						} else {
							bioFilters.addAll(
									bioFingerData.stream().map(bio -> (bio.getBioType() + BIO_TYPE_SEPARATOR + bio.getBioSubType()))
											.collect(Collectors.toList()));
						}
					}
				}

				if (AuthTransactionHelper.isIrisAuth(authRequestDTO, env)) {
					List<BioIdentityInfoDTO> bioIrisInfo = getBioIds(authRequestDTO, BioAuthType.IRIS_IMG.getType());
					if (!bioIrisInfo.isEmpty()) {
						List<DataDTO> bioIrisData = bioIrisInfo.stream().map(BioIdentityInfoDTO::getData)
								.collect(Collectors.toList());
						// for UNKNOWN getting all the subtypes
						if (bioIrisData.stream()
								.anyMatch(bio -> bio.getBioSubType().equals(IdAuthCommonConstants.UNKNOWN_BIO))) {
							bioFilters.addAll(getBioSubTypes(BiometricType.IRIS));
						} else {
							bioFilters.addAll(
									bioIrisData.stream().map(bio -> (bio.getBioType() + BIO_TYPE_SEPARATOR + bio.getBioSubType()))
											.collect(Collectors.toList()));
						}
					}
				}
				if (AuthTransactionHelper.isFaceAuth(authRequestDTO, env)) {
					List<BioIdentityInfoDTO> bioFaceInfo = getBioIds(authRequestDTO, BioAuthType.FACE_IMG.getType());
					List<DataDTO> bioFaceData = bioFaceInfo.stream().map(BioIdentityInfoDTO::getData)
							.collect(Collectors.toList());
					if (!bioFaceData.isEmpty()) {
						bioFilters.addAll(
								bioFaceData.stream().map(bio -> (bio.getBioType())).collect(Collectors.toList()));
					}
				}
				return bioFilters;
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Gets the bio ids.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param type the type
	 * @return the bio ids
	 */
	private List<BioIdentityInfoDTO> getBioIds(AuthRequestDTO authRequestDTO, String type) {
		List<BioIdentityInfoDTO> identity = Optional.ofNullable(authRequestDTO.getRequest())
				.map(RequestDTO::getBiometrics).orElseGet(Collections::emptyList);
		if (!identity.isEmpty()) {
			return identity.stream().filter(Objects::nonNull)
					.filter(bioId -> bioId.getData().getBioType().equalsIgnoreCase(type)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	/**
	 * to get all bio subtypes for given type.
	 *
	 * @param type the type
	 * @return the bio sub types
	 */
	public List<String> getBioSubTypes(BiometricType type) {
		switch (type) {
		case FINGER:
			return getFingerSubTypes(type);
		case IRIS:
			return getIrisSubTypes(type);
		default:
			return Collections.emptyList();
		}
	}
	
	/**
	 * Construct and returns finger type along with all the sub types.
	 *
	 * @param type the type
	 * @return the finger sub types
	 */
	private List<String> getFingerSubTypes(BiometricType type){
		return List.of(type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.LEFT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.THUMB.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.LEFT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.INDEX_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.LEFT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.MIDDLE_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.LEFT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.RING_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.LEFT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.LITTLE_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.RIGHT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.THUMB.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.RIGHT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.INDEX_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.RIGHT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.MIDDLE_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.RIGHT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.RING_FINGER.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.RIGHT.value() + BIO_SUBTYPE_SEPARATOR + SingleAnySubtypeType.LITTLE_FINGER.value());
	}
	
	/**
	 * Construct and returns finger type along with all the sub types.
	 *
	 * @param type the type
	 * @return the iris sub types
	 */
	private List<String> getIrisSubTypes(BiometricType type){
		return List.of(type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.LEFT.value(),
				type.value() + BIO_TYPE_SEPARATOR + SingleAnySubtypeType.RIGHT.value());
	}

	public List<String> getIdentityAttributesForIdName(String idName)
			throws IdAuthenticationBusinessException {
		boolean isDynamic = idMappingConfig.getDynamicAttributes().keySet().contains(idName);
		return getIdentityAttributesForIdName(idName, isDynamic);
	}
	
	/**
	 * Gets the identity attributes for id name.
	 *
	 * @param idName the id name
	 * @param isDynamic the is dynamic
	 * @return the property names for id name
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<String> getIdentityAttributesForIdName(String idName, boolean isDynamic)
			throws IdAuthenticationBusinessException {
		DemoMatchType[] demoMatchTypes = DemoMatchType.values();
		List<String> propNames = new ArrayList<>();
		for (DemoMatchType demoMatchType : demoMatchTypes) {
			if(isDynamic == demoMatchType.isDynamic()) {
				List<String> propertyNamesForMatchType = identityAttributesForMatchTypeHelper.getIdentityAttributesForMatchType(demoMatchType, idName);
				if(!propertyNamesForMatchType.isEmpty()) {
					propNames.addAll(propertyNamesForMatchType);
				}
			}
		}
		if(propNames.isEmpty()) {
			propNames.add(idName);
		}
		return propNames;
	}
	
	public boolean isBiometricDataNeeded(AuthRequestDTO authRequestDTO) {
		return AuthTypeUtil.isBio(authRequestDTO) || containsPhotoKYCAttribute(authRequestDTO);
	}

	public boolean containsPhotoKYCAttribute(AuthRequestDTO authRequestDTO) {
		return (authRequestDTO instanceof EkycAuthRequestDTO)
				&& isKycAttributeHasPhoto((EkycAuthRequestDTO) authRequestDTO);
	}

	public static boolean isKycAttributeHasPhoto(EkycAuthRequestDTO authRequestDTO) {
		return getKycAttributeHasPhoto(authRequestDTO.getAllowedKycAttributes()).isPresent();
	}
	
	public static Optional<String> getKycAttributeHasPhoto(List<String> allowedKycAttributes) {
		return Optional.ofNullable(allowedKycAttributes)
				.stream()
				.flatMap(List::stream)
				.filter(elem -> elem.equalsIgnoreCase(IdAuthCommonConstants.PHOTO.toLowerCase())
						|| elem.equalsIgnoreCase(CbeffDocType.FACE.getType().value().toLowerCase()))
				.findAny();
	}
}

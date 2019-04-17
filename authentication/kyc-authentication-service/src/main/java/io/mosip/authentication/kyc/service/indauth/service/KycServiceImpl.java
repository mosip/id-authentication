package io.mosip.authentication.kyc.service.indauth.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.ResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.kernel.core.util.DateUtils;

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

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The env. */
	@Autowired
	Environment env;

	/** The Kyc Service */
	@Autowired
	private KycService kycService;

	/** The Id Info Service */
	@Autowired
	private IdAuthService<AutnTxn> idInfoService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The demo helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	@Autowired
	private IdAuthService<AutnTxn> idAuthService;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.service.KycService#processKycAuth(io
	 * .mosip.authentication.core.dto.indauth.KycAuthRequestDTO,
	 * io.mosip.authentication.core.dto.indauth.AuthResponseDTO, java.lang.String)
	 */
	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId) throws IdAuthenticationBusinessException {
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		Map<String, Object> idResDTO = null;
		String resTime = null;
		IdType idType = null;
		if (kycAuthRequestDTO != null) {
			String idvId = null;
			Optional<String> idvIdOptional = idInfoFetcher.getUinOrVid(kycAuthRequestDTO);
			if (idvIdOptional.isPresent()) {
				idvId = idvIdOptional.get();
			}
			String idvIdtype = idInfoFetcher.getUinOrVidType(kycAuthRequestDTO).getType();
			idResDTO = idAuthService.processIdType(idvIdtype, idvId, true);

			if (idvIdtype.equals(IdType.UIN.getType())) {
				idType = IdType.UIN;
			} else {
				idType = IdType.VID;
			}
			String dateTimePattern = env.getProperty(DATETIME_PATTERN);

			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getRequestTime(), isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					kycAuthRequestDTO.getIndividualId(), idType, AuditModules.EKYC_AUTH.getDesc());
		}
		Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(idResDTO);
		KycResponseDTO response = null;
		ResponseDTO authResponse = authResponseDTO.getResponse();
		if (idResDTO != null && authResponse != null && authResponse.isAuthStatus()) {
			response = kycService.retrieveKycInfo(String.valueOf(idResDTO.get("uin")),
					kycAuthRequestDTO.getAllowedKycAttributes(), kycAuthRequestDTO.getSecondaryLangCode(), idInfo);
			response.setTtl(env.getProperty("ekyc.ttl.hours"));

			response.setKycStatus(authResponse.isAuthStatus());
			response.setStaticToken(authResponse.getStaticToken());
			kycAuthResponseDTO.setResponse(response);
			kycAuthResponseDTO.setId(authResponseDTO.getId());
			kycAuthResponseDTO.setTransactionID(authResponseDTO.getTransactionID());
			kycAuthResponseDTO.setVersion(authResponseDTO.getVersion());
			kycAuthResponseDTO.setErrors(authResponseDTO.getErrors());
			kycAuthResponseDTO.setResponseTime(resTime);
		}
		return kycAuthResponseDTO;
	}

}
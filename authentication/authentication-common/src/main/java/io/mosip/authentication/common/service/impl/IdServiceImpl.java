package io.mosip.authentication.common.service.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
//import io.mosip.authentication.common.service.repository.VIDRepository;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The class validates the UIN and VID.
 *
 * @author Arun Bose
 * @author Rakesh Roshan
 */
@Service
public class IdServiceImpl implements IdService<AutnTxn> {


	private static final String INDIVIDUAL_BIOMETRICS = "individualBiometrics";

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdServiceImpl.class);

	/** The rest factory. */
	@Autowired
	private RestRequestFactory restFactory;
	
	/** The audit factory. */
	@Autowired
	private AuditRequestFactory auditFactory;

	@Autowired
	private IdRepoManager idRepoManager;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/*
	 * To get Identity data from IDRepo based on UIN
	 * 
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateUIN(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Object> getIdByUin(String uin, boolean isBio) throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = idRepoManager.getIdenity(uin, isBio);
		auditData();
		return idRepo;
	}

	/*
	 * To get Identity data from IDRepo based on VID
	 * 
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateVID(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Object> getIdByVid(String vid, boolean isBio) throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = getIdRepoByVidAsRequest(vid, isBio);
		auditData();
		return idRepo;
	}

	/**
	 * Do validate VID entity and checks for the expiry date.
	 *
	 * @param vid the vid
	 * @return the string
	 * @throws IdValidationFailedException the id validation failed exception
	 */
	Map<String, Object> getIdRepoByVidAsRequest(String vid, boolean isBio) throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = null;
		String uin = idRepoManager.getUINByVID(vid);
				try {
					idRepo = idRepoManager.getIdenity(uin, isBio);
				} catch (IdAuthenticationBusinessException e) {
					if (e.getErrorCode().equals(IdAuthenticationErrorConstants.UIN_DEACTIVATED.getErrorCode())) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VID_DEACTIVATED_UIN);
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
					}
				}
			
		return idRepo;
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param idvIdType idType
	 * @param idvId     id-number
	 * @param isBio
	 * @return map map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio)
			throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = null;
		if (idvIdType.equals(IdType.UIN.getType())) {
			try {
				idResDTO = getIdByUin(idvId, isBio);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
				throw e;
			}
		} else if(idvIdType.equals(IdType.VID.getType())) {
			try {
				idResDTO = getIdByVid(idvId, isBio);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}
		}
		
		else if(idvIdType.equals(IdType.USER_ID.getType())) {
			
				 try {
					 String regId = idRepoManager.getRIDByUID(idvId);
					 if(null!=regId) {
							idResDTO=idRepoManager.getIdByRID(regId, isBio);
						}
					} catch (IdAuthenticationBusinessException e) {
						logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
						throw e;
					}
	            } 
		return idResDTO;
		}

		
	

	/**
	 * Store entry in Auth_txn table for all authentications.
	 *
	 * @param idvId       idvId
	 * @param idvIdType   idvIdType(D/V)
	 * @param reqTime     reqTime
	 * @param txnId       txnId
	 * @param status      status('Y'/'N')
	 * @param comment     comment
	 * @param requestType requestType(OTP_REQUEST,OTP_AUTH,DEMO_AUTH,BIO_AUTH)
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void saveAutnTxn(AutnTxn authTxn) throws IdAuthenticationBusinessException {
		autntxnrepository.saveAndFlush(authTxn);
	}

	/**
	 * Audit data.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void auditData() throws IdAuthenticationBusinessException {
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");

		RestRequestDTO restRequest;
		try {
			restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
					ResponseWrapper.class);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
		}

		restHelper.requestAsync(restRequest);
	}

	/**
	 * Fetch data from Identity info value based on Identity response
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, List<IdentityInfoDTO>> getIdInfo(Map<String, Object> idResponseDTO)
			throws IdAuthenticationBusinessException {
		return idResponseDTO.entrySet().stream()
				.filter(entry -> entry.getKey().equals("response") && entry.getValue() instanceof Map)
				.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream()).flatMap(entry -> {
					if (entry.getKey().equals("identity") && entry.getValue() instanceof Map) {
						return ((Map<String, Object>) entry.getValue()).entrySet().stream();
					} else if (entry.getKey().equals("documents") && entry.getValue() instanceof List) {
						return (getDocumentValues((List<Map<String, Object>>) entry.getValue())).entrySet().stream();
					}
					return Stream.empty();
				}).collect(Collectors.toMap(t -> t.getKey(), entry -> {
					Object val = entry.getValue();
					if (val instanceof List) {
						List<Map> arrayList = (List) val;
						return arrayList.stream().filter(elem -> elem instanceof Map)
								.map(elem -> (Map<String, Object>) elem).map(map1 -> {
									String value = String.valueOf(map1.get("value"));
									IdentityInfoDTO idInfo = new IdentityInfoDTO();
									if (map1.containsKey("language")) {
										idInfo.setLanguage(String.valueOf(map1.get("language")));
									}
									idInfo.setValue(value);
									return idInfo;
								}).collect(Collectors.toList());

					} else if (val instanceof Boolean || val instanceof String || val instanceof Long
							|| val instanceof Integer || val instanceof Double) {
						IdentityInfoDTO idInfo = new IdentityInfoDTO();
						idInfo.setValue(String.valueOf(val));
						return Stream.of(idInfo).collect(Collectors.toList());
					}
					return Collections.emptyList();
				}));

	}

	/**
	 * Fetch document values for Individual's
	 * 
	 * @param value
	 * @return
	 * @throws IdAuthenticationDaoException
	 */
	private Map<String, Object> getDocumentValues(List<Map<String, Object>> value) {
		Map<String, Object> docValues = value.stream().filter(map -> INDIVIDUAL_BIOMETRICS.equals(map.get("category")))
				.flatMap(map -> map.entrySet().stream()).filter(entry -> entry.getKey().equalsIgnoreCase("value"))
				.<Entry<String, String>>map(
						entry -> new SimpleEntry<>("documents." + INDIVIDUAL_BIOMETRICS, (String) entry.getValue()))
				.collect(Collectors.toMap(Entry<String, String>::getKey, Entry<String, String>::getValue));
		return docValues;

	}

}

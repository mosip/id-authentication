package io.mosip.authentication.common.service.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The class validates the UIN and VID.
 *
 * @author Arun Bose
 * @author Rakesh Roshan
 */
@Service
public class IdServiceImpl implements IdService<AutnTxn> {

	/** The Constant INDIVIDUAL_BIOMETRICS. */
	private static final String INDIVIDUAL_BIOMETRICS = "individualBiometrics";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdServiceImpl.class);

	/** The id repo manager. */
	@Autowired
	private IdRepoManager idRepoManager;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;
	
	@Autowired
	private ObjectMapper mapper;

	/*
	 * To get Identity data from IDRepo based on UIN
	 * 
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateUIN(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Object> getIdByUin(String uin, boolean isBio) throws IdAuthenticationBusinessException {
		return idRepoManager.getIdentity(uin, isBio);
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
		return idRepoManager.getIdentity(vid, isBio, IdType.VID);
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param idvIdType idType
	 * @param idvId     id-number
	 * @param isBio the is bio
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
	 * @param authTxn the auth txn
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void saveAutnTxn(AutnTxn authTxn) throws IdAuthenticationBusinessException {
		autntxnrepository.saveAndFlush(authTxn);
	}

	/**
	 * Fetch data from Identity info value based on Identity response.
	 *
	 * @param idResponseDTO the id response DTO
	 * @return the id info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
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
	 * Fetch document values for Individual's.
	 *
	 * @param value the value
	 * @return the document values
	 */
	private Map<String, Object> getDocumentValues(List<Map<String, Object>> value) {
		return value.stream().filter(map -> INDIVIDUAL_BIOMETRICS.equals(map.get("category")))
				.flatMap(map -> map.entrySet().stream()).filter(entry -> entry.getKey().equalsIgnoreCase("value"))
				.<Entry<String, String>>map(
						entry -> new SimpleEntry<>("documents." + INDIVIDUAL_BIOMETRICS, (String) entry.getValue()))
				.collect(Collectors.toMap(Entry<String, String>::getKey, Entry<String, String>::getValue));

	}
	
	/**
	 * Gets the demo data.
	 *
	 * @param identity the identity
	 * @return the demo data
	 */
	@SuppressWarnings("unchecked")
	public byte[] getDemoData(Map<String, Object> identity) {
		return Optional.ofNullable(identity.get("response"))
								.filter(obj -> obj instanceof Map)
								.map(obj -> ((Map<String, Object>)obj).get("identity"))
								.filter(obj -> obj instanceof Map)
								.map(obj -> {
									try {
										return mapper.writeValueAsBytes(obj);
									} catch (JsonProcessingException e) {
										logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
												"handleCreateUinEvent", e.getMessage());
									}
									return new byte[0];
								})
								.orElse(new byte[0]);
	}
	
	/**
	 * Gets the bio data.
	 *
	 * @param identity the identity
	 * @return the bio data
	 */
	@SuppressWarnings("unchecked")
	public byte[] getBioData(Map<String, Object> identity) {
		return Optional.ofNullable(identity.get("response"))
								.filter(obj -> obj instanceof Map)
								.map(obj -> ((Map<String, Object>)obj).get("documents"))
								.filter(obj -> obj instanceof List)
								.flatMap(obj -> 
										((List<Map<String, Object>>)obj)
											.stream()
											.filter(map -> map.containsKey("category") 
															&& map.get("category").toString().equalsIgnoreCase("individualBiometrics")
															&& map.containsKey("value"))
											.map(map -> (String)map.get("value"))
											.findAny())
								.map(CryptoUtil::decodeBase64)
								.orElse(new byte[0]);
	}

}

package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_ZERO_KNOWLEDGE_UNENCRYPTED_CREDENTIAL_ATTRIBUTES;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The class validates the UIN and VID.
 *
 * @author Arun Bose
 * @author Rakesh Roshan
 */
@Service
public class IdServiceImpl implements IdService<AutnTxn> {

	private static final String TOKEN = "TOKEN";

	private static final String ID_HASH = "ID_HASH";

	private static final String BIOMETRICS = "biometrics";

	private static final String DEMOGRAPHICS = "demographics";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdServiceImpl.class);

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private IdentityCacheRepository identityRepo;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Value("${" + IDA_ZERO_KNOWLEDGE_UNENCRYPTED_CREDENTIAL_ATTRIBUTES + ":#{null}" + "}")
	private String zkUnEncryptedCredAttribs;

	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;

	/*
	 * To get Identity data from IDRepo based on UIN
	 *
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateUIN(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Object> getIdByUin(String uin, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
		return getIdentity(uin, isBio, filterAttributes);
	}

	/*
	 * To get Identity data from IDRepo based on VID
	 *
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateVID(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Object> getIdByVid(String vid, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
		return getIdentity(vid, isBio, IdType.VID, filterAttributes);
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
	public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio, boolean markVidConsumed, Set<String> filterAttributes)
			throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = null;
		if (idvIdType.equals(IdType.UIN.getType()) || idvIdType.equals(IdType.HANDLE.getType())) {
			try {
				idResDTO = getIdByUin(idvId, isBio, filterAttributes);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
				throw e;
			}
		} else if(idvIdType.equals(IdType.VID.getType())) {
			try {
				idResDTO = getIdByVid(idvId, isBio, filterAttributes);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}

			if(markVidConsumed) {
				updateVIDstatus(idvId);
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
	 * Gets the demo data.
	 *
	 * @param identity the identity
	 * @return the demo data
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDemoData(Map<String, Object> identity) {
		return Optional.ofNullable(identity.get("response"))
				.filter(obj -> obj instanceof Map)
				.map(obj -> ((Map<String, Object>)obj).get("identity"))
				.filter(obj -> obj instanceof Map)
				.map(obj -> (Map<String, Object>) obj)
				.orElseGet(Collections::emptyMap);
	}

	public Map<String, Object> getIdentity(String id, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
		return getIdentity(id, isBio, IdType.UIN, filterAttributes);
	}

	/**
	 * Fetch data from Id Repo based on Individual's UIN / VID value and all UIN.
	 *
	 * @param id
	 *            the uin
	 * @param isBio
	 *            the is bio
	 * @return the idenity
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getIdentity(String id, boolean isBio, IdType idType, Set<String> filterAttributes) throws IdAuthenticationBusinessException {
		String hashedId;
		try {
			hashedId = securityManager.hash(id);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
							idType.getType(), e));
		}

		try {
			IdentityEntity entity = null;
			if (!identityRepo.existsById(hashedId)) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
						"Id not found in DB");
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
								idType.getType()));
			}

			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					"Generated HASHID >> " + hashedId);

			if (isBio) {
				entity = identityRepo.getOne(hashedId);
			} else {
				Object[] data = identityRepo.findDemoDataById(hashedId).get(0);
				entity = new IdentityEntity();
				entity.setId(String.valueOf(data[0]));
				entity.setDemographicData((byte[]) data[1]);
				entity.setExpiryTimestamp(Objects.nonNull(data[2]) ? LocalDateTime.parse(String.valueOf(data[2])) : null);
				entity.setTransactionLimit(Objects.nonNull(data[3]) ? Integer.parseInt(String.valueOf(data[3])) : null);
				entity.setToken(String.valueOf(data[4]));
			}

			if (Objects.nonNull(entity.getExpiryTimestamp())
					&& DateUtils.before(entity.getExpiryTimestamp(), DateUtils.getUTCCurrentDateTime())) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
						idType.getType() + " expired/deactivated/revoked/blocked");
				IdAuthenticationErrorConstants errorConstant;
				if (idType == IdType.UIN) {
					errorConstant = IdAuthenticationErrorConstants.UIN_DEACTIVATED_BLOCKED;
				} else {
					errorConstant = IdAuthenticationErrorConstants.VID_EXPIRED_DEACTIVATED_REVOKED;
				}
				throw new IdAuthenticationBusinessException(errorConstant);
			}

			Map<String, Object> responseMap = new LinkedHashMap<>();

			Map<String, String> demoDataMap = mapper.readValue(entity.getDemographicData(), Map.class);
			Set<String> filterAttributesInLowercase = filterAttributes.isEmpty() ? Set.of()
					: filterAttributes.stream().map(String::toLowerCase).collect(Collectors.toSet());

			if (!filterAttributesInLowercase.isEmpty()) {
				Map<String, String> demoDataMapPostFilter = demoDataMap.entrySet().stream()
						.filter(demo -> filterAttributesInLowercase.contains(demo.getKey().toLowerCase()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
				responseMap.put(DEMOGRAPHICS, decryptConfiguredAttributes(id, demoDataMapPostFilter));
			}

			if (entity.getBiometricData() != null) {
				Map<String, String> bioDataMap = mapper.readValue(entity.getBiometricData(), Map.class);
				if (!filterAttributesInLowercase.isEmpty()) {
					Map<String, String> bioDataMapPostFilter = bioDataMap.entrySet().stream()
							.filter(bio -> filterAttributesInLowercase.contains(bio.getKey().toLowerCase()))
							.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
					responseMap.put(BIOMETRICS, decryptConfiguredAttributes(id, bioDataMapPostFilter));
				}
			}
			responseMap.put(TOKEN, entity.getToken());
			responseMap.put(ID_HASH, hashedId);
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					"TOKEN in responseMap >> " + entity.getToken());
			return responseMap;
		} catch (IOException | DataAccessException | TransactionException | JDBCConnectionException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Decrypt the attributes as per configuration.
	 * @param id
	 * @param dataMap
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private Map<String, Object> decryptConfiguredAttributes(String id, Map<String, String> dataMap) throws IdAuthenticationBusinessException {
		List<String> zkUnEncryptedAttributes = getZkUnEncryptedAttributes()
				.stream().map(String::toLowerCase).collect(Collectors.toList());
		Map<Boolean, Map<String, String>> partitionedMap = dataMap.entrySet()
				.stream()
				.collect(Collectors.partitioningBy(entry ->
								!zkUnEncryptedAttributes.contains(entry.getKey().toLowerCase()),
						Collectors.toMap(Entry::getKey, Entry::getValue)));
		Map<String, String> dataToDecrypt = partitionedMap.get(true);
		Map<String, String> plainData = partitionedMap.get(false);
		Map<String, String> decryptedData = dataToDecrypt.isEmpty() ? Map.of()
				: securityManager.zkDecrypt(id, dataToDecrypt);
		Map<String, String> finalDataStr = new LinkedHashMap<>();
		finalDataStr.putAll(plainData);
		finalDataStr.putAll(decryptedData);
		return finalDataStr.entrySet().stream().collect(Collectors.toMap(entry -> (String) entry.getKey(),
				entry -> {
					Object valObject = entry.getValue();
					if (valObject instanceof String) {
						String val = (String) valObject;
						if (val.trim().startsWith("[") || val.trim().startsWith("{")) {
							try {
								return mapper.readValue(val.getBytes(), Object.class);
							} catch (IOException e) {
								logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
										"decryptConfiguredAttributes", ExceptionUtils.getStackTrace(e));
								return val;
							}
						} else {
							return val;
						}
					} else {
						return valObject;
					}
				}));
	}

	/**
	 * Get the list of attributes not to decrypt from config. Returns empty if no config is there
	 * @return
	 */
	private List<String> getZkUnEncryptedAttributes() {
		return Optional.ofNullable(zkUnEncryptedCredAttribs).stream()
				.flatMap(str -> Stream.of(str.split(",")))
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toList());
	}

	/**
	 * Update VID dstatus.
	 *
	 * @param vid
	 *            the vid
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void updateVIDstatus(String vid) throws IdAuthenticationBusinessException {
		try {
			vid = securityManager.hash(vid);
			// Assumption : If transactionLimit is null, id is considered as Perpetual VID
			// If transactionLimit is nonNull, id is considered as Temporary VID

			//get entity
			Optional<IdentityEntity> entityOpt = identityRepo.findById(vid);
			if(entityOpt.isPresent()) {
				IdentityEntity entity =entityOpt.get();
				Integer transactionLimit = entity.getTransactionLimit();
				if (identityRepo.existsById(vid)
						&& Objects.nonNull(transactionLimit)){
					int newTransactionLimit = transactionLimit-1;
					if (newTransactionLimit>0) {
						entity.setTransactionLimit(newTransactionLimit);
						identityRepo.save(entity);
					} else {
						identityRepo.deleteById(vid);
					}
				}
			}

		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	@Override
	public String getToken(Map<String, Object> idResDTO) {
		return (String) idResDTO.get(TOKEN);
	}


	@Override
	public String getIdHash(Map<String, Object> idResDTO) {
		return (String) idResDTO.get(ID_HASH);

	}

	@Override
	public void checkIdKeyBindingPermitted(String idvId, String idvIdType) throws IdAuthenticationBusinessException {
		try {
			String idVidHash = securityManager.hash(idvId);
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
						"Checking Id Key Binding Permitted or not. IdVidHash: " + idVidHash);
			// Assumption : If transactionLimit is null, id is considered as Perpetual VID
			// If transactionLimit is nonNull, id is considered as Temporary VID
			// Duplicated identity data fetching from DB, because to avoid lot of if else conditions needs to be added in 
			// above getIdentity method. Above getIdentity method also includes data decryption logic. 
			List<Object[]> entityObjList = identityRepo.findTransactionLimitById(idVidHash);
			if(entityObjList.size() == 0) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
						"Id not found in DB");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage()));
			}
			Object[] entityObjs = entityObjList.get(0);
			
			LocalDateTime expiryTimestamp = Objects.nonNull(entityObjs[1]) ? LocalDateTime.parse(String.valueOf(entityObjs[1])) : null;

			if (Objects.nonNull(expiryTimestamp)
					&& DateUtils.before(expiryTimestamp, DateUtils.getUTCCurrentDateTime())) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
					idvIdType + " expired/deactivated/revoked/blocked");
				IdAuthenticationErrorConstants errorConstant;
				if (idvIdType.equals(IdType.UIN.getType())) {
					errorConstant = IdAuthenticationErrorConstants.UIN_DEACTIVATED_BLOCKED;
				} else {
					errorConstant = IdAuthenticationErrorConstants.VID_EXPIRED_DEACTIVATED_REVOKED;
				}
				throw new IdAuthenticationBusinessException(errorConstant);
			}
			
			int transactionLimit = Objects.nonNull(entityObjs[2]) ? Integer.parseInt(String.valueOf(entityObjs[2])) : -1;
			if (transactionLimit > 0) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
						"Id not allowed for identity key binding.");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_KEY_BINDING_NOT_ALLOWED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.ID_KEY_BINDING_NOT_ALLOWED.getErrorMessage()));
			} 
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "checkIdKeyBindingPermitted",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
}

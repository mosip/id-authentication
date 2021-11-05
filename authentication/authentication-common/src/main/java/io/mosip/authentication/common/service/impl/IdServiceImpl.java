package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_ZERO_KNOWLEDGE_UNENCRYPTED_CREDENTIAL_ATTRIBUTES;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.spi.CbeffUtil;
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
	
	@Autowired
	private CbeffUtil cbeffUtil;
	
	private static final String ERROR_CODE = "errorCode";

	private static final String ERRORS = "errors";

	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * The Restrequest Factory
	 */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/**
	 * The Environment
	 */
	@Autowired
	private Environment environment;
	
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
		if (idvIdType.equals(IdType.UIN.getType())) {
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
		Set<String> filterAttributesInLowercase = filterAttributes.stream().map(String::toLowerCase)
				.collect(Collectors.toSet());
		String hashedId;
		try {
			hashedId = securityManager.hash(id);
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
							idType.getType()));
		}
		
		try {
			IdentityEntity entity = null;
//			if (!identityRepo.existsById(hashedId)) {
//				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
//						"Id not found in DB");
//				throw new IdAuthenticationBusinessException(
//						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
//						String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
//								idType.getType()));
//			}
			
			Map<String, Object> idData = loadIdentityFromFile("C:\\OneDrive - Mindtree Limited\\MOSIP\\IDA\\DOCS\\2419762130-dev-response.json");
			
			//Map<String, Object> idData = loadIdentityFromIdRepo(id, isBio);
			
			String demodata = mapper.writeValueAsString(idData.get("demographics"));
			entity = new IdentityEntity();
			entity.setDemographicData(demodata.getBytes("UTF-8"));

			if (isBio) {
				//entity = identityRepo.getOne(hashedId);
				String biodata = mapper.writeValueAsString(idData.get("biometrics"));
				entity.setBiometricData(biodata.getBytes("UTF-8"));

			} else {
				//Object[] data = identityRepo.findDemoDataById(hashedId).get(0);
				//entity = new IdentityEntity();
				//entity.setId(String.valueOf(data[0]));
				//entity.setExpiryTimestamp(Objects.nonNull(data[2]) ? LocalDateTime.parse(String.valueOf(data[2])) : null);
				//entity.setTransactionLimit(Objects.nonNull(data[3]) ? Integer.parseInt(String.valueOf(data[3])) : null);
				//entity.setToken(String.valueOf(data[4]));
			}
			entity.setToken("dummyToken");
			
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
			if (!filterAttributes.isEmpty()) {		
				Map<String, String> demoDataMapPostFilter = demoDataMap.entrySet().stream()
						.filter(demo -> filterAttributesInLowercase.contains(demo.getKey().toLowerCase()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue));					
				responseMap.put(DEMOGRAPHICS, decryptConfiguredAttributes(id, demoDataMapPostFilter));
			}
			
			if (entity.getBiometricData() != null) {
				Map<String, String> bioDataMap = mapper.readValue(entity.getBiometricData(), Map.class);				
				if (!filterAttributes.isEmpty()) {					
					Map<String, String> bioDataMapPostFilter = bioDataMap.entrySet().stream()
							.filter(bio -> filterAttributesInLowercase.contains(bio.getKey().toLowerCase()))
							.collect(Collectors.toMap(Entry::getKey, Entry::getValue));					
					responseMap.put(BIOMETRICS, decryptConfiguredAttributes(id, bioDataMapPostFilter));
				}
			}
			responseMap.put(TOKEN, entity.getToken());
			return responseMap;
		} catch (IOException | DataAccessException | TransactionException | JDBCConnectionException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	private Map<String, Object> loadIdentityFromIdRepo(String id, boolean isBio) throws IdAuthenticationBusinessException, JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
		return loadIdentityFromInputStream(new ByteArrayInputStream(getIdByID(id, isBio).getBytes("UTF-8")));
	}

	/**
	 * Gets the id by RID.
	 *
	 * @param id the reg ID
	 * @param isBio the is bio
	 * @return the id by RID
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	public String getIdByID(String id, boolean isBio) throws IdAuthenticationBusinessException, JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
		RestRequestDTO buildRequest = null;
		String idRepoResponse = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("rid", id);
			if (isBio) {
				buildRequest = restRequestFactory.buildRequest(RestServicesConstants.RID_UIN, null, Map.class);
				params.put("type", "bio");
			} else {
				buildRequest = restRequestFactory.buildRequest(RestServicesConstants.RID_UIN_WITHOUT_TYPE, null,
						String.class);
			}
			buildRequest.setPathVariables(params);
			idRepoResponse = restHelper.requestSync(buildRequest);
			Map<String, Object> idRepoResponseMap = mapper.readValue(idRepoResponse.getBytes("UTF-8"), Map.class);
			if (!environment.getProperty(IdRepoConstants.ACTIVE_STATUS).equalsIgnoreCase(
					(String) ((Map<String, Object>) idRepoResponseMap.get(IdAuthCommonConstants.RESPONSE)).get(IdAuthCommonConstants.STATUS))) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			}
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey(ERRORS)) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get(ERRORS);

					if (!idRepoerrorList.isEmpty() && idRepoerrorList.stream()
							.anyMatch(map -> map.containsKey(ERROR_CODE)
									&& (IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode()
											.equalsIgnoreCase((String) map.get(ERROR_CODE)))
									|| IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode().equalsIgnoreCase((String)map.get(ERROR_CODE)))) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
								                    String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),IdType.UIN.getType()));
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
								e);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return idRepoResponse;
	}

	private Map<String, Object> loadIdentityFromFile(String identityJsonFilePath) {

		try {
			File inputJsonFile = new File(identityJsonFilePath);
			FileInputStream fileInputStream = new FileInputStream(inputJsonFile);
			return loadIdentityFromInputStream(fileInputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Map.of();
	}

	private Map<String, Object> loadIdentityFromInputStream(InputStream fileInputStream) {
		Map<String, Object> idMap = new HashMap<>();

		try {
			Map<String, Object> inputId = mapper.readValue(fileInputStream, Map.class);
			Map<String, Object> respMap = (Map<String, Object>)inputId.get("response");
			idMap.put("demographics", respMap.get("identity"));
			List<Map<String, Object>> docsList = (List<Map<String,Object>>)respMap.get("documents");
			
			Optional<String> individualBioCbeff = docsList.stream()
																.filter(map -> map.get("category") instanceof String && map.get("category").equals("individualBiometrics"))
																.map(map -> (String)map.get("value"))
																.map(cbeffEncoded -> new String(CryptoUtil.decodeBase64Url(cbeffEncoded)))
																.findAny();
			if(individualBioCbeff.isPresent()) {
				List<BIR> birList = cbeffUtil.getBIRDataFromXML(individualBioCbeff.get().getBytes());
				Map<String, Object> bioMap = new HashMap<>();

				for (BIR bir : birList) {
					List<BIR> birs = new ArrayList<>();
					birs.add(bir);
					BDBInfo bdbInfo = bir.getBdbInfo();
					String type = bdbInfo.getType().get(0).value();
					String subType = getSubType(bdbInfo.getSubtype());
					if (subType != null) {
						bioMap.put(type + "_" + subType, new String(cbeffUtil.createXML(birs)));
					}
				}
				
				List<BIR> faceBirList = birList.stream()
						.filter(bir -> bir.getBdbInfo().getType().get(0).value().toLowerCase().startsWith(BiometricType.FACE.value().toLowerCase()))
						.collect(Collectors.toList());
				if (!faceBirList.isEmpty()) {
					bioMap.put(BiometricType.FACE.value(), new String(cbeffUtil.createXML(faceBirList)));
				}
				
				idMap.put("biometrics", bioMap);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idMap;
	}

	private String getSubType(List<String> bdbSubTypeList) {
		String subType;
		try {
			if (bdbSubTypeList.size() == 1) {
				subType = bdbSubTypeList.get(0);
			} else {
				subType = bdbSubTypeList.get(0) + " " + bdbSubTypeList.get(1);
			}
			return subType;
		} catch (Exception e) {
			return null;
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
		List<String> zkUnEncryptedAttributes = List.of();//getZkUnEncryptedAttributes()
				//.stream().map(String::toLowerCase).collect(Collectors.toList());
		Map<Boolean, Map<String, String>> partitionedMap = dataMap.entrySet()
				.stream()
				.collect(Collectors.partitioningBy(entry -> 
							zkUnEncryptedAttributes.contains(entry.getKey().toLowerCase()),
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
			if (identityRepo.existsById(vid)
					&& Objects.nonNull(identityRepo.getOne(vid).getTransactionLimit())) {
				identityRepo.deleteById(vid);
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

}

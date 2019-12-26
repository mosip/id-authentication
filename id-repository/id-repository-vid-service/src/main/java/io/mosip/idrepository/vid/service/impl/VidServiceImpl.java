package io.mosip.idrepository.vid.service.impl;

import static io.mosip.idrepository.core.constant.IdRepoConstants.ACTIVE_STATUS;
import static io.mosip.idrepository.core.constant.IdRepoConstants.APPLICATION_VERSION_VID;
import static io.mosip.idrepository.core.constant.IdRepoConstants.MODULO_VALUE;
import static io.mosip.idrepository.core.constant.IdRepoConstants.SPLITTER;
import static io.mosip.idrepository.core.constant.IdRepoConstants.VID_ACTIVE_STATUS;
import static io.mosip.idrepository.core.constant.IdRepoConstants.VID_DEACTIVATED;
import static io.mosip.idrepository.core.constant.IdRepoConstants.VID_REGENERATE_ACTIVE_STATUS;
import static io.mosip.idrepository.core.constant.IdRepoConstants.VID_REGENERATE_ALLOWED_STATUS;
import static io.mosip.idrepository.core.constant.IdRepoConstants.VID_UNLIMITED_TRANSACTION_STATUS;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.DATABASE_ACCESS_ERROR;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.INVALID_INPUT_PARAMETER;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.INVALID_UIN;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.INVALID_VID;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.NO_RECORD_FOUND;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.UIN_HASH_MISMATCH;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.UIN_RETRIEVAL_FAILED;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.VID_GENERATION_FAILED;
import static io.mosip.idrepository.core.constant.IdRepoErrorConstants.VID_POLICY_FAILED;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.AuditEvents;
import io.mosip.idrepository.core.constant.AuditModules;
import io.mosip.idrepository.core.constant.IdType;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.dto.VidPolicy;
import io.mosip.idrepository.core.dto.VidRequestDTO;
import io.mosip.idrepository.core.dto.VidResponseDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.AuditHelper;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;
import io.mosip.idrepository.vid.repository.UinEncryptSaltRepo;
import io.mosip.idrepository.vid.repository.UinHashSaltRepo;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * The Class VidServiceImpl - service implementation for VID service.
 *
 * @author Manoj SP
 * @author Prem Kumar
 */
@Component
@Transactional
public class VidServiceImpl implements VidService<VidRequestDTO, ResponseWrapper<VidResponseDTO>> {

	/** The Constant VID. */
	private static final String VID = "vid";

	/** The Constant REACTIVATE. */
	private static final String REACTIVATE = "reactivate";

	/** The Constant DEACTIVATE. */
	private static final String DEACTIVATE = "deactivate";

	/** The Constant REGENERATE_VID. */
	private static final String REGENERATE_VID = "regenerateVid";

	/** The Constant UPDATE_VID. */
	private static final String UPDATE_VID = "updateVid";

	/** The Constant CREATE_VID. */
	private static final String CREATE_VID = "createVid";

	/** The Constant RETRIEVE_UIN_BY_VID. */
	private static final String RETRIEVE_UIN_BY_VID = "retrieveUinByVid";

	/** The mosip logger. */
	private Logger mosipLogger = IdRepoLogger.getLogger(VidServiceImpl.class);

	/** The Constant EXPIRED. */
	private static final String EXPIRED = "EXPIRED";

	/** The Constant ID_REPO_VID_SERVICE. */
	private static final String ID_REPO_VID_SERVICE = "VidService";

	/** The env. */
	@Autowired
	private Environment env;

	/** The vid repo. */
	@Autowired
	private VidRepo vidRepo;

	/** The rest builder. */
	@Autowired
	private RestRequestBuilder restBuilder;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The policy provider. */
	@Autowired
	private VidPolicyProvider policyProvider;

	/** The security manager. */
	@Autowired
	private IdRepoSecurityManager securityManager;

	/** The Audit Helper. */
	@Autowired
	private AuditHelper auditHelper;

	/** The Uin Hash Salt Repo. */
	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;

	/** The Uin Encrypt Salt Repo. */
	@Autowired
	private UinEncryptSaltRepo uinEncryptSaltRepo;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.idrepository.core.spi.VidService#createVid(java.lang.Object)
	 */
	@Override
	public ResponseWrapper<VidResponseDTO> generateVid(VidRequestDTO vidRequest) throws IdRepoAppException {
		String uin = vidRequest.getUin().toString();
		try {
			Vid vid = generateVid(uin, vidRequest.getVidType());
			VidResponseDTO responseDTO = new VidResponseDTO();
			responseDTO.setVid(Long.parseLong(vid.getVid()));
			responseDTO.setVidStatus(vid.getStatusCode());
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.CREATE_VID,
					securityManager.hash(uin.getBytes()), IdType.VID,
					"Create VID requested for " + vidRequest.getVidType());
			return buildResponse(responseDTO, id.get("create"));
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, CREATE_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, CREATE_VID, e.getMessage());
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.CREATE_VID,
					securityManager.hash(uin.getBytes()), IdType.VID, e.getMessage());
			throw new IdRepoAppException(DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * This method will generate Vid and send back the Vid Object as Response.
	 *
	 * @param uin the uin
	 * @param vidType the vid type
	 * @return the vid
	 * @throws IdRepoAppException the id repo app exception
	 */
	private Vid generateVid(String uin, String vidType) throws IdRepoAppException {
		checkUinStatus(uin);
		Integer moduloValue = env.getProperty(MODULO_VALUE, Integer.class);
		int modResult = (int) (Long.parseLong(uin) % moduloValue);
		String encryptSalt = uinEncryptSaltRepo.retrieveSaltById(modResult);
		String hashSalt = uinHashSaltRepo.retrieveSaltById(modResult);
		String uinToEncrypt = modResult + SPLITTER + uin + SPLITTER + encryptSalt;
		String uinHash = String.valueOf(modResult) + SPLITTER
				+ securityManager.hashwithSalt(uin.getBytes(), CryptoUtil.decodeBase64(hashSalt));
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime();
		List<Vid> vidDetails = vidRepo.findByUinHashAndStatusCodeAndVidTypeCodeAndExpiryDTimesAfter(uinHash,
				env.getProperty(VID_ACTIVE_STATUS), vidType, currentTime);
		VidPolicy policy = policyProvider.getPolicy(vidType);
		if (Objects.isNull(vidDetails) || vidDetails.isEmpty() || vidDetails.size() < policy.getAllowedInstances()) {
			String vidRefId = UUIDUtils
					.getUUID(UUIDUtils.NAMESPACE_OID, uin + SPLITTER + DateUtils.getUTCCurrentDateTime()).toString();
			return vidRepo
					.save(new Vid(vidRefId, generateVid(), uinHash, uinToEncrypt, vidType, currentTime,
							Objects.nonNull(policy.getValidForInMinutes())
									? DateUtils.getUTCCurrentDateTime().plusMinutes(policy.getValidForInMinutes())
									: LocalDateTime.MAX.withYear(9999),
							env.getProperty(VID_ACTIVE_STATUS),
							IdRepoSecurityManager.getUser(), currentTime, null, null, false, null));
		} else {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, CREATE_VID,
					"throwing vid creation failed");
			throw new IdRepoAppException(VID_POLICY_FAILED);
		}
	}

	/**
	 * Generate vid.
	 *
	 * @return the string
	 * @throws IdRepoAppException the id repo app exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String generateVid() throws IdRepoAppException {
		try {
			ResponseWrapper response = restHelper.requestSync(
					restBuilder.buildRequest(RestServicesConstants.VID_GENERATOR_SERVICE, null, ResponseWrapper.class));
			return ((Map<String, String>) response.getResponse()).get(VID);
		} catch (IdRepoDataValidationException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "generateVID",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(INVALID_INPUT_PARAMETER.getErrorCode(), e.getErrorText());
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, CREATE_VID, e.getErrorText());
			throw new IdRepoAppException(VID_GENERATION_FAILED, e);
		}
	}

	/**
	 * Fetch details of the provided uin from Id Repository identity service and
	 * check if the uin is active or not. If not, Exception will be thrown.
	 *
	 * @param uin
	 *            the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private void checkUinStatus(String uin) throws IdRepoAppException {
		try {
			RestRequestDTO request = restBuilder.buildRequest(RestServicesConstants.IDREPO_IDENTITY_SERVICE, null,
					IdResponseDTO.class);
			request.setPathVariables(Collections.singletonMap("uin", uin));
			IdResponseDTO identityResponse = restHelper.requestSync(request);
			String uinStatus = identityResponse.getResponse().getStatus();
			if (!uinStatus.equals(env.getProperty(ACTIVE_STATUS))) {
				throw new IdRepoAppException(INVALID_UIN.getErrorCode(),
						String.format(INVALID_UIN.getErrorMessage(), uinStatus));
			}
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + ExceptionUtils.getStackTrace(e));
			List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(
					e.getResponseBodyAsString().isPresent() ? e.getResponseBodyAsString().get() : null);
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkUinStatus", "\n" + errorList);
			if (Objects.nonNull(errorList) && !errorList.isEmpty()
					&& errorList.get(0).getErrorCode().equals(NO_RECORD_FOUND.getErrorCode())) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkUinStatus",
						"throwing no record found");
				throw new IdRepoAppException(NO_RECORD_FOUND);
			} else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkUinStatus",
						"throwing UIN_RETRIEVAL_FAILED");
				throw new IdRepoAppException(UIN_RETRIEVAL_FAILED);
			}
		} catch (IdRepoDataValidationException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(INVALID_INPUT_PARAMETER.getErrorCode(), e.getErrorText());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idrepository.core.spi.VidService#retrieveUinByVid(java.lang.String)
	 */
	@Override
	public ResponseWrapper<VidResponseDTO> retrieveUinByVid(String vid) throws IdRepoAppException {
		try {
			Vid vidObject = retrieveVidEntity(vid);
			if (vidObject != null) {
				String decryptedUin = decryptUin(vidObject.getUin(), vidObject.getUinHash());
				List<String> uinList = Arrays.asList(decryptedUin.split(SPLITTER));
				checkExpiry(vidObject.getExpiryDTimes());
				checkStatus(vidObject.getStatusCode());
				checkUinStatus(uinList.get(1));
				VidResponseDTO resDTO = new VidResponseDTO();
				resDTO.setUin(Long.parseLong(uinList.get(1)));
				auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.RETRIEVE_VID_UIN,
						securityManager.hash(vid.getBytes()), IdType.VID, "Retrieve Uin By VID requested");
				return buildResponse(resDTO, id.get("read"));
			} else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, RETRIEVE_UIN_BY_VID,
						"throwing NO_RECORD_FOUND_VID");
				throw new IdRepoAppException(NO_RECORD_FOUND);
			}
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, RETRIEVE_UIN_BY_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, RETRIEVE_UIN_BY_VID,
					e.getMessage());
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.RETRIEVE_VID_UIN,
					securityManager.hash(vid.getBytes()), IdType.VID, e.getMessage());
			throw new IdRepoAppException(DATABASE_ACCESS_ERROR, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.idrepository.core.spi.VidService#updateVid(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public ResponseWrapper<VidResponseDTO> updateVid(String vid, VidRequestDTO request) throws IdRepoAppException {
		try {
			String vidStatus = request.getVidStatus();
			Vid vidObject = retrieveVidEntity(vid);
			if (Objects.isNull(vidObject)) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, UPDATE_VID,
						"throwing NO_RECORD_FOUND_VID");
				throw new IdRepoAppException(NO_RECORD_FOUND);
			}
			checkStatus(vidObject.getStatusCode());
			checkExpiry(vidObject.getExpiryDTimes());
			String decryptedUin = decryptUin(vidObject.getUin(), vidObject.getUinHash());
			VidPolicy policy = policyProvider.getPolicy(vidObject.getVidTypeCode());
			VidResponseDTO response = updateVidStatus(vidStatus, vidObject, decryptedUin, policy);
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.UPDATE_VID_STATUS,
					securityManager.hash(vid.getBytes()), IdType.VID, "Update VID requested");
			return buildResponse(response, id.get("update"));
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, UPDATE_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, UPDATE_VID, e.getMessage());
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.UPDATE_VID_STATUS,
					securityManager.hash(vid.getBytes()), IdType.VID, e.getMessage());
			throw new IdRepoAppException(DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * This method will update the status and VidResponseDTO will be sent back as
	 * response.
	 *
	 * @param vidStatus the vid status
	 * @param vidObject the vid object
	 * @param decryptedUin the decrypted uin
	 * @param policy the policy
	 * @return VidResponseDTO
	 * @throws IdRepoAppException the id repo app exception
	 */
	private VidResponseDTO updateVidStatus(String vidStatus, Vid vidObject, String decryptedUin, VidPolicy policy)
			throws IdRepoAppException {
		String uin = Arrays.asList(decryptedUin.split(SPLITTER)).get(1);
		if (!(vidStatus.equals(env.getProperty(VID_UNLIMITED_TRANSACTION_STATUS))
				&& Objects.isNull(policy.getAllowedTransactions()))) {
			vidObject.setStatusCode(vidStatus);
			vidObject.setUpdatedBy(IdRepoSecurityManager.getUser());
			vidObject.setUpdatedDTimes(DateUtils.getUTCCurrentDateTime());
			vidObject.setUin(decryptedUin);
			vidRepo.saveAndFlush(vidObject);
		}
		VidResponseDTO response = new VidResponseDTO();
		response.setVidStatus(vidObject.getStatusCode());
		if (policy.getAutoRestoreAllowed() && policy.getRestoreOnAction().equals(vidStatus)) {
			Vid createVidResponse = generateVid(uin, vidObject.getVidTypeCode());
			VidResponseDTO restoredVidDTO = new VidResponseDTO();
			restoredVidDTO.setVid(Long.valueOf(createVidResponse.getVid()));
			restoredVidDTO.setVidStatus(createVidResponse.getStatusCode());
			response.setRestoredVid(restoredVidDTO);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idrepository.core.spi.VidService#regenerateVid(java.lang.String)
	 */
	@Override
	public ResponseWrapper<VidResponseDTO> regenerateVid(String vid) throws IdRepoAppException {
		try {
			Vid vidObject = retrieveVidEntity(vid);
			if (Objects.isNull(vidObject)) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, REGENERATE_VID,
						"throwing NO_RECORD_FOUND_VID");
				throw new IdRepoAppException(NO_RECORD_FOUND);
			}
			VidPolicy policy = policyProvider.getPolicy(vidObject.getVidTypeCode());
			if (policy.getAutoRestoreAllowed()) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, REGENERATE_VID,
						"throwing Vid Regeneration Failed");
				throw new IdRepoAppException(VID_POLICY_FAILED);
			}
			checkRegenerateStatus(vidObject.getStatusCode());
			String decryptedUin = decryptUin(vidObject.getUin(), vidObject.getUinHash());
			updateVidStatus(VID_REGENERATE_ACTIVE_STATUS, vidObject, decryptedUin, policy);
			List<String> uinList = Arrays.asList(decryptedUin.split(SPLITTER));
			VidResponseDTO response = new VidResponseDTO();
			Vid generateVidObject = generateVid(uinList.get(1), vidObject.getVidTypeCode());
			response.setVid(Long.parseLong(generateVidObject.getVid()));
			response.setVidStatus(generateVidObject.getStatusCode());
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.REGENERATE_VID,
					securityManager.hash(vid.getBytes()), IdType.VID, "Regenerate VID requested");
			return buildResponse(response, id.get("regenerate"));
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, REGENERATE_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, REGENERATE_VID, e.getMessage());
			auditHelper.audit(AuditModules.ID_REPO_VID_SERVICE, AuditEvents.REGENERATE_VID,
					securityManager.hash(vid.getBytes()), IdType.VID, e.getMessage());
			throw new IdRepoAppException(DATABASE_ACCESS_ERROR, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idrepository.core.spi.VidService#deactivateVIDsForUIN(java.lang.
	 * String)
	 */
	@Override
	public ResponseWrapper<VidResponseDTO> deactivateVIDsForUIN(String uin) throws IdRepoAppException {
		return applyVIDStatus(uin, env.getProperty(VID_DEACTIVATED), DEACTIVATE, env.getProperty(VID_ACTIVE_STATUS));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.idrepository.core.spi.VidService#reactivateVIDsForUIN(java.lang.
	 * String)
	 */
	@Override
	public ResponseWrapper<VidResponseDTO> reactivateVIDsForUIN(String uin) throws IdRepoAppException {
		return applyVIDStatus(uin, env.getProperty(VID_ACTIVE_STATUS), REACTIVATE, env.getProperty(VID_DEACTIVATED));
	}

	/**
	 * Apply VID status.
	 *
	 * @param uin the uin
	 * @param status the status
	 * @param idType the id type
	 * @param vidStatusToRetrieveVIDList the vid status to retrieve VID list
	 * @return the response wrapper
	 * @throws IdRepoAppException the id repo app exception
	 */
	private ResponseWrapper<VidResponseDTO> applyVIDStatus(String uin, String status, String idType,
			String vidStatusToRetrieveVIDList) throws IdRepoAppException {
		Integer moduloValue = env.getProperty(MODULO_VALUE, Integer.class);
		String hashSalt = uinHashSaltRepo.retrieveSaltById((int) (Long.parseLong(uin) % moduloValue));
		String uinHash = String.valueOf((Long.parseLong(uin) % moduloValue)) + SPLITTER
				+ securityManager.hashwithSalt(uin.getBytes(), CryptoUtil.decodeBase64(hashSalt));
		List<Vid> vidList = vidRepo.findByUinHashAndStatusCodeAndExpiryDTimesAfter(uinHash, vidStatusToRetrieveVIDList,
				DateUtils.getUTCCurrentDateTime());
		if (!vidList.isEmpty()) {
			String decryptedUin = decryptUin(vidList.get(0).getUin(), uinHash);
			vidList.forEach(vid -> {
				vid.setStatusCode(status);
				vid.setUpdatedBy(IdRepoSecurityManager.getUser());
				vid.setUpdatedDTimes(DateUtils.getUTCCurrentDateTime());
				vid.setUin(decryptedUin);
			});
			vidRepo.saveAll(vidList);
			VidResponseDTO response = new VidResponseDTO();
			response.setVidStatus(status);
			return buildResponse(response, id.get(idType));
		} else {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "deactivateVIDsForUIN",
					"throwing NO_RECORD_FOUND_VID");
			throw new IdRepoAppException(NO_RECORD_FOUND);
		}
	}

	/**
	 * This method will verify the status of vid from the allowed status available.
	 *
	 * @param statusCode the status code
	 * @throws IdRepoAppException the id repo app exception
	 */
	private void checkRegenerateStatus(String statusCode) throws IdRepoAppException {
		String allowedStatus = env.getProperty(VID_REGENERATE_ALLOWED_STATUS);
		List<String> allowedStatusList = Arrays.asList(allowedStatus.split(","));
		if (!allowedStatusList.contains(statusCode)) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkRegenerateStatus",
					"throwing " + statusCode + " VID");
			throw new IdRepoAppException(INVALID_VID.getErrorCode(),
					String.format(INVALID_VID.getErrorMessage(), statusCode));
		}
	}

	/**
	 * This Method will accepts vid as parameter and will return Vid Object from DB.
	 *
	 * @param vid
	 *            the vid
	 * @return The Vid Object
	 */
	private Vid retrieveVidEntity(String vid) {
		return vidRepo.findByVid(vid);
	}

	/**
	 * This method will check expiry date of the vid, if vid is expired then it will
	 * throw IdRepoAppException.
	 *
	 * @param expiryDTimes
	 *            the expiry D times
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private void checkExpiry(LocalDateTime expiryDTimes) throws IdRepoAppException {
		if (!DateUtils.after(expiryDTimes, DateUtils.getUTCCurrentDateTime())) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkExpiry",
					"throwing Expired VID");
			throw new IdRepoAppException(INVALID_VID.getErrorCode(),
					String.format(INVALID_VID.getErrorMessage(), EXPIRED));
		}
	}

	/**
	 * This method will check Status of the vid.
	 *
	 * @param statusCode
	 *            the status code
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private void checkStatus(String statusCode) throws IdRepoAppException {
		if (!statusCode.equalsIgnoreCase(env.getProperty(VID_ACTIVE_STATUS))) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_VID_SERVICE, "checkStatus",
					"throwing INVALID_VID with status - " + statusCode);
			throw new IdRepoAppException(INVALID_VID.getErrorCode(),
					String.format(INVALID_VID.getErrorMessage(), statusCode));
		}
	}

	/**
	 * This Method is used to decrypt the UIN stored in DB.
	 *
	 * @param uin the uin
	 * @param uinHash the uin hash
	 * @return the string
	 * @throws IdRepoAppException             the id repo app exception
	 */
	private String decryptUin(String uin, String uinHash) throws IdRepoAppException {
		List<String> uinDetails = Arrays.stream(uin.split(SPLITTER))
				.collect(Collectors.toList());
		String decryptSalt = uinEncryptSaltRepo.retrieveSaltById(Integer.parseInt(uinDetails.get(0)));
		String hashSalt = uinHashSaltRepo.retrieveSaltById(Integer.parseInt(uinDetails.get(0)));
		String encryptedUin = uin.substring(uinDetails.get(0).length() + 1, uin.length());
		String decryptedUin = new String(securityManager.decryptWithSalt(CryptoUtil.decodeBase64(encryptedUin),
				CryptoUtil.decodeBase64(decryptSalt)));
		String uinHashWithSalt = uinDetails.get(0) + SPLITTER
				+ securityManager.hashwithSalt(decryptedUin.getBytes(), CryptoUtil.decodeBase64(hashSalt));
		if (!MessageDigest.isEqual(uinHashWithSalt.getBytes(), uinHash.getBytes())) {
			throw new IdRepoAppUncheckedException(UIN_HASH_MISMATCH);
		}
		return uinDetails.get(0) + SPLITTER + decryptedUin + SPLITTER + decryptSalt;
	}

	/**
	 * This Method will build the Vid Response.
	 *
	 * @param response
	 *            the response
	 * @param id
	 *            the id
	 * @return The Vid Response
	 */
	private ResponseWrapper<VidResponseDTO> buildResponse(VidResponseDTO response, String id) {
		ResponseWrapper<VidResponseDTO> responseDto = new ResponseWrapper<>();
		responseDto.setId(id);
		responseDto.setVersion(env.getProperty(APPLICATION_VERSION_VID));
		responseDto.setResponse(response);
		return responseDto;
	}
}

package io.mosip.idrepository.vid.service.impl;

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
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
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
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;
import io.mosip.kernel.idgenerator.vid.exception.VidException;

/**
 * The Class VidServiceImpl.
 *
 * @author Manoj SP
 * @author Prem Kumar
 */
@Component
@Transactional
public class VidServiceImpl implements VidService<VidRequestDTO, ResponseWrapper<VidResponseDTO>> {

	private static final String REGENERATE_VID = "regenerateVid";

	private static final String UPDATE_VID = "updateVid";

	private static final String CREATE_VID = "createVid";

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

	/** The vid generator. */
	@Autowired
	private VidGenerator<String> vidGenerator;

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

	@Autowired
	private AuditHelper auditHelper;

	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;

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
	public ResponseWrapper<VidResponseDTO> createVid(VidRequestDTO vidRequest) throws IdRepoAppException {
		try {
			String uin = vidRequest.getUin().toString();
			Vid vid = generateVid(uin, vidRequest.getVidType());
			VidResponseDTO responseDTO = new VidResponseDTO();
			responseDTO.setVid(Long.parseLong(vid.getVid()));
			responseDTO.setVidStatus(vid.getStatusCode());
			return buildResponse(responseDTO, id.get("create"));
		} catch (VidException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, CREATE_VID, e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.VID_GENERATION_FAILED, e);
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, CREATE_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, CREATE_VID, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		} finally {
			auditHelper.audit(AuditModules.CREATE_VID, AuditEvents.CREATE_VID_REQUEST_RESPONSE,
					vidRequest.getUin().toString(), "Create VID requested");
		}
	}

	private Vid generateVid(String uin, String vidType) throws IdRepoAppException {
		checkUinStatus(uin);
		Integer moduloValue = env.getProperty(IdRepoConstants.MODULO_VALUE.getValue(), Integer.class);
		int modResult = (int) (Long.parseLong(uin) % moduloValue);
		String encryptSalt = uinEncryptSaltRepo.retrieveSaltById(modResult);
		String hashSalt = uinHashSaltRepo.retrieveSaltById(modResult);
		String uinToEncrypt = modResult + "_" + uin + "_" + encryptSalt;
		String uinHash = String.valueOf(modResult) + "_"
				+ securityManager.hashwithSalt(uin.getBytes(), CryptoUtil.decodeBase64(hashSalt));
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime();
		List<Vid> vidDetails = vidRepo.findByUinHashAndStatusCodeAndVidTypeCodeAndExpiryDTimesAfter(uinHash,
				env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()), vidType, currentTime);
		VidPolicy policy = policyProvider.getPolicy(vidType);
		if (Objects.isNull(vidDetails) || vidDetails.isEmpty() || vidDetails.size() < policy.getAllowedInstances()) {
			String vidRefId = UUIDUtils
					.getUUID(UUIDUtils.NAMESPACE_OID,
							uin + "_" + DateUtils.getUTCCurrentDateTime())
					.toString();
			return vidRepo.save(new Vid(vidRefId, vidGenerator.generateId(), uinHash, uinToEncrypt, vidType, currentTime,
					Objects.nonNull(policy.getValidForInMinutes())
							? DateUtils.getUTCCurrentDateTime().plusMinutes(policy.getValidForInMinutes())
							: LocalDateTime.MAX.withYear(9999),
					env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()), "createdBy", currentTime,
					"updatedBy", currentTime, false, currentTime));
		} else {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, CREATE_VID, "throwing vid creation failed");
			throw new IdRepoAppException(IdRepoErrorConstants.VID_POLICY_FAILED);
		}
	}

	/**
	 * Check uin status.
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
			if (!uinStatus.equals(env.getProperty(IdRepoConstants.ACTIVE_STATUS.getValue()))) {
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), uinStatus));
			}
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + ExceptionUtils.getStackTrace(e));
			List<ServiceError> errorList = ExceptionUtils.getServiceErrorList(
					e.getResponseBodyAsString().isPresent() ? e.getResponseBodyAsString().get() : null);
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus", "\n" + errorList);
			if (Objects.nonNull(errorList) && !errorList.isEmpty()
					&& errorList.get(0).getErrorCode().equals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode())) {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
						"throwing no record found");
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND);
			} else {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
						"throwing UIN_RETRIEVAL_FAILED");
				throw new IdRepoAppException(IdRepoErrorConstants.UIN_RETRIEVAL_FAILED);
			}
		} catch (IdRepoDataValidationException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkUinStatus",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), e.getErrorText());
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
				String decryptedUin = decryptUin(vidObject.getUin(),vidObject.getUinHash());
				List<String> uinList = Arrays.asList(decryptedUin.split("_"));
				checkExpiry(vidObject.getExpiryDTimes());
				checkStatus(vidObject.getStatusCode());
				checkUinStatus(uinList.get(1));
				VidResponseDTO resDTO = new VidResponseDTO();
				resDTO.setUin(Long.parseLong(uinList.get(1)));
				return buildResponse(resDTO, id.get("read"));
			} else {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, RETRIEVE_UIN_BY_VID,
						"throwing NO_RECORD_FOUND_VID");
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND);
			}
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, RETRIEVE_UIN_BY_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, RETRIEVE_UIN_BY_VID, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		} finally {
			auditHelper.audit(AuditModules.CREATE_VID, AuditEvents.RETRIEVE_UIN_BY_VID_REQUEST_RESPONSE, vid,
					"Retrieve Uin By VID requested");
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
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, UPDATE_VID,
						"throwing NO_RECORD_FOUND_VID");
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND);
			}
			checkStatus(vidObject.getStatusCode());
			checkExpiry(vidObject.getExpiryDTimes());
			String decryptedUin = decryptUin(vidObject.getUin(),vidObject.getUinHash());
			VidPolicy policy = policyProvider.getPolicy(vidObject.getVidTypeCode());
			VidResponseDTO response = updateVidStatus(vidStatus, vidObject, decryptedUin, policy);
			return buildResponse(response, id.get("update"));
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, UPDATE_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, UPDATE_VID, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		} finally {
			auditHelper.audit(AuditModules.UPDATE_VID, AuditEvents.UPDATE_VID_REQUEST_RESPONSE, vid,
					"Update VID requested");
		}
	}

	private VidResponseDTO updateVidStatus(String vidStatus, Vid vidObject, String decryptedUin, VidPolicy policy)
			throws IdRepoAppException {
		String uin = Arrays.asList(decryptedUin.split("_")).get(1);
		if (!(vidStatus.equals(env.getProperty(IdRepoConstants.VID_UNLIMITED_TRANSACTION_STATUS.getValue()))
				&& Objects.isNull(policy.getAllowedTransactions()))) {
			vidObject.setStatusCode(vidStatus);
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

	@Override
	public ResponseWrapper<VidResponseDTO> regenerateVid(String vid) throws IdRepoAppException {
		try {
			Vid vidObject = retrieveVidEntity(vid);
			if (Objects.isNull(vidObject)) {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, REGENERATE_VID,
						"throwing NO_RECORD_FOUND_VID");
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND);
			}
			VidPolicy policy = policyProvider.getPolicy(vidObject.getVidTypeCode());
			if (policy.getAutoRestoreAllowed()) {
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, REGENERATE_VID,
						"throwing Vid Regeneration Failed");
				throw new IdRepoAppException(IdRepoErrorConstants.VID_POLICY_FAILED);
			}
			checkRegenerateStatus(vidObject.getStatusCode());
			String decryptedUin = decryptUin(vidObject.getUin(),vidObject.getUinHash());
			updateVidStatus(IdRepoConstants.VID_REGENERATE_ACTIVE_STATUS.getValue(), vidObject, decryptedUin, policy);
			List<String> uinList = Arrays.asList(decryptedUin.split("_"));
			VidResponseDTO response = new VidResponseDTO();
			Vid generateVidObject = generateVid(uinList.get(1), vidObject.getVidTypeCode());
			response.setVid(Long.parseLong(generateVidObject.getVid()));
			response.setVidStatus(generateVidObject.getStatusCode());
			return buildResponse(response, id.get("regenerate"));
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(IdRepoLogger.getVid(), ID_REPO_VID_SERVICE, REGENERATE_VID,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			mosipLogger.error(IdRepoLogger.getVid(), ID_REPO_VID_SERVICE, REGENERATE_VID, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		} finally {
			auditHelper.audit(AuditModules.REGENERATE_VID, AuditEvents.REGENERATE_VID_REQUEST_RESPONSE, vid,
					"Regenerate VID requested");
		}
	}

	private void checkRegenerateStatus(String statusCode) throws IdRepoAppException {
		String allowedStatus = env.getProperty(IdRepoConstants.VID_REGENERATE_ALLOWED_STATUS.getValue());
		List<String> allowedStatusList = Arrays.asList(allowedStatus.split(","));
		if (!allowedStatusList.contains(statusCode)) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkRegenerateStatus",
					"throwing " + statusCode + " VID");
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), statusCode));
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
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkExpiry", "throwing Expired VID");
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), EXPIRED));
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
		if (!statusCode.equalsIgnoreCase(env.getProperty(IdRepoConstants.VID_ACTIVE_STATUS.getValue()))) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_VID_SERVICE, "checkStatus",
					"throwing INVALID_VID with status - " + statusCode);
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_VID.getErrorMessage(), statusCode));
		}
	}
	

	/**
	 * This Method is used to decrypt the UIN stored in DB
	 *
	 * @param vidObject
	 *            the vid object
	 * @return
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private String decryptUin(String uin,String uinHash) throws IdRepoAppException {
		List<String> uinDetails = Arrays.stream(uin.split("_")).collect(Collectors.toList());
		String decryptSalt = uinEncryptSaltRepo.retrieveSaltById(Integer.parseInt(uinDetails.get(0)));
		String hashSalt = uinHashSaltRepo.retrieveSaltById(Integer.parseInt(uinDetails.get(0)));
		String encryptedUin = uin.substring(uinDetails.get(0).length() + 1, uin.length());
		String decryptedUin = new String(securityManager.decryptWithSalt(CryptoUtil.decodeBase64(encryptedUin),
				CryptoUtil.decodeBase64(decryptSalt)));
		String uinHashWithSalt = uinDetails.get(0) + "_"
				+ securityManager.hashwithSalt(decryptedUin.getBytes(), CryptoUtil.decodeBase64(hashSalt));
		if (!MessageDigest.isEqual(uinHashWithSalt.getBytes(), uinHash.getBytes())) {
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.UIN_HASH_MISMATCH);
		}
		return uinDetails.get(0) + "_" + decryptedUin + "_" + decryptSalt;
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
		responseDto.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
		responseDto.setResponse(response);
		return responseDto;
	}

}

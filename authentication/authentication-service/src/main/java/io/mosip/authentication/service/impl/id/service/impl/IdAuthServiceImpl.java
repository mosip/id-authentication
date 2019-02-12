package io.mosip.authentication.service.impl.id.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.authentication.service.repository.VIDRepository;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The class validates the UIN and VID.
 *
 * @author Arun Bose
 * @author Rakesh Roshan
 */
@Service
public class IdAuthServiceImpl implements IdAuthService<AutnTxn> {

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The env. */
	@Autowired
	private Environment env;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdAuthServiceImpl.class);

	/** The rest factory. */
	@Autowired
	private RestRequestFactory restFactory;

	/** The audit factory. */
	@Autowired
	private AuditRequestFactory auditFactory;

	/** The vid repository. */
	@Autowired
	private VIDRepository vidRepository;

	@Autowired
	private IdRepoService idRepoService;

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
	public Map<String, Object> getIdRepoByUIN(String uin, boolean isBio) throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = idRepoService.getIdenity(uin, isBio);
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
	public Map<String, Object> getIdRepoByVID(String vid, boolean isBio) throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = getIdRepoByVidAsRequest(vid, isBio);
		auditData();
		return idRepo;
	}

	/**
	 * Do validate VID entity and checks for the expiry date.
	 *
	 * @param vid
	 *            the vid
	 * @return the string
	 * @throws IdValidationFailedException
	 *             the id validation failed exception
	 */
	Map<String, Object> getIdRepoByVidAsRequest(String vid, boolean isBio) throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = null;

		Optional<String> findUinByRefId = vidRepository.findUinByVid(vid);
		if (findUinByRefId.isPresent()) {
			String uin = findUinByRefId.get().trim();
			try {
				idRepo = idRepoService.getIdenity(uin, isBio);
			} catch (IdAuthenticationBusinessException e) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
			}
		}

		return idRepo;
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param idvIdType
	 *            idType
	 * @param idvId
	 *            id-number
	 * @param isBio
	 * @return map map
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Override
	public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio)
			throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = null;
		if (idvIdType.equals(IdType.UIN.getType())) {
			try {
				idResDTO = getIdRepoByUIN(idvId, isBio);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(null, null, e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
			}
		} else {
			try {
				idResDTO = getIdRepoByVID(idvId, isBio);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(null, null, null, e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}
		}

		return idResDTO;
	}

	/**
	 * Store entry in Auth_txn table for all authentications.
	 *
	 * @param idvId
	 *            idvId
	 * @param idvIdType
	 *            idvIdType(D/V)
	 * @param reqTime
	 *            reqTime
	 * @param txnId
	 *            txnId
	 * @param status
	 *            status('Y'/'N')
	 * @param comment
	 *            comment
	 * @param requestType
	 *            requestType(OTP_REQUEST,OTP_AUTH,DEMO_AUTH,BIO_AUTH)
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public void saveAutnTxn(AutnTxn authTxn) throws IdAuthenticationBusinessException {
		autntxnrepository.saveAndFlush(authTxn);
	}

	/**
	 * Audit data.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void auditData() throws IdAuthenticationBusinessException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH,
				AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");

		RestRequestDTO restRequest;
		try {
			restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
					AuditResponseDto.class);
		} catch (IDDataValidationException e) {
			logger.error(DEFAULT_SESSION_ID, null, null, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
		}

		restHelper.requestAsync(restRequest);
	}

}

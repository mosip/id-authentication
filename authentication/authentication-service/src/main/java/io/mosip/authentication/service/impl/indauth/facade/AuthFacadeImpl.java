/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * This class provides the implementation of AuthFacade.
 *
 * @author Arun Bose
 */
@Service
public class AuthFacadeImpl implements AuthFacade {

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The logger. */
	private static MosipLogger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpService;

	/** The demo auth service. */
	@Autowired
	private DemoAuthService demoAuthService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/**
	 * Process the authorisation type and authorisation response is returned.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */

	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO)
			throws IdAuthenticationBusinessException {
		String refId = processIdType(authRequestDTO);
		List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, refId);

		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance();
		authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIndIdType())
				.setReqTime(authRequestDTO.getReqTime()).setVersion(authRequestDTO.getVer());

		authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);

		auditData();
		AuthResponseDTO authResponseDTO = authResponseBuilder.build();
		logger.info(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE,
				"authenticateApplicant status : " + authResponseDTO.isStatus());
		return authResponseDTO;

	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param refId          the ref id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO, String refId)
			throws IdAuthenticationBusinessException {
		List<AuthStatusInfo> authStatusList = new ArrayList<>();

		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus = otpService.validateOtp(authRequestDTO, refId);
			authStatusList.add(otpValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE, "OTP Authentication status : " + otpValidationStatus);
		}

		if (authRequestDTO.getAuthType().isPersonalIdentity() || authRequestDTO.getAuthType().isAddress()
				|| authRequestDTO.getAuthType().isFullAddress()) {
			AuthStatusInfo demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, refId);
			authStatusList.add(demoValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE,
					"Demographic Authentication status : " + demoValidationStatus);
		}
		// TODO Update audit details
		auditData();
		return authStatusList;
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */

	public String processIdType(AuthRequestDTO authRequestDTO) throws IdAuthenticationBusinessException {
		String refId = null;
		String reqType = authRequestDTO.getIndIdType();
		if (reqType.equals(IdType.UIN.getType())) {
			try {
				refId = idAuthService.validateUIN(authRequestDTO.getIndId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, null, e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
			}
		} else {
			try {
				refId = idAuthService.validateVID(authRequestDTO.getIndId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, null, e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}
		}

		auditData();
		return refId;
	}

	/**
	 * Audit data.
	 */
	private void auditData() {
		// TODO Update audit details
	}
}

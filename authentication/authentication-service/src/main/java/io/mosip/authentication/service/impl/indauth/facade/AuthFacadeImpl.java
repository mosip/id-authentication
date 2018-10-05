/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.spi.idauth.service.IdAuthService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * This class provides the implementation of AuthFacade
 * @author Arun Bose
 */
@Service
public class AuthFacadeImpl implements AuthFacade {
	
	private static final String DEFAULT_SESSION_ID = "sessionId";

	@Autowired
	RestHelper restHelper;
	
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private OTPAuthService otpService;

	@Autowired
	private IdAuthService idAuthService;
	
	@Autowired
	private RestRequestFactory  restFactory;
	
	@Autowired
	private AuditRequestFactory auditFactory;

	/**
	 * Process the authorisation type and authorisation response is returned
	 * 
	 * @param authRequestDTO
	 * @throws IdAuthenticationBusinessException
	 */
	
	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO)
			throws IdAuthenticationBusinessException {
		String refId = processIdType(authRequestDTO);
		boolean authFlag = processAuthType(authRequestDTO, refId);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setTxnID(authRequestDTO.getTxnID());
		authResponseDTO.setResTime(new Date());
		authResponseDTO.setStatus(authFlag);
		logger.info(DEFAULT_SESSION_ID, "IDA", "AuthFacade","authenticateApplicant status : " + authFlag); //FIXME

		auditData(); 
		return authResponseDTO;
	    

	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 * 
	 * @param authRequestDTO
	 * @param refId
	 * @throws IdAuthenticationBusinessException
	 */
	public boolean processAuthType(AuthRequestDTO authRequestDTO, String refId)
			throws IdAuthenticationBusinessException {
		boolean authStatus = false;

		if (authRequestDTO.getAuthType().isOtp()) {
			authStatus = otpService.validateOtp(authRequestDTO, refId);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", "AuthFacade","authenticateApplicant status : " + authStatus);
		}
		//TODO Update audit details
		auditData();  
		return authStatus;
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 * 
	 *
	 * @param authRequestDTO
	 * @throws IdAuthenticationBusinessException
	 * @throws IdValidationFailedException
	 * 
	 * 
	 */

	public String processIdType(AuthRequestDTO authRequestDTO) throws IdAuthenticationBusinessException {
		String refId = null;
		String reqType = authRequestDTO.getIdType();
		if (reqType.equals(IdType.UIN.getType())) {
			try {
				refId = idAuthService.validateUIN(authRequestDTO.getId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, null, e.getErrorText()); //FIX ME
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
			}
		} else {

			try {
				refId = idAuthService.validateVID(authRequestDTO.getId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, null, e.getErrorText());//FIX ME
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}
		}
		//TODO Update audit details
		auditData();
		return refId;
	}
 
	private void auditData() throws IdAuthenticationBusinessException {
		AuditRequestDto auditRequest = auditFactory.buildRequest("moduleId", "description");

		RestRequestDTO restRequest;
		try {
			restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
					AuditResponseDto.class);
		} catch (IDDataValidationException e) {
			logger.error(DEFAULT_SESSION_ID, null, null, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN,	e);
		}

		restHelper.requestAsync(restRequest);
	}
}

/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.spi.idauth.service.IdAuthService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
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
	private DemoAuthService demoAuthService;

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
		List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, refId);
		
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance();
		authResponseBuilder.setTxnID(authRequestDTO.getTxnID());
		
		authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);

		auditData(); 
		AuthResponseDTO authResponseDTO = authResponseBuilder.build();
		logger.info(DEFAULT_SESSION_ID, "IDA", "AuthFacade","authenticateApplicant status : " + authResponseDTO.isStatus()); //FIXME
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
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO, String refId)
			throws IdAuthenticationBusinessException {
		List<AuthStatusInfo> authStatusList = new ArrayList<>();

		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus = otpService.validateOtp(authRequestDTO, refId);
			authStatusList.add(otpValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", "AuthFacade","OTP Authentication status : " + otpValidationStatus.isStatus());
		}
		
		if (authRequestDTO.getAuthType().isPi() || authRequestDTO.getAuthType().isAd() || authRequestDTO.getAuthType().isFad()) {
			AuthStatusInfo demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, refId);
			authStatusList.add(demoValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", "AuthFacade","Demographic Authentication status : " + demoValidationStatus.isStatus());
		}
		//TODO Update audit details
		auditData();  
		return authStatusList;
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
		AuditRequestDto auditRequest = auditFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "desc");

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

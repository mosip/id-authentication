package io.mosip.authentication.service.impl.id.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.entity.UinEntity;
import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.repository.UinRepository;
import io.mosip.authentication.service.repository.VIDRepository;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The class validates the UIN and VID.
 *
 * @author Arun Bose
 */
@Service
public class IdAuthServiceImpl implements IdAuthService {

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

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

	/** The uin repository. */
	@Autowired
	private UinRepository uinRepository;

	/** The vid repository. */
	@Autowired
	private VIDRepository vidRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateUIN(java.lang.
	 * String)
	 */
	public String validateUIN(String uin) throws IdAuthenticationBusinessException {
		String refId = null;
//		Optional<UinEntity> uinEntityOpt = uinRepository.findById(uin);
//		if (uinEntityOpt.isPresent()) {
//			UinEntity uinEntity = uinEntityOpt.get();
//			if (uinEntity.isActive()) {
//				refId = uinEntity.getUinRefId();
//			} else {
//				// TODO log error
//				throw new IdValidationFailedException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
//			}
//		} else {
//			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN);
//		}

		// TODO Update audit details
		auditData();

//		return refId;
		return "1234567890";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.auth.core.spi.idauth.service.IdAuthService#validateVID(java.lang.
	 * String)
	 */
	public String validateVID(String vid) throws IdAuthenticationBusinessException {
		String refId = doValidateVIDEntity(vid);

		auditData();

		return refId;
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
	private String doValidateVIDEntity(String vid) throws IdValidationFailedException {
		Optional<VIDEntity> vidEntityOpt = vidRepository.findById(vid);
		if (!vidEntityOpt.isPresent()) {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_VID);
		} 
		VIDEntity vidEntity = vidEntityOpt.get();
		if (!vidEntity.isActive()) {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INACTIVE_VID);
		}
		
		Date currentDate = new Date();
		if (!currentDate.before(vidEntity.getExpiryDate())) {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.EXPIRED_VID);
		}
		
		String refId = vidEntity.getRefId();
//		Optional<UinEntity> uinEntityOpt = uinRepository.findByUinRefId(refId);
//		if (!uinEntityOpt.isPresent()) {
//			throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN);
//		}
		
		
//		doValidateUIN(uinEntityOpt.get());
	
		return refId;
	}

	/**
	 * Do validate UIN and checks whether it is active.
	 *
	 * @param uinEntityOpt
	 *            the uin entity opt
	 * @throws IdValidationFailedException
	 *             the id validation failed exception
	 */
	private static void doValidateUIN(UinEntity uinEntity) throws IdValidationFailedException {
		if (!uinEntity.isActive()) {
			throw new IdValidationFailedException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
		}
	}

}

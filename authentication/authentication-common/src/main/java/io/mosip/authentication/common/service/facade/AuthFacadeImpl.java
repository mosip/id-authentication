/*
 * 
 */
package io.mosip.authentication.common.service.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthResponseBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class provides the implementation of AuthFacade, provides the
 * authentication for individual by calling the respective Service
 * Classes{@link AuthFacade}.
 *
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */
@Service
public class AuthFacadeImpl implements AuthFacade {

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpService;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The Environment */
	@Autowired
	private Environment env;

	/** The Id Info Service */
	@Autowired
	private IdService<AutnTxn> idInfoService;

	/** The Demo Auth Service */
	@Autowired
	private DemoAuthService demoAuthService;

	/** The BioAuthService */
	@Autowired
	private BioAuthService bioAuthService;

	/** The NotificationService */
	@Autowired
	private NotificationService notificationService;

	/** The Pin Auth Service */
	@Autowired
	private PinAuthService pinAuthService;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	/** The Id Info Fetcher */
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.AuthFacade#
	 * authenticateApplicant(io.mosip.authentication.core.dto.indauth.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequestDTO, boolean isAuth, String partnerId)
			throws IdAuthenticationBusinessException {

		IdType idType = idInfoFetcher.getUinOrVidType(authRequestDTO);
		Optional<String> idvid = idInfoFetcher.getUinOrVid(authRequestDTO);
		String idvIdType = idType.getType();
		Map<String, Object> idResDTO = idAuthService.processIdType(idvIdType, idvid.orElse(""),
				authRequestDTO.getRequestedAuth().isBio());

		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder
				.newInstance(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String uin = String.valueOf(idResDTO.get("uin"));
		String staticTokenId = null;
		Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE, Boolean.class);
		try {
			idInfo = idInfoService.getIdInfo(idResDTO);
			authResponseBuilder.setTxnID(authRequestDTO.getTransactionID());
			staticTokenId = staticTokenRequired ? tokenIdManager.generateTokenId(uin, partnerId) : "";
			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, uin, isAuth, staticTokenId,
					partnerId);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			// Set static token
			if (staticTokenRequired) {
				authResponseDTO = authResponseBuilder.build(staticTokenId);
			} else {
				authResponseDTO = authResponseBuilder.build(null);
			}
			logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
					AUTH_FACADE, "authenticateApplicant status : " + authResponseDTO.getResponse().isAuthStatus());
		}

		if (idInfo != null && uin != null) {
			notificationService.sendAuthNotification(authRequestDTO, uin, authResponseDTO, idInfo, isAuth);
		}

		return authResponseDTO;

	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo         list of identityInfoDto request
	 * @param uin            the uin
	 * @param isAuth         the is auth
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String uin, boolean isAuth, String staticTokenId,
			String partnerId) throws IdAuthenticationBusinessException {

		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = null;

		if (idInfoFetcher.getUinOrVidType(authRequestDTO).getType().equals(IdType.UIN.getType())) {
			idType = IdType.UIN;
		} else {
			idType = IdType.VID;
		}

		processOTPAuth(authRequestDTO, uin, isAuth, authStatusList, idType, staticTokenId, partnerId);

		processDemoAuth(authRequestDTO, idInfo, uin, isAuth, authStatusList, idType, staticTokenId, partnerId);

		processBioAuth(authRequestDTO, idInfo, uin, isAuth, authStatusList, idType, staticTokenId, partnerId);

		processPinAuth(authRequestDTO, uin, isAuth, authStatusList, idType, staticTokenId, partnerId);

		return authStatusList;
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type.
	 * 
	 * @param authRequestDTO
	 * @param uin
	 * @param isAuth
	 * @param authStatusList
	 * @param idType
	 * @throws IdAuthenticationBusinessException
	 */
	private void processPinAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isPin()) {
			AuthStatusInfo pinValidationStatus;
			pinValidationStatus = pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), partnerId);
			authStatusList.add(pinValidationStatus);
		}
	}

	/**
	 * process the BioAuth
	 * 
	 * @param authRequestDTO
	 * @param idInfo
	 * @param isAuth
	 * @param authStatusList
	 * @param idType
	 * @throws IdAuthenticationBusinessException
	 */
	private void processBioAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isBio()) {
			AuthStatusInfo bioValidationStatus;
			bioValidationStatus = bioAuthService.authenticate(authRequestDTO, uin, idInfo, partnerId);
			authStatusList.add(bioValidationStatus);
		}
	}

	/**
	 * Process demo auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo         the id info
	 * @param uin            the uin
	 * @param isAuth         the is auth
	 * @param authStatusList the auth status list
	 * @param idType         the id type
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isDemo()) {
			AuthStatusInfo demoValidationStatus;
			demoValidationStatus = demoAuthService.authenticate(authRequestDTO, uin, idInfo, partnerId);
			authStatusList.add(demoValidationStatus);
		}
	}

	/**
	 * Process OTP auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin            the uin
	 * @param isAuth         the is auth
	 * @param authStatusList the auth status list
	 * @param idType         the id type
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isOtp()) {
			AuthStatusInfo otpValidationStatus;
			try {
				otpValidationStatus = otpService.authenticate(authRequestDTO, uin, Collections.emptyMap(), partnerId);
				authStatusList.add(otpValidationStatus);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(),
						e.getErrorText());
				otpValidationStatus = new AuthStatusInfo();
				otpValidationStatus.setStatus(false);
				AuthError authError;
				if (e.getActionMessage() != null) {
					authError = new ActionableAuthError(e.getErrorCode(), e.getErrorText(), e.getActionMessage());
				} else {
					authError = new AuthError(e.getErrorCode(), e.getErrorText());
				}
				otpValidationStatus.setErr(Collections.singletonList(authError));
				authStatusList.add(otpValidationStatus);
			}

		}
	}

}

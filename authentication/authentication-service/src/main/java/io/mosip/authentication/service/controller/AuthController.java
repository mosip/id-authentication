package io.mosip.authentication.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests.
 *
 * @author Arun Bose
 * @author Prem Kumar
 */
@RestController
public class AuthController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(AuthController.class);

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The Environment */
	@Autowired
	private Environment env;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("authRequestDTO")
	private void initAuthRequestBinder(WebDataBinder binder) {
		binder.setValidator(authRequestValidator);
	}

	/**
	 * authenticateRequest - method to authenticate request.
	 *
	 * @param authrequestdto - Authenticate Request
	 * @param errors         the errors
	 * @return authResponsedto AuthResponseDTO
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 * @throws IdAuthenticationBusinessException
	 */
	@PostMapping(path = "/{Auth-Partner-ID}/{MISP-LK}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public AuthResponseDTO authenticateIndividual(@Validated @RequestBody AuthRequestDTO authrequestdto,
			@ApiIgnore Errors errors, @PathVariable("Auth-Partner-ID") String partnerId,
			@PathVariable("MISP-LK") String mispLK)
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {
		AuthResponseDTO authResponsedto = null;
		AuthStatusInfo statusInfo = null;
		try {
			DataValidationUtil.validate(errors);
			authResponsedto = authFacade.authenticateIndividual(authrequestdto, true, partnerId);
		} catch (IDDataValidationException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					"authenticateApplication", e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					"authenticateApplication", e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} finally {
			AuthTypeDTO requestedAuthType = authrequestdto.getRequestedAuth();
			if (requestedAuthType != null) {
				boolean isStatus = authResponsedto != null && authResponsedto.getResponse() != null
						&& authResponsedto.getResponse().isAuthStatus();
				String uin = authrequestdto.getIndividualId();
				Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE,
						Boolean.class);
				String staticTokenId = staticTokenRequired ? tokenIdManager.generateTokenId(uin, partnerId) : "";
				String idType = authrequestdto.getIndividualIdType();
				IdType actualidType = null;
				if (idType != null && !idType.isEmpty()) {
					actualidType = idType.equalsIgnoreCase(IdType.UIN.getType()) ? IdType.UIN : IdType.VID;
					if (requestedAuthType.isOtp()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"OTP Authentication status : " + isStatus);
						auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
								auditHelper.getUinorVid(authrequestdto), actualidType, AuditModules.OTP_AUTH.getDesc());
						AutnTxn authTxn = auditHelper.createAuthTxn(authrequestdto, uin, RequestType.OTP_AUTH,
								staticTokenId, isStatus);
						idAuthService.saveAutnTxn(authTxn);
					}
					if (requestedAuthType.isPin()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"Pin Authentication  status :" + isStatus);
						auditHelper.audit(AuditModules.PIN_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
								auditHelper.getUinorVid(authrequestdto),
								authrequestdto.getIndividualIdType().equalsIgnoreCase(IdType.UIN.getType()) ? IdType.UIN
										: IdType.VID,
								AuditModules.PIN_AUTH.getDesc());
						AutnTxn authtxn = auditHelper.createAuthTxn(authrequestdto, uin, RequestType.STATIC_PIN_AUTH,
								staticTokenId, isStatus);
						idAuthService.saveAutnTxn(authtxn);
					}
					if (requestedAuthType.isDemo()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"Demographic Authentication status : " + isStatus);
						auditHelper.audit(AuditModules.DEMO_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
								auditHelper.getUinorVid(authrequestdto), actualidType,
								AuditModules.DEMO_AUTH.getDesc());
						AutnTxn authtxn = auditHelper.createAuthTxn(authrequestdto, uin, RequestType.DEMO_AUTH,
								staticTokenId, isStatus);
						idAuthService.saveAutnTxn(authtxn);
					}
					if (requestedAuthType.isBio()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"BioMetric Authentication status :" + statusInfo);
						saveAndAuditBioAuthTxn(authrequestdto, true, auditHelper.getUinorVid(authrequestdto),
								actualidType, isStatus, staticTokenId);
					}
				}
			}

		}

		return authResponsedto;
	}

	/**
	 * Processed to authentic bio type request.
	 * 
	 * @param authRequestDTO authRequestDTO
	 * @param isAuth         boolean value for verify is auth type request or not.
	 * @param idType         idtype
	 * @param isStatus
	 * @throws IdAuthenticationBusinessException
	 */
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, boolean isAuth, String uin, IdType idType,
			boolean isStatus, String staticTokenId) throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FGR_MIN.getType())
						|| bioInfo.getBioType().equals(BioAuthType.FGR_IMG.getType()))) {
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					auditHelper.getUinorVid(authRequestDTO), idType, AuditModules.FINGERPRINT_AUTH.getDesc());
			AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.FINGER_AUTH, staticTokenId,
					isStatus);
			idAuthService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.IRIS_IMG.getType()))) {
			auditHelper.audit(AuditModules.IRIS_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					auditHelper.getUinorVid(authRequestDTO), idType, AuditModules.IRIS_AUTH.getDesc());
			AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.IRIS_AUTH, staticTokenId,
					isStatus);
			idAuthService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FACE_IMG.getType()))) {
			auditHelper.audit(AuditModules.FACE_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					auditHelper.getUinorVid(authRequestDTO), idType, AuditModules.FACE_AUTH.getDesc());
			AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.FACE_AUTH, staticTokenId,
					isStatus);
			idAuthService.saveAutnTxn(authTxn);
		}
	}
}

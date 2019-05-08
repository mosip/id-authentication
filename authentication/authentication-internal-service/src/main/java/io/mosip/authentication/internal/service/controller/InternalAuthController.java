package io.mosip.authentication.internal.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.integration.TokenIdManager;
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
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.internal.service.validator.InternalAuthRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the Internal authentication
 * requests.
 *
 * @author Prem Kumar
 */
@RestController
public class InternalAuthController {

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;

	/** The internal Auth Request Validator */
	@Autowired
	private InternalAuthRequestValidator internalAuthRequestValidator;

	@Autowired
	private Environment env;

	@Autowired
	private AuditHelper auditHelper;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(InternalAuthController.class);

	public static final String DEFAULT_PARTNER_ID = "INTERNAL";

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.addValidators(internalAuthRequestValidator);
	}

	/**
	 * Authenticate tsp.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param e              the e
	 * @return authResponseDTO the auth response DTO
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Internal Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public AuthResponseDTO authenticate(@Validated @RequestBody AuthRequestDTO authRequestDTO, @ApiIgnore Errors e)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthResponseDTO authResponseDTO = null;
		try {
			DataValidationUtil.validate(e);
			authResponseDTO = authFacade.authenticateIndividual(authRequestDTO, false, DEFAULT_PARTNER_ID);
		} catch (IDDataValidationException e1) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
					e1.getErrorTexts().isEmpty() ? "" : e1.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e1);
		} catch (IdAuthenticationBusinessException e1) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
					e1.getErrorTexts().isEmpty() ? "" : e1.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e1);
		} finally {
			AuthTypeDTO requestedAuthType = authRequestDTO.getRequestedAuth();
			if (requestedAuthType != null) {
				boolean isStatus = authResponseDTO != null && authResponseDTO.getResponse() != null
						&& authResponseDTO.getResponse().isAuthStatus();
				String uin = authRequestDTO.getIndividualId();
				Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE,
						Boolean.class);
				String idType = authRequestDTO.getIndividualIdType();
				IdType actualidType = null;
				if (idType != null && !idType.isEmpty()) {
					boolean statusInfo = authResponseDTO != null && authResponseDTO.getErrors() != null
							&& !authResponseDTO.getErrors().isEmpty();
					actualidType = idType.equalsIgnoreCase(IdType.UIN.getType()) ? IdType.UIN : IdType.VID;
					if (requestedAuthType.isOtp()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"OTP Authentication status : " + isStatus);
						auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.INTERNAL_REQUEST_RESPONSE,
								auditHelper.getUinorVid(authRequestDTO), actualidType, AuditModules.OTP_AUTH.getDesc());
						AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.OTP_AUTH,
								DEFAULT_PARTNER_ID, isStatus);
						idAuthService.saveAutnTxn(authTxn);
					}
					if (requestedAuthType.isPin()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"Pin Authentication  status :" + isStatus);
						auditHelper.audit(AuditModules.PIN_AUTH, AuditEvents.INTERNAL_REQUEST_RESPONSE,
								auditHelper.getUinorVid(authRequestDTO),
								authRequestDTO.getIndividualIdType().equalsIgnoreCase(IdType.UIN.getType()) ? IdType.UIN
										: IdType.VID,
								AuditModules.PIN_AUTH.getDesc());
						AutnTxn authtxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.STATIC_PIN_AUTH,
								DEFAULT_PARTNER_ID, isStatus);
						idAuthService.saveAutnTxn(authtxn);
					}
					if (requestedAuthType.isDemo()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"Demographic Authentication status : " + isStatus);
						auditHelper.audit(AuditModules.DEMO_AUTH, AuditEvents.INTERNAL_REQUEST_RESPONSE,
								auditHelper.getUinorVid(authRequestDTO), actualidType,
								AuditModules.DEMO_AUTH.getDesc());
						AutnTxn authtxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.DEMO_AUTH,
								DEFAULT_PARTNER_ID, isStatus);
						idAuthService.saveAutnTxn(authtxn);
					}
					if (requestedAuthType.isBio()) {
						mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
								env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
								"Bio Authentication status :" + statusInfo);
						saveAndAuditBioAuthTxn(authRequestDTO, true, auditHelper.getUinorVid(authRequestDTO),
								actualidType, isStatus, DEFAULT_PARTNER_ID);
					}
				}
			}

		}

		return authResponseDTO;
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
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, AuditEvents.INTERNAL_REQUEST_RESPONSE,
					auditHelper.getUinorVid(authRequestDTO), idType, AuditModules.FINGERPRINT_AUTH.getDesc());
			AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.FINGER_AUTH, staticTokenId,
					isStatus);
			idAuthService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.IRIS_IMG.getType()))) {
			auditHelper.audit(AuditModules.IRIS_AUTH, AuditEvents.INTERNAL_REQUEST_RESPONSE,
					auditHelper.getUinorVid(authRequestDTO), idType, AuditModules.IRIS_AUTH.getDesc());
			AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.IRIS_AUTH, staticTokenId,
					isStatus);
			idAuthService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FACE_IMG.getType()))) {
			auditHelper.audit(AuditModules.FACE_AUTH, AuditEvents.INTERNAL_REQUEST_RESPONSE,
					auditHelper.getUinorVid(authRequestDTO), idType, AuditModules.FACE_AUTH.getDesc());
			AutnTxn authTxn = auditHelper.createAuthTxn(authRequestDTO, uin, RequestType.FACE_AUTH, staticTokenId,
					isStatus);
			idAuthService.saveAutnTxn(authTxn);
		}
	}

}

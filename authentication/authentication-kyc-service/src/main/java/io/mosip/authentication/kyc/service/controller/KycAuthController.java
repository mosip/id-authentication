package io.mosip.authentication.kyc.service.controller;

import java.util.Date;

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
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.kyc.service.facade.KycFacadeImpl;
import io.mosip.authentication.kyc.service.validator.KycAuthRequestValidator;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;
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
public class KycAuthController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(KycAuthController.class);

	/** The KycAuthRequestValidator */
	@Autowired
	private KycAuthRequestValidator kycReqValidator;

	/** The auth facade. */
	@Autowired
	private KycFacadeImpl kycFacade;

	/** The auth facade. */
	@Autowired
	private Environment env;

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";


	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("kycAuthRequestDTO")
	private void initKycBinder(WebDataBinder binder) {
		binder.addValidators(kycReqValidator);
	}

	/** The Constant SUCCESS_STATUS. */
	private static final String SUCCESS_STATUS = "Y";

	/** The Constant FAILED. */
	private static final String FAILED = "N";

	/**
	 * Controller Method to auhtentication for eKyc-Details.
	 *
	 * @param kycAuthRequestDTO the kyc auth request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/{eKYC-Partner-ID}/{MISP-LK}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "eKyc Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public KycAuthResponseDTO processKyc(@Validated @RequestBody KycAuthRequestDTO kycAuthRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("eKYC-Partner-ID") String partnerId,
			@PathVariable("MISP-LK") String mispLK)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		AuthResponseDTO authResponseDTO = null;
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		try {
			DataValidationUtil.validate(errors);
			authResponseDTO = kycFacade.authenticateIndividual(kycAuthRequestDTO, true, partnerId);
			if (authResponseDTO != null) {
				kycAuthResponseDTO = kycFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId);
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} finally {
			if (kycAuthRequestDTO != null) {
				IdType actualidType = null;
				String idType = kycAuthRequestDTO.getIndividualIdType();
				actualidType = idType != null && !idType.isEmpty() && idType.equalsIgnoreCase(IdType.UIN.getType())
						? IdType.UIN
						: IdType.VID;
				String uin = kycAuthRequestDTO.getIndividualId();
				Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE,
						Boolean.class);
				String staticTokenId = staticTokenRequired ? tokenIdManager.generateTokenId(uin, partnerId) : "";
				boolean statusInfo = authResponseDTO != null && authResponseDTO.getResponse() != null
						&& authResponseDTO.getResponse().isAuthStatus();
				mosipLogger.info(IdAuthCommonConstants.SESSION_ID,
						env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), AUTH_FACADE,
						"Kyc Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.DEMO_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, uin, actualidType,
						AuditModules.EKYC_AUTH.getDesc());
				AutnTxn authTxn = createAuthTxn(kycAuthRequestDTO, uin, RequestType.KYC_AUTH_REQUEST, staticTokenId,
						kycAuthResponseDTO.getResponse() != null && kycAuthResponseDTO.getResponse().isKycStatus());
				idAuthService.saveAutnTxn(authTxn);
			}

		}
		return kycAuthResponseDTO;
	}

	/**
	 * sets AuthTxn entity values
	 * 
	 * @param authRequestDTO
	 * @param uin
	 * @param status
	 * @param comment
	 * @param requestType
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private AutnTxn createAuthTxn(KycAuthRequestDTO kycauthRequestDTO, String uin, RequestType requestType,
			String staticTokenId, boolean isStatus) throws IdAuthenticationBusinessException {
		try {
			String status = isStatus ? SUCCESS_STATUS : FAILED;
			String comment = isStatus ? requestType.getMessage() + " Success" : requestType.getMessage() + " Failed";
			String idvId = kycauthRequestDTO.getIndividualId();
			String reqTime = kycauthRequestDTO.getRequestTime();
			String idvIdType = kycauthRequestDTO.getIndividualIdType();
			String txnID = kycauthRequestDTO.getTransactionID();
			AutnTxn autnTxn = new AutnTxn();
			autnTxn.setRefId(idvId);
			autnTxn.setRefIdType(idvIdType);
			String id = createId(uin);
			autnTxn.setId(id); // FIXME
			autnTxn.setCrBy(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			autnTxn.setStaticTknId(staticTokenId);
			autnTxn.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			String strUTCDate = DateUtils.getUTCTimeFromDate(
					DateUtils.parseToDate(reqTime, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)));
			autnTxn.setRequestDTtimes(DateUtils.parseToLocalDateTime(strUTCDate));
			autnTxn.setResponseDTimes(DateUtils.getUTCCurrentDateTime()); // TODO check this
			autnTxn.setAuthTypeCode(requestType.getRequestType());
			autnTxn.setRequestTrnId(txnID);
			autnTxn.setStatusCode(status);
			autnTxn.setStatusComment(comment);
			// FIXME
			autnTxn.setLangCode(env.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE));
			return autnTxn;
		} catch (ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getClass().getName(),
					e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Creates UUID
	 * 
	 * @param uin
	 * @return
	 */
	private String createId(String uin) {
		String currentDate = DateUtils.formatDate(new Date(),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
	}

}

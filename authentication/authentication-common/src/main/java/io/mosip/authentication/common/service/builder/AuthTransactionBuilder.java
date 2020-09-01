package io.mosip.authentication.common.service.builder;

import java.util.Date;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.constant.TransactionType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * The builder to build {@code AutnTxn} instance.
 * 
 * @author Loganathan.Sekar
 *
 */
public class AuthTransactionBuilder {

	private static final String SERVICE_ACCOUNT = "service-account-";

	/**
	 * Instantiates a new auth transaction builder.
	 */
	private AuthTransactionBuilder() {
	}

	/**
	 * Get new instance of {@code AuthTransactionBuilder}
	 * 
	 * @return new instance of AuthTransactionBuilder
	 */
	public static AuthTransactionBuilder newInstance() {
		return new AuthTransactionBuilder();
	}

	/** The Constant SUCCESS_STATUS. */
	private static final String SUCCESS_STATUS = "Y";

	/** The Constant FAILED. */
	private static final String FAILED = "N";

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(AuditHelper.class);

	/** The auth request DTO. */
	private AuthRequestDTO authRequestDTO;

	/** The uin. */
	private String token;

	/** The request type. */
	private RequestType requestType;

	/** The static token id. */
	private String staticTokenId;

	/** The is status. */
	private boolean isStatus;

	/** The otp request DTO. */
	private OtpRequestDTO otpRequestDTO;

	private Optional<PartnerDTO> partnerOptional = Optional.empty();

	private boolean isInternal;

	/**
	 * Set the AuthRequestDTO.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withAuthRequest(AuthRequestDTO authRequestDTO) {
		this.authRequestDTO = authRequestDTO;
		return this;
	}

	/**
	 * With otp request.
	 *
	 * @param otpRequestDTO
	 *            the otp request DTO
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withOtpRequest(OtpRequestDTO otpRequestDTO) {
		this.otpRequestDTO = otpRequestDTO;
		return this;
	}

	/**
	 * Set the UIN.
	 *
	 * @param uin
	 *            the uin
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withToken(String token) {
		this.token = token;
		return this;
	}

	/**
	 * Set the RequestType.
	 *
	 * @param requestType
	 *            the request type
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withRequestType(RequestType requestType) {
		this.requestType = requestType;
		return this;
	}

	/**
	 * Set the static token.
	 *
	 * @param staticTokenId
	 *            the static token id
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withStaticToken(String staticTokenId) {
		this.staticTokenId = staticTokenId;
		return this;
	}

	/**
	 * With status.
	 *
	 * @param isStatus
	 *            the is status
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withStatus(boolean isStatus) {
		this.isStatus = isStatus;
		return this;
	}
	
	public AuthTransactionBuilder withPartner(Optional<PartnerDTO> partnerOptional) {
		this.partnerOptional = partnerOptional;
		return this;
	}
	
	public AuthTransactionBuilder withInternal(boolean isInternal) {
		this.isInternal = isInternal;
		return this;
	}

	/**
	 * Build {@code AutnTxn}.
	 *
	 * @param env
	 *            the env
	 * @return the instance of {@code AutnTxn}
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public AutnTxn build(Environment env, UinEncryptSaltRepo uinEncryptSaltRepo, UinHashSaltRepo uinHashSaltRepo,
			IdAuthSecurityManager securityManager) throws IdAuthenticationBusinessException {
		try {
			String idvId;
			String reqTime;
			String idvIdType;
			String txnID;
			if (authRequestDTO != null) {
				idvId = authRequestDTO.getIndividualId();
				reqTime = authRequestDTO.getRequestTime();
				idvIdType = IdType.getIDTypeStrOrDefault(authRequestDTO.getIndividualIdType());
				txnID = authRequestDTO.getTransactionID();
			} else if (otpRequestDTO != null) {
				idvId = otpRequestDTO.getIndividualId();
				reqTime = otpRequestDTO.getRequestTime();
				idvIdType = IdType.getIDTypeStrOrDefault(otpRequestDTO.getIndividualIdType());
				txnID = otpRequestDTO.getTransactionID();
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
						"Missing arguments to build for AutnTxn", this.toString());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
			}

			if (requestType != null) {
				String status = isStatus ? SUCCESS_STATUS : FAILED;
				String comment = isStatus ? requestType.getMessage() + " Success"
						: requestType.getMessage() + " Failed";
				AutnTxn autnTxn = new AutnTxn();
				autnTxn.setRefId(HMACUtils.digestAsPlainText(HMACUtils.generateHash(idvId.getBytes())));
				autnTxn.setRefIdType(idvIdType);
				String id = createId(token, env);
				autnTxn.setToken(token);
				autnTxn.setId(id);
				autnTxn.setCrBy(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
				autnTxn.setStaticTknId(staticTokenId);
				autnTxn.setCrDTimes(DateUtils.getUTCCurrentDateTime());
				String strUTCDate = DateUtils.getUTCTimeFromDate(
						DateUtils.parseToDate(reqTime, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)));
				autnTxn.setRequestDTtimes(DateUtils.parseToLocalDateTime(strUTCDate));
				autnTxn.setResponseDTimes(DateUtils.getUTCCurrentDateTime());
				autnTxn.setAuthTypeCode(requestType.getRequestType());
				autnTxn.setRequestTrnId(txnID);
				autnTxn.setStatusCode(status);
				autnTxn.setStatusComment(comment);
				// Setting primary code only
				autnTxn.setLangCode(env.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE));

				if(isInternal) {
					autnTxn.setEntitytype(TransactionType.INTERNAL.getType());
					Optional<String> clientId = Optional.ofNullable(securityManager.getUser());
					if (clientId.isPresent()) {
						String user = clientId.get();
						// Added workaround to trim the user id coming out authentication to avoid
						// length exceeding than 36 chars for Entity ID value in Auth Transaction table.
						// Handle this properly.
						if(user.contains(SERVICE_ACCOUNT)) {
							user = user.replace(SERVICE_ACCOUNT, "");
						}
						autnTxn.setEntityId(user);
						autnTxn.setEntityName(user);
					}
				} else {
					autnTxn.setEntitytype(TransactionType.PARTNER.getType());
					if(partnerOptional.isPresent()) {
						PartnerDTO partner = partnerOptional.get();
						autnTxn.setEntityId(partner.getPartnerId());
						autnTxn.setEntityName(partner.getPartnerName());
					}

				}

				return autnTxn;
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
						"Missing arguments to build for AutnTxn", this.toString());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
			}

		} catch (ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getClass().getName(),
					e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Creates UUID.
	 *
	 * @param uin
	 *            the uin
	 * @param env
	 *            the env
	 * @return the string
	 */
	private String createId(String token, PropertyResolver env) {
		String currentDate = DateUtils.formatDate(new Date(),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String tokenAndDate = token + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, tokenAndDate).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthTransactionBuilder [authRequestDTO=" + authRequestDTO + ", token=" + token + ", requestType="
				+ requestType + ", staticTokenId=" + staticTokenId + ", isStatus=" + isStatus + "]";
	}

}

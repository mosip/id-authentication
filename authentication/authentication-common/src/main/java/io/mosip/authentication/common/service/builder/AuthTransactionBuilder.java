package io.mosip.authentication.common.service.builder;

import java.util.Date;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthTransactionManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.constant.TransactionType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.idrepository.core.constant.IdRepoConstants;
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
	

	/**
	 * Instantiates a new auth transaction builder.
	 */
	private AuthTransactionBuilder() {
	}
	
	/**
	 *  Get new instance of {@code AuthTransactionBuilder}
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
	private String uin;

	/** The request type. */
	private RequestType requestType;

	/** The static token id. */
	private String staticTokenId;

	/** The is status. */
	private boolean isStatus;

	/** The otp request DTO. */
	private OtpRequestDTO otpRequestDTO;
	
	
	
	/**
	 * Set the AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withAuthRequest(AuthRequestDTO authRequestDTO) {
		this.authRequestDTO = authRequestDTO;
		return this;
	}
	
	/**
	 * With otp request.
	 *
	 * @param otpRequestDTO the otp request DTO
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withOtpRequest(OtpRequestDTO otpRequestDTO) {
		this.otpRequestDTO = otpRequestDTO;
		return this;
	}

	/**
	 * Set the UIN.
	 *
	 * @param uin the uin
	 * @return  {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withUin(String uin) {
		this.uin = uin;
		return this;
	}

	/**
	 * Set the RequestType.
	 *
	 * @param requestType the request type
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withRequestType(RequestType requestType) {
		this.requestType = requestType;
		return this;
	}

	/**
	 * Set the static token.
	 *
	 * @param staticTokenId the static token id
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withStaticToken(String staticTokenId) {
		this.staticTokenId = staticTokenId;
		return this;
	}

	/**
	 * With status.
	 *
	 * @param isStatus the is status
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withStatus(boolean isStatus) {
		this.isStatus = isStatus;
		return this;
	}
	
	/**
	 * Build {@code AutnTxn}.
	 *
	 * @param env the env
	 * @return the instance of {@code AutnTxn}
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public AutnTxn build(Environment env,UinEncryptSaltRepo uinEncryptSaltRepo,UinHashSaltRepo uinHashSaltRepo,IdAuthTransactionManager transactionManager) throws IdAuthenticationBusinessException {
		try {
			String idvId;
			String reqTime;
			String idvIdType;
			String txnID;
			if(authRequestDTO != null) {
				idvId = authRequestDTO.getIndividualId();
				reqTime = authRequestDTO.getRequestTime();
				idvIdType = authRequestDTO.getIndividualIdType();
				txnID = authRequestDTO.getTransactionID();
			} else if(otpRequestDTO != null) {
				idvId = otpRequestDTO.getIndividualId();
				reqTime = otpRequestDTO.getRequestTime();
				idvIdType =otpRequestDTO.getIndividualIdType();
				txnID = otpRequestDTO.getTransactionID();
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "Missing arguments to build for AutnTxn",
						this.toString());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
			}
			
			if (uin != null && requestType != null) {
				String status = isStatus ? SUCCESS_STATUS : FAILED;
				String comment = isStatus ? requestType.getMessage() + " Success"
						: requestType.getMessage() + " Failed";
				AutnTxn autnTxn = new AutnTxn();
				autnTxn.setRefId(HMACUtils.digestAsPlainText(HMACUtils.generateHash(idvId.getBytes())));
				autnTxn.setRefIdType(idvIdType);
				String id = createId(uin, env);
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
				int SaltModuloConstant=Integer.valueOf(env.getProperty(IdAuthConfigKeyConstants.UIN_SALT_MODULO));
				int uinModulo= (int)(Long.parseLong(uin)%SaltModuloConstant);
				String hashSaltValue=uinHashSaltRepo.retrieveSaltById(uinModulo);
				autnTxn.setUinHash(HMACUtils.digestAsPlainTextWithSalt(uin.getBytes(), hashSaltValue.getBytes()));
				String encryptSaltValue=uinEncryptSaltRepo.retrieveSaltById(uinModulo);
				autnTxn.setUin(uinModulo+IdAuthCommonConstants.UIN_MODULO_SPLITTER+uin +IdAuthCommonConstants.UIN_MODULO_SPLITTER + encryptSaltValue);
				Optional<String> clientId=Optional.of(transactionManager.getUser());
				if(clientId.isPresent()) {
					autnTxn.setEntityName(clientId.get());
					if(clientId.get().equalsIgnoreCase(env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID))) {
					     autnTxn.setEntitytype(TransactionType.PARTNER.getType());
					}
					else {
						autnTxn.setEntitytype(TransactionType.INTERNAL.getType());
					}
				}
				return autnTxn;
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "Missing arguments to build for AutnTxn",
						this.toString());
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
	 * @param uin the uin
	 * @param env the env
	 * @return the string
	 */
	private String createId(String uin, PropertyResolver env) {
		String currentDate = DateUtils.formatDate(new Date(),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthTransactionBuilder [authRequestDTO=" + authRequestDTO + ", uin=" + uin + ", requestType="
				+ requestType + ", staticTokenId=" + staticTokenId + ", isStatus=" + isStatus + "]";
	}
	
	

	

}

package io.mosip.authentication.common.service.builder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.constant.TransactionType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The builder to build {@code AutnTxn} instance.
 * 
 * @author Loganathan Sekar
 *
 */
public class AuthTransactionBuilder {

	/** The Constant REQ_TYPE_MSG_DELIM. */
	public static final String REQ_TYPE_MSG_DELIM = ";";

	/** The Constant REQ_TYPE_DELIM. */
	public static final String REQ_TYPE_DELIM = ",";

	/**
	 * Instantiates a new auth transaction builder.
	 */
	private AuthTransactionBuilder() {
	}

	/**
	 * Get new instance of {@code AuthTransactionBuilder}.
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

	/**
	 * Below comparator puts the KYC-AUTH to the beginning in the list
	 */
	private static final Comparator<? super RequestType> KYC_AUTH_COMPARATOR = (s1, s2) -> {
		if(s1.getType().equalsIgnoreCase(RequestType.EKYC_AUTH_REQUEST.getType())) {
			return -1;
		} else if(s2.getType().equalsIgnoreCase(RequestType.EKYC_AUTH_REQUEST.getType())) {
			return 1;
		}
		return 0;
	};

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(AuditHelper.class);

	/** The auth request DTO. */
	private BaseRequestDTO requestDTO;

	/** The uin. */
	private String token;

	/** The request type. */
	private Set<RequestType> requestTypes = new LinkedHashSet<>(6);

	/** The auth token id. */
	private String authTokenId;

	/** The is status. */
	private boolean isStatus;

	/** The partner optional. */
	private Optional<PartnerDTO> partnerOptional = Optional.empty();

	/** The is internal. */
	private boolean isInternal;

	/** The status comment. */
	private String statusComment;

	/** The auth type code. */
	private String authTypeCode;

	/**
	 * Set the AuthRequestDTO.
	 *
	 * @param requestDTO the auth request DTO
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withRequest(BaseRequestDTO requestDTO) {
		this.requestDTO = requestDTO;
		return this;
	}

	/**
	 * Set the UIN.
	 *
	 * @param token the token
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withToken(String token) {
		this.token = token;
		return this;
	}
	
	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * Set the RequestType.
	 *
	 * @param requestType the request type
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder addRequestType(RequestType requestType) {
		requestTypes.add(requestType);
		return this;
	}
	
	/**
	 * With auth type code.
	 *
	 * @param authTypeCode the auth type code
	 */
	public void withAuthTypeCode(String authTypeCode) {
		this.authTypeCode = authTypeCode;
	}

	/**
	 * Set the auth token.
	 *
	 * @param authTokenId the auth token id
	 * @return {@code AuthTransactionBuilder} instance
	 */
	public AuthTransactionBuilder withAuthToken(String authTokenId) {
		this.authTokenId = authTokenId;
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
	 * With status comment.
	 *
	 * @param statusComment the status comment
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withStatusComment(String statusComment) {
		this.statusComment = statusComment;
		return this;
	}

	/**
	 * With partner.
	 *
	 * @param partnerOptional the partner optional
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withPartner(Optional<PartnerDTO> partnerOptional) {
		this.partnerOptional = partnerOptional;
		return this;
	}

	/**
	 * With internal.
	 *
	 * @param isInternal the is internal
	 * @return the auth transaction builder
	 */
	public AuthTransactionBuilder withInternal(boolean isInternal) {
		this.isInternal = isInternal;
		return this;
	}
	
	public BaseRequestDTO getRequestDTO() {
		return requestDTO;
	}
	
	/**
	 * Gets the request types.
	 *
	 * @return the request types
	 */
	public Set<RequestType> getRequestTypes() {
		return requestTypes;
	}

	/**
	 * Build {@code AutnTxn}.
	 *
	 * @param env the env
	 * @param uinEncryptSaltRepo the uin encrypt salt repo
	 * @param uinHashSaltRepo the uin hash salt repo
	 * @param securityManager the security manager
	 * @return the instance of {@code AutnTxn}
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public AutnTxn build(EnvUtil env, IdaUinHashSaltRepo uinHashSaltRepo,
			IdAuthSecurityManager securityManager) throws IdAuthenticationBusinessException {
		try {
			String idvId;
			String reqTime;
			String idvIdType;
			String txnID;
			if (requestDTO != null) {
				idvId = requestDTO.getIndividualId();
				reqTime = requestDTO.getRequestTime();
				idvIdType = IdType.getIDTypeStrOrDefault(requestDTO.getIndividualIdType());
				txnID = requestDTO.getTransactionID();
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
						"Missing arguments to build for AutnTxn", "authRequestDTO");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
			}

			String status = isStatus ? SUCCESS_STATUS : FAILED;
			AutnTxn autnTxn = new AutnTxn();
			autnTxn.setRefId(idvId == null ? null : IdAuthSecurityManager.generateHashAndDigestAsPlainText(idvId.getBytes()));
			autnTxn.setRefIdType(idvIdType);
			String id = createId();
			autnTxn.setToken(token);
			autnTxn.setId(id);
			autnTxn.setCrBy(EnvUtil.getAppId());
			autnTxn.setAuthTknId(authTokenId);
			autnTxn.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			LocalDateTime strUTCDate = DateUtils.getUTCCurrentDateTime();
			try {
				strUTCDate = DateUtils.parseToLocalDateTime(DateUtils.getUTCTimeFromDate(
						DateUtils.parseToDate(reqTime, EnvUtil.getDateTimePattern())));
			} catch (ParseException e) {
				mosipLogger.warn(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getMessage(),
						"Invalid Request Time - setting to current date time");
			}
			autnTxn.setRequestDTtimes(strUTCDate);
			autnTxn.setResponseDTimes(DateUtils.getUTCCurrentDateTime());
			autnTxn.setRequestTrnId(txnID);
			autnTxn.setStatusCode(status);
			
			if (!requestTypes.isEmpty()) {
				String authTypeCodes = requestTypes.stream()
						.sorted(KYC_AUTH_COMPARATOR) // Put KYC-AUTH in the beginning if available
						.map(RequestType::getRequestType)
						.collect(Collectors.joining(REQ_TYPE_DELIM));
				autnTxn.setAuthTypeCode(authTypeCodes);
	
				String requestTypeMessages = requestTypes.stream()
						.sorted(KYC_AUTH_COMPARATOR) // Put KYC-AUTH message in the beginning if available
						.map(RequestType::getMessage)
						.collect(Collectors.joining(REQ_TYPE_MSG_DELIM));
				String comment = isStatus ? requestTypeMessages + " Success" : requestTypeMessages + " Failed";
				autnTxn.setStatusComment(comment);
			} else {
				if(authTypeCode != null) {
					autnTxn.setAuthTypeCode(authTypeCode);
				} else {
					autnTxn.setAuthTypeCode(IdAuthCommonConstants.UNKNOWN);
				}
			}
			//Overwrite the generated status comment if specified explicitly
			if(this.statusComment != null) {
				autnTxn.setStatusComment(statusComment);
			}
			
			if(autnTxn.getStatusComment() == null) {
				autnTxn.setStatusComment(IdAuthCommonConstants.UNKNOWN);
			}
			
			if(autnTxn.getToken() == null) {
				autnTxn.setToken(IdAuthCommonConstants.UNKNOWN);
			}
			
			// Will remove this column after discussion.
			autnTxn.setLangCode(IdAuthCommonConstants.NA);

			if (isInternal) {
				autnTxn.setEntitytype(TransactionType.INTERNAL.getType());
				String user = securityManager.getUser().replace(IdAuthCommonConstants.SERVICE_ACCOUNT, "");
				autnTxn.setEntityId(user);
				autnTxn.setEntityName(user);
			} else {
				autnTxn.setEntitytype(TransactionType.PARTNER.getType());
				if (partnerOptional.isPresent()) {
					PartnerDTO partner = partnerOptional.get();
					autnTxn.setEntityId(partner.getPartnerId());
					autnTxn.setEntityName(partner.getPartnerName());
				}

			}

			return autnTxn;

		} catch (ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getClass().getName(),
					e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Creates UUID.
	 *
	 * @param token the token
	 * @param env the env
	 * @return the string
	 */
	private String createId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthTransactionBuilder [requestDTO=" + requestDTO + ", token=" + token + ", requestType="
				+ requestTypes.toString() + ", authTokenId=" + authTokenId + ", isStatus=" + isStatus + "]";
	}

}

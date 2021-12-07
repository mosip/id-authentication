package io.mosip.authentication.common.service.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The builder class of AuthResponseDTO.
 *
 * @author Loganathan Sekar
 */
public class AuthResponseBuilder {
	
	/** The built flag. */
	private boolean built;

	/** The Auth response DTO. */
	private final AuthResponseDTO responseDTO;

	/** The auth status infos. */
	private List<AuthStatusInfo> authStatusInfos;

	/**
	 * Instantiates a new auth response builder.
	 *
	 * @param dateTimePattern the date time pattern
	 */
	private AuthResponseBuilder() {
		responseDTO = new AuthResponseDTO();
		authStatusInfos = new ArrayList<>();
	}

	/**
	 * Sets the txn ID.
	 *
	 * @param txnID the txn ID
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setTxnID(String txnID) {
		assertNotBuilt();
		responseDTO.setTransactionID(txnID);
		return this;
	}

	/**
	 * Adds the errors.
	 *
	 * @param errors the errors
	 * @return the auth response builder
	 */
	public AuthResponseBuilder addErrors(AuthError... errors) {
		assertNotBuilt();
		if (responseDTO.getErrors() == null) {
			responseDTO.setErrors(new ArrayList<>());
		}

		responseDTO.getErrors().addAll(Arrays.asList(errors));
		return this;
	}

	/**
	 * Adds the auth status info.
	 *
	 * @param authStatusInfo the auth status info
	 * @return the auth response builder
	 */
	public AuthResponseBuilder addAuthStatusInfo(AuthStatusInfo authStatusInfo) {
		assertNotBuilt();
		authStatusInfos.add(authStatusInfo);
		return this;
	}

	public AuthResponseBuilder setId() {
		responseDTO.setId("mosip.identity.auth");
		return this;
	}

	/**
	 * Sets the auth Token Id.
	 *
	 * @param authTokenId the auth token id
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setAuthTokenId(String authTokenId) {
		ResponseDTO res = new ResponseDTO();
		res.setAuthToken(authTokenId);
		responseDTO.setResponse(res);
		return this;
	}

	/**
	 * Sets the version.
	 *
	 * @param ver the ver
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setVersion(String ver) {
		responseDTO.setVersion(ver);
		return this;
	}

	/**
	 * Builds the.
	 *
	 * @param tokenID the auth token ID
	 * @return the auth response DTO
	 */
	public AuthResponseDTO build(String tokenID) {
		assertNotBuilt();
		boolean status = !authStatusInfos.isEmpty() && authStatusInfos.stream().allMatch(AuthStatusInfo::isStatus);
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(status);
		res.setAuthToken(tokenID);
		responseDTO.setResponse(res);
		responseDTO.setResponseTime(DateUtils.getUTCCurrentDateTimeString(IdAuthCommonConstants.UTC_DATETIME_PATTERN));
		AuthError[] authErrors = authStatusInfos.stream().flatMap(statusInfo -> Optional.ofNullable(statusInfo.getErr())
				.map(List<AuthError>::stream).orElseGet(Stream::empty)).toArray(size -> new AuthError[size]);
		if(authErrors.length > 0) {
			addErrors(authErrors);
		}
		
		if(responseDTO.getErrors() != null && responseDTO.getErrors().isEmpty()) {
			responseDTO.setErrors(null);
		}

		built = true;
		return responseDTO;
	}

	/**
	 * Assert not built.
	 */
	private void assertNotBuilt() {
		if (built) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Get new instance of AuthResponseBuilder.
	 *
	 * @param dateTimePattern the date time pattern
	 * @return the auth response builder
	 */
	public static AuthResponseBuilder newInstance() {
		return new AuthResponseBuilder();
	}

}
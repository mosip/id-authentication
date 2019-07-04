package io.mosip.authentication.common.service.builder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;

/**
 * The builder class of AuthResponseDTO.
 *
 * @author Loganathan Sekar
 */
public class AuthResponseBuilder {

	/** The date format to use */
	private SimpleDateFormat dateFormat;

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
	private AuthResponseBuilder(String dateTimePattern) {
		responseDTO = new AuthResponseDTO();
		authStatusInfos = new ArrayList<>();
		dateFormat = new SimpleDateFormat(dateTimePattern);
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

	public AuthResponseBuilder setId(String idType) {
		responseDTO.setId("mosip.identity.auth");
		return this;
	}

	/**
	 * Sets the Static Token Id.
	 *
	 * @param staticTokenId the static token id
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setStaticTokenId(String staticTokenId) {
		ResponseDTO res = new ResponseDTO();
		res.setStaticToken(staticTokenId);
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
	 * @param staticTokenID the static token ID
	 * @return the auth response DTO
	 */
	public AuthResponseDTO build(String staticTokenID) {
		assertNotBuilt();
		boolean status = !authStatusInfos.isEmpty() && authStatusInfos.stream().allMatch(AuthStatusInfo::isStatus);
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(status);
		res.setStaticToken(staticTokenID);
		responseDTO.setResponse(res);

		responseDTO.setResponseTime(dateFormat.format(new Date()));
		AuthError[] authErrors = authStatusInfos.stream().flatMap(statusInfo -> Optional.ofNullable(statusInfo.getErr())
				.map(List<AuthError>::stream).orElseGet(Stream::empty)).toArray(size -> new AuthError[size]);
		addErrors(authErrors);

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
	public static AuthResponseBuilder newInstance(String dateTimePattern) {
		return new AuthResponseBuilder(dateTimePattern);
	}

}

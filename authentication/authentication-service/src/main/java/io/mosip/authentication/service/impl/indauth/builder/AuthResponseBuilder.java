package io.mosip.authentication.service.impl.indauth.builder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseInfo;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.MatchInfo;

/**
 * The builder class of AuthResponseDTO.
 *
 * @authour Loganathan Sekar
 */
public class AuthResponseBuilder {

	//FIXME get the date time pattern from configuration
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/** The built. */
	private boolean built;

	/** The Constant DEFAULT_USAGE_DATA_HEX_COUNT. */
	private static final int DEFAULT_USAGE_DATA_HEX_COUNT = 16;
	
	/** The response DTO. */
	private final AuthResponseDTO responseDTO;
	
	/** The auth status infos. */ 
	private List<AuthStatusInfo> authStatusInfos;

	/**
	 * Instantiates a new auth response builder.
	 */
	private AuthResponseBuilder() {
		responseDTO = new AuthResponseDTO();
		AuthResponseInfo authResponseInfo = new AuthResponseInfo();
		responseDTO.setInfo(authResponseInfo);
		authStatusInfos = new ArrayList<>();
	}

	/**
	 * New instance.
	 *
	 * @return the auth response builder
	 */
	public static AuthResponseBuilder newInstance() {
		return new AuthResponseBuilder();
	}

	/**
	 * Sets the txn ID.
	 *
	 * @param txnID the txn ID
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setTxnID(String txnID) {
		assertNotBuilt();
		responseDTO.setTxnID(txnID);
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
		if (responseDTO.getErr() == null) {
			responseDTO.setErr(new ArrayList<>());
		}

		responseDTO.getErr().addAll(Arrays.asList(errors));
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

	/**
	 * Sets the id type.
	 *
	 * @param idType the id type
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setIdType(String idType) {
		responseDTO.getInfo().setIdType(idType);
		return this;
	}

	/**
	 * Sets the req time.
	 *
	 * @param reqTime the req time
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setReqTime(String reqTime) {
		responseDTO.getInfo().setReqTime(reqTime);
		return this;
	}

	/**
	 * Sets the version.
	 *
	 * @param ver the ver
	 * @return the auth response builder
	 */
	public AuthResponseBuilder setVersion(String ver) {
		responseDTO.getInfo().setVer(ver);
		return this;
	}

	/**
	 * Builds the.
	 *
	 * @return the auth response DTO
	 */
	public AuthResponseDTO build() {
		assertNotBuilt();
		boolean status = !authStatusInfos.isEmpty() && authStatusInfos.stream().allMatch(AuthStatusInfo::isStatus);
		if(status) {
			responseDTO.setStatus("Y");
		} else {
			responseDTO.setStatus("N");
		}

		responseDTO.setResTime(dateFormat.format(new Date()));

		AuthError[] authErrors = authStatusInfos.stream().flatMap(statusInfo -> Optional.ofNullable(statusInfo.getErr())
				.map(List<AuthError>::stream).orElseGet(Stream::empty)).toArray(size -> new AuthError[size]);
		addErrors(authErrors);

		List<MatchInfo> matchInfos = authStatusInfos.stream().flatMap(statusInfo -> Optional
				.ofNullable(statusInfo.getMatchInfos())
				.map(List<MatchInfo>::stream)
				.orElseGet(Stream::empty))
				.collect(Collectors.toList());
		responseDTO.getInfo().setMatchInfos(matchInfos);

		BitwiseInfo bitwiseInfo = new BitwiseInfo(DEFAULT_USAGE_DATA_HEX_COUNT);

		authStatusInfos.stream()
				.flatMap(statusInfo -> Optional.ofNullable(statusInfo.getUsageDataBits())
						.map(List<AuthUsageDataBit>::stream).orElseGet(Stream::empty))
				.collect(Collectors.toList())
				.forEach(usageDataBit -> bitwiseInfo.setBit(usageDataBit.getHexNum(), usageDataBit.getBitIndex()));

		responseDTO.getInfo().setUsageData(bitwiseInfo.toString());

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

}

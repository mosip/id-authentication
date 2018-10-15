package io.mosip.authentication.service.impl.indauth.builder;

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
 * The builder class of AuthResponseDTO
 * 
 * @authour Loganathan Sekar
 */
public class AuthResponseBuilder {
	
	private boolean built;
	
	private static final int DEFAULT_USAGE_DATA_HEX_COUNT = 16;
	private final AuthResponseDTO responseDTO;
	private List<AuthStatusInfo> authStatusInfos;

	private AuthResponseBuilder() {
		responseDTO = new AuthResponseDTO();
		AuthResponseInfo authResponseInfo = new AuthResponseInfo();
		responseDTO.setInfo(authResponseInfo);
		authStatusInfos = new ArrayList<>();
	}

	public static AuthResponseBuilder newInstance() {
		return new AuthResponseBuilder();
	}

	public AuthResponseBuilder setTxnID(String txnID) {
		assertNotBuilt();
		responseDTO.setTxnID(txnID);
		return this;
	}

	public AuthResponseBuilder addErrors(AuthError... errors) {
		assertNotBuilt();
		if(responseDTO.getErr() == null) {
			responseDTO.setErr(new ArrayList<>());
		}
		
		responseDTO.getErr().addAll(Arrays.asList(errors));
		return this;
	}
	
	public AuthResponseBuilder addAuthStatusInfo(AuthStatusInfo authStatusInfo) {
		assertNotBuilt();
		authStatusInfos.add(authStatusInfo);
		return  this;
	}
	
	public AuthResponseBuilder setIdType(String idType) {
		responseDTO.getInfo().setIdType(idType);;
		return  this;
	}
	
	public AuthResponseBuilder setReqTime(String reqTime) {
		responseDTO.getInfo().setReqTime(reqTime);
		return  this;
	}
	
	public AuthResponseBuilder setVersion(String ver) {
		responseDTO.getInfo().setVer(ver);
		return  this;
	}
	
	public AuthResponseDTO build() {
		assertNotBuilt();
		boolean status = !authStatusInfos.isEmpty() 
				&& authStatusInfos.stream().allMatch(AuthStatusInfo::isStatus);
		responseDTO.setStatus(status );
		
		responseDTO.setResTime(new Date());
		
		AuthError[] authErrors = authStatusInfos.stream()
											.flatMap(statusInfo -> 
												Optional.ofNullable(statusInfo.getErr())
													.map(List<AuthError>::stream)
													.orElseGet(Stream::empty))
											.toArray(size -> new AuthError[size]);
		addErrors(authErrors);
		
		List<MatchInfo> matchInfos = authStatusInfos.stream()
										.flatMap(statusInfo -> 
											Optional.ofNullable(statusInfo.getMatchInfos())
												.map(List<MatchInfo>::stream)
												.orElseGet(Stream::empty))
										.collect(Collectors.toList());
		responseDTO.getInfo().setMatchInfos(matchInfos);
		
		BitwiseInfo bitwiseInfo = new BitwiseInfo(DEFAULT_USAGE_DATA_HEX_COUNT);
		
		authStatusInfos.stream()
						.flatMap(statusInfo -> 
							Optional.ofNullable(statusInfo.getUsageDataBits())
								.map(List<AuthUsageDataBit>::stream)
								.orElseGet(Stream::empty))
						.collect(Collectors.toList())
						.forEach(usageDataBit -> 
									bitwiseInfo.setBit(usageDataBit.getHexNum(), usageDataBit.getBitIndex()));
		
		responseDTO.getInfo().setUsageData(bitwiseInfo.toString());
		
		built = true;
		return responseDTO;
	}
	
	
	private void assertNotBuilt() {
		if(built) {
			throw new IllegalStateException();
		}
	}
	

}

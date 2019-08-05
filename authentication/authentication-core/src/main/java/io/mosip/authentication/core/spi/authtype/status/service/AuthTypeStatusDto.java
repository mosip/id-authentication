package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import lombok.Data;

@Data
public class AuthTypeStatusDto {

	private String id;
	private String version;
	private String requestTime;
	private boolean consentObtained;
	private String individualId;
	private String individualIdType;
	private List<AuthtypeStatus> request;

}

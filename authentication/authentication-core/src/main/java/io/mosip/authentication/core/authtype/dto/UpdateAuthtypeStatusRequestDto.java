package io.mosip.authentication.core.authtype.dto;

import java.util.List;

import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import lombok.Data;

@Data
public class UpdateAuthtypeStatusRequestDto {
	private String id;
	private String version;
	private String requestTime;
	private boolean consentObtained;
	private String individualId;
	private String individualIdType;
	private List<AuthTypeDTO> request;

}

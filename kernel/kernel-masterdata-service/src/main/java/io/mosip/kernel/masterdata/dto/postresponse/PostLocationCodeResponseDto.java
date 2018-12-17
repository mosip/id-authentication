package io.mosip.kernel.masterdata.dto.postresponse;

import lombok.Data;

@Data


public class PostLocationCodeResponseDto {

	private String code;
	private String parentLocCode;
	private Boolean isActive;

}

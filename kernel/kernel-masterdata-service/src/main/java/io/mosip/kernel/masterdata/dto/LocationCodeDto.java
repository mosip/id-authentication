package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data


public class LocationCodeDto {

	private String code;
	private String parentLocCode;
	private Boolean isActive;

}

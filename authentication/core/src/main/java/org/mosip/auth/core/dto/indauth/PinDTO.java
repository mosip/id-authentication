package org.mosip.auth.core.dto.indauth;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PinDTO {

	@NotNull
	private String value;
	
	@NotNull
	private PinType type;
}

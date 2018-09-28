package org.mosip.auth.core.dto.indauth;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PinDTO {

	@NotNull
	private String pinValue;
	@NotNull
	private PinType pinType;
}

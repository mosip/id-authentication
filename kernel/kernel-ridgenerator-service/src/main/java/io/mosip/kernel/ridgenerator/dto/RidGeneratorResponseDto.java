package io.mosip.kernel.ridgenerator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RidGeneratorResponseDto{
	private String rid;
}

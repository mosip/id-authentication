package io.mosip.kernel.ridgenerator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO class for generated RID response.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RidGeneratorResponseDto {
	/**
	 * The generated RID.
	 */
	private String rid;
}

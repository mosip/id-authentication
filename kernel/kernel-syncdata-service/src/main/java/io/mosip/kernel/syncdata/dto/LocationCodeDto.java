package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LocationCodeDto extends BaseDto {

	private String code;
	private String parentLocCode;
	private Boolean isActive;

}

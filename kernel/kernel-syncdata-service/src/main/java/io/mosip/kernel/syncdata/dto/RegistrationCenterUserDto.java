package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegistrationCenterUserDto extends BaseDto {

	@NotNull
	@Size(min = 1, max = 10)
	private String regCenterId;
	
	@NotNull
	@Size(min =1, max = 36)
	private String userId;
	
	@NotNull
	private Boolean isActive;
}

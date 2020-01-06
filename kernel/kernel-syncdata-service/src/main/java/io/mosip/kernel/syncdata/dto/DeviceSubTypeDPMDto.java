package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceSubTypeDPMDto extends BaseDto {

	/** The code. */

	private String code;

	/** The dtype code. */

	private String dtypeCode;

	/** The name. */

	private String name;

	/** The descr. */
	private String descr;

}

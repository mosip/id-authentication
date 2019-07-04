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
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AppDetailDto extends BaseDto {

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The descr. */
	private String descr;

	/** The lang code. */
	private String langCode;

}

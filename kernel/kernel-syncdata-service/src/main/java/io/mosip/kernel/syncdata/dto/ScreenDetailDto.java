package io.mosip.kernel.syncdata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;


/** @author Srinivasan
 *  @since 1.0.0
 * Instantiates a new screen detail dto.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ScreenDetailDto extends BaseDto {

	/** The id. */
	private String id;

	/** The app id. */
	private String appId;

	/** The name. */
	private String name;

	/** The descr. */
	private String descr;

}
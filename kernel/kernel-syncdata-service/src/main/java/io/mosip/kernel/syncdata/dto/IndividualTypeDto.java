package io.mosip.kernel.syncdata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Srinivasan
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IndividualTypeDto extends BaseDto {

	private String code;

	private String name;

	private String langCode;
}

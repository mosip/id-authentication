package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PinInfo {

	/** PIN Value */
	private String value;

	/** PIN type */
	private String type;
}

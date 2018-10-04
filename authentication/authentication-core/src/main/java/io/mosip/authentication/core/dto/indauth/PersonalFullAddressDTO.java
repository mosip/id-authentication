package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used for match complete address with 100% of match strategy and
 * match value.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalFullAddressDTO {

	private String matchingStrategy;
	private String addressValue;
	private Integer matchValue;
	private String localAddressValue;
	private Integer localMatchValue;
}

package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
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

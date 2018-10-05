package io.mosip.authentication.core.dto.indauth;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

	/**
	 * msPri(Match Strategy)Valid value is “E” (Exact match) and “P” (Partial Match)
	 * in Primary language
	 */
	
	private String msPri;

	/** Complete Address of the Resident in the primary language */
	@Size(max=155)
	private String addrPri;

	/** Complete Address of the Resident in the Secondary language */
	@Size(max=155)
	private String addrSec;

	/**
	 * mtPri(Match Threshold or MatchValue) in Primary language. Valid value is 1 to
	 * 100 and it is used only when matching strategy (ms attribute) is “P” (Partial
	 * match).
	 */
	@Pattern(regexp = "^([0-9]?[1-9]$ | ^(100)$")
	private Integer mtPri;

	/**
	 * mtSec(Match Threshold or MatchValue) in Secondary or Local language. Valid
	 * value is 1 to 100 and it is used only when matching strategy (ms attribute)
	 * is “P” (Partial match).
	 */
	@Pattern(regexp = "^([0-9]?[1-9]$ | ^(100)$")
	private Integer mtSec;
}

package io.mosip.authentication.core.spi.idauth.demo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

	/** Complete Address of the Resident in the primary language */
	@Size(max = 155)
	private String addrPri;

	/** Complete Address of the Resident in the Secondary language */
	@Size(max = 155)
	private String addrSec;

	/**
	 * msPri(Match Strategy)Valid value is “E” (Exact match) and “P” (Partial Match)
	 * in Primary language
	 */

	@Pattern(regexp = "^(E|P[H]?)")
	private String msPri; // TODO

	/**
	 * msSec(Match Strategy)Valid value is “E” (Exact match) and “P” (Partial Match)
	 * for Secondary language
	 */
	@Pattern(regexp = "^(E|P[H]?)")
	private String msSec;  //TODO

	/**
	 * mtPri(Match Threshold or MatchValue) in Primary language. Valid value is 1 to
	 * 100 and it is used only when matching strategy (ms attribute) is “P” (Partial
	 * match).
	 */
	@Min(1)
	@Max(100)
	private Integer mtPri; // TODO

	/**
	 * mtSec(Match Threshold or MatchValue) in Secondary or Local language. Valid
	 * value is 1 to 100 and it is used only when matching strategy (ms attribute)
	 * is “P” (Partial match).
	 */
	@Min(1)
	@Max(100)
	private Integer mtSec; // TODO
}

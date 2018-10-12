package io.mosip.authentication.core.spi.idauth.demo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class give the info of individual with match strategy and match value.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalIdentityDTO {

	/** Name of the Resident in the primary language */
	private String namePri;

	/** Name of the Resident in the Secondary language */
	private String nameSec;

	/**
	 * msPri(Match Strategy)Valid value is “E” (Exact match) and “P” (Partial Match)
	 * for Primary language
	 */
	@Pattern(regexp = "^(E|P[H]?)")
	private String msPri;

	/**
	 * msSec(Match Strategy)Valid value is “E” (Exact match) and “P” (Partial Match)
	 * for Secondary language
	 */
	@Pattern(regexp = "^(E|P[H]?)")
	private String msSec;

	/**
	 * mtPri(Match Threshold or MatchValue) in Primary language. Valid value is 1 to
	 * 100 and it is used only when matching strategy (ms attribute) is “P” (Partial
	 * match).
	 */
	//FIXME add manual validation
//	@Min(1)
//	@Max(100)
	private Integer mtPri;

	/**
	 * mtSec(Match Threshold or MatchValue) in Secondary or Local language. Valid
	 * value is 1 to 100 and it is used only when matching strategy (ms attribute)
	 * is “P” (Partial match).
	 */
	//FIXME add manual validation
//	@Min(1)
//	@Max(100)
	private Integer mtSec;

	/** Gender acceptable values are M- Male, F-Female, T- Transgender */
	@Pattern(regexp = "^([M|F|T])$")
	private String gender;

	/** Date of Birth of the individual of Pattern "yyyy-MM-dd". */
	@Pattern(regexp = "^([0-9]{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$")
	private String dob;

	/** dobType */
	private String dobType; // TODO

	/** Age of the individual. Should be between 0 and 150. */
	@Min(1)
	@Max(150)
	private Integer age;

	/** phone */
	@Pattern(regexp = "^([0-9]{10}$)")
	private String phone; // TODO

	/** Registered e-mail ID of the individual */
	@Pattern(regexp = "^[\\_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
	private String email;

}

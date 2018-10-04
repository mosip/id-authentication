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
public class PersonalIdentityDTO {

	private String matchingStrategy;
	private String name;
	private String matchValue;
	private String localNmae;
	private String localMatchValue;
	private String gender;
	private String dob;
	private String dobType;
	private Integer age;
	private String phone;
	private String email;

}

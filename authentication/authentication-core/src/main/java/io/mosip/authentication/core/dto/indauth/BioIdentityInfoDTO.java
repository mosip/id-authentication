package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Prem Kumar
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BioIdentityInfoDTO {
	/** The Value for type */
	private String type; 
	
	/** The Value for subType*/
	private String subType;
	
	/** The Value for value*/
	private String value; 
}

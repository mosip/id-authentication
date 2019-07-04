package io.mosip.authentication.core.indauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * This Class Holds the values for Bio Related data
 * 
 * @author Prem Kumar
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BioIdentityInfoDTO {
	
	/** The Value for data */
	private DataDTO data;
	
	/** The Value for hash */
	private String hash;
	
	/** The Value for sessionKey */
	private String sessionKey;
	
	/** The Value for signature */
	private String signature;
}

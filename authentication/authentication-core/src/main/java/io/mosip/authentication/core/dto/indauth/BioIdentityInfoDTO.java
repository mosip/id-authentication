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
	
	/** The Value for data */
	private DataDTO data;
	
	/** The Value for hash */
	private String hash;
	
	/** The Value for sessionKey */
	private String sessionKey;
	
	/** The Value for signature */
	private String signature;
}

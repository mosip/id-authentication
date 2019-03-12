package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Data
public class IdentityDTO {

	/** variable to hold age value */
	private String age;
	
	/** variable to hold dob value */
	private String dob;
	
	/** List of identity info to hold name */
	List<IdentityInfoDTO> name;


	/** List of identity info to hold dateOfBirthType */
	List<IdentityInfoDTO> dobType;


	/** List of identity info to hold gender */
	List<IdentityInfoDTO> gender;

	/** List of identity info to hold phoneNumber */
	List<IdentityInfoDTO> phoneNumber;

	/** List of identity info to hold emailId */
	List<IdentityInfoDTO> emailId;

	/** List of identity info to hold addressLine1 */
	List<IdentityInfoDTO> addressLine1;

	/** List of identity info to hold addressLine2 */
	List<IdentityInfoDTO> addressLine2;

	/** List of identity info to hold addressLine3 */
	List<IdentityInfoDTO> addressLine3;

	/** List of identity info to hold location1 */
	List<IdentityInfoDTO> location1;

	/** List of identity info to hold location2 */
	List<IdentityInfoDTO> location2;

	/** List of identity info to hold location3 */
	List<IdentityInfoDTO> location3;

	/** List of identity info to hold pinCode */
	List<IdentityInfoDTO> pinCode;

	/** List of identity info to hold fullAddress */
	List<IdentityInfoDTO> fullAddress;
	
	/** List of biometric identity info */
	List<BioIdentityInfoDTO> biometrics;

}

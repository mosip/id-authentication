package io.mosip.authentication.core.dto.indauth;

import java.util.List;
import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Data
public class IdentityDTO {

	/** List of identity info to hold name */
	List<IdentityInfoDTO> name;

	/** List of identity info to hold dateOfBirth */
	List<IdentityInfoDTO> dateOfBirth;

	/** List of identity info to hold dateOfBirthType */
	List<IdentityInfoDTO> dateOfBirthType;

	/** List of identity info to hold age */
	List<IdentityInfoDTO> age;

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

	/** List of identity info to hold leftEye */
	List<IdentityInfoDTO> leftEye;

	/** List of identity info to hold rightEye */
	List<IdentityInfoDTO> rightEye;

	/** List of identity info to hold leftIndex */
	List<IdentityInfoDTO> leftIndex;

	/** List of identity info to hold leftLittle */
	List<IdentityInfoDTO> leftLittle;

	/** List of identity info to hold leftMiddle */
	List<IdentityInfoDTO> leftMiddle;

	/** List of identity info to hold leftRing */
	List<IdentityInfoDTO> leftRing;

	/** List of identity info to hold leftThumb */
	List<IdentityInfoDTO> leftThumb;

	/** List of identity info to hold rightIndex */
	List<IdentityInfoDTO> rightIndex;

	/** List of identity info to hold rightLittle */
	List<IdentityInfoDTO> rightLittle;

	/** List of identity info to hold rightMiddle */
	List<IdentityInfoDTO> rightMiddle;

	/** List of identity info to hold rightRing */
	List<IdentityInfoDTO> rightRing;

	/** List of identity info to hold rightThumb */
	List<IdentityInfoDTO> rightThumb;

	/** List of identity info to hold face */
	List<IdentityInfoDTO> face;

}

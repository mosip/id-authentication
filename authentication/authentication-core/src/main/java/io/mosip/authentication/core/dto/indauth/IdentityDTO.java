package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import lombok.Data;

@Data
public class IdentityDTO {

	List<IdentityInfoDTO> name;
	List<IdentityInfoDTO> dateOfBirth;
	List<IdentityInfoDTO> dateOfBirthType;
	List<IdentityInfoDTO> age;
	List<IdentityInfoDTO> gender;
	List<IdentityInfoDTO> phoneNumber;
	List<IdentityInfoDTO> emailId;
	List<IdentityInfoDTO> addressLine1;
	List<IdentityInfoDTO> addressLine2;
	List<IdentityInfoDTO> addressLine3;
	List<IdentityInfoDTO> location1;
	List<IdentityInfoDTO> location2;
	List<IdentityInfoDTO> location3;
	List<IdentityInfoDTO> pinCode;
	List<IdentityInfoDTO> fullAddress;
	List<IdentityInfoDTO> leftEye;
	List<IdentityInfoDTO> rightEye;
	List<IdentityInfoDTO> leftIndex;
	List<IdentityInfoDTO> leftLittle;
	List<IdentityInfoDTO> leftMiddle;
	List<IdentityInfoDTO> leftRing;
	List<IdentityInfoDTO> leftThumb;
	List<IdentityInfoDTO> rightIndex;
	List<IdentityInfoDTO> rightLittle;
	List<IdentityInfoDTO> rightMiddle;
	List<IdentityInfoDTO> rightRing;
	List<IdentityInfoDTO> rightThumb;
	List<IdentityInfoDTO> face;

}

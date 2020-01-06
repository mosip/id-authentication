package io.mosip.registration.dto;

import java.util.List;

import io.mosip.registration.dto.demographic.ValuesDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This DTO class contains the meta-information of the Registration
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
@Getter
@Setter
public class RegistrationMetaDataDTO extends BaseDTO {

	private double geoLatitudeLoc;
	private double geoLongitudeLoc;
	// Document Based or Introducer Based
	private String registrationCategory;
	private String machineId;
	private String centerId;
	private String previousRID;
	private String uin;
	private String consentOfApplicant;
	private String parentOrGuardianUINOrRID;
	private String parentOrGuardianUIN;
	private String parentOrGuardianRID;
	private String deviceId;
	private String applicantTypeCode;
	private List<ValuesDTO> fullName;
	
	private String regClientVersionNumber;
}

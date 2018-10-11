package io.mosip.registration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the meta-information of the Registration
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationMetaDataDTO extends BaseDTO {
	private double geoLatitudeLoc;
	private double geoLongitudeLoc;
	// New , update , correction, lost UIN
	private String applicationType;
	// Document Based or Introducer Based
	private String registrationCategory;
}

package io.mosip.registration.dto;

import lombok.Data;

/**
 * The Class SelectionListDTO will contain all the updatable fields for update UIN.
 * 
 * @author Mahesh Kumar
 */
@Data
public class SelectionListDTO {

	private String uinId;
	private boolean isChild;
	private boolean name;
	private boolean age;
	private boolean gender;
	private boolean address;
	private boolean phone;
	private boolean email;
	private boolean biometricException;
	private boolean biometricIris;
	private boolean biometricFingerprint;
	private boolean cnieNumber;
	private boolean parentOrGuardianDetails;
	private boolean foreigner;
}

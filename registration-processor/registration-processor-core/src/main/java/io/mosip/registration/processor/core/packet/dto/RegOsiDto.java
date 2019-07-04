package io.mosip.registration.processor.core.packet.dto;
	
import lombok.Data;

/**
 * Instantiates a new reg osi dto.
 */
@Data
public class RegOsiDto {
	
	/** The reg id. */
	private String regId;

	/** The prereg id. */
	private String preregId;

	/** The officer id. */
	private String officerId;

	/** The officer hashed pin. */
	private String officerHashedPin;

	/** The officer hashed pwd. */
	private String officerHashedPwd;

	/** The supervisor id. */
	private String supervisorId;

	/** The supervisor hashed pwd. */
	private String supervisorHashedPwd;

	/** The supervisor hashed pin. */
	private String supervisorHashedPin;


	/** The introducer typ. */
	private String introducerTyp;

	/** The is active. */
	private Boolean isActive;

	/** The is deleted. */
	private Boolean isDeleted;

	private String supervisorOTPAuthentication;

	private String officerOTPAuthentication;

	private String supervisorBiometricFileName;
	
	private String officerBiometricFileName;
	

}
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

	/** The officer iris image name. */
	private String officerIrisImageName;

	/** The officerfinger type. */
	private String officerfingerType;

	/** The officer iris type. */
	private String officerIrisType;

	/** The officer photo name. */
	private String officerPhotoName;

	/** The officer hashed pin. */
	private String officerHashedPin;

	/** The officer hashed pwd. */
	private String officerHashedPwd;

	/** The officer fingerp image name. */
	private String officerFingerpImageName;

	/** The supervisor id. */
	private String supervisorId;

	/** The supervisor fingerp image name. */
	private String supervisorFingerpImageName;

	/** The supervisor iris image name. */
	private String supervisorIrisImageName;

	/** The supervisor finger type. */
	private String supervisorFingerType;

	/** The supervisor iris type. */
	private String supervisorIrisType;

	/** The supervisor hashed pwd. */
	private String supervisorHashedPwd;

	/** The supervisor hashed pin. */
	private String supervisorHashedPin;

	/** The supervisor photo name. */
	private String supervisorPhotoName;

	/** The introducer id. */
	private String introducerId;

	/** The introducer typ. */
	private String introducerTyp;

	/** The introducer reg id. */
	private String introducerRegId;

	/** The introducer iris image name. */
	private String introducerIrisImageName;

	/** The introducer fingerp type. */
	private String introducerFingerpType;

	/** The introducer iris type. */
	private String introducerIrisType;

	/** The introducer fingerp image name. */
	private String introducerFingerpImageName;
	
	/** The introducer photo name. */
	private String introducerPhotoName;

	/** The introducer uin. */
	private String introducerUin;

	/** The is active. */
	private Boolean isActive;

	/** The is deleted. */
	private Boolean isDeleted;
}

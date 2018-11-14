package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new osi data.
 */
@Data
public class OsiData {

	/** The operator id. */
	private String operatorId;
	
	/** The operator fingerprint image. */
	private String operatorFingerprintImage;
	
	/** The operator iris name. */
	private String operatorIrisName;
	
	/** The supervisor id. */
	private String supervisorId;
	
	/** The supervisor name. */
	private String supervisorName;
	
	/** The supervisor fingerprint image. */
	private String supervisorFingerprintImage;
	
	/** The supervisor iris name. */
	private String supervisorIrisName;

	/** The introducer type. */
	private String introducerType;
	
	/** The introducer UIN. */
	private String introducerUIN;
	
	/** The introducer name. */
	private String introducerName;
	
	/** The introducer UIN hash. */
	private String introducerUINHash;
	
	/** The introducer RID. */
	private Object introducerRID;
	
	/** The introducer RID hash. */
	private Object introducerRIDHash;
	
	/** The introducer fingerprint image. */
	private String introducerFingerprintImage;
	
	/** The introducer iris image. */
	private String introducerIrisImage;

}

package io.mosip.kernel.masterdata.validator.registereddevice;

public class RegisteredDeviceConstant {
	// PurposeValidator values
	public static final String REGISTRATION = "Registration";
	public static final String AUTH = "Auth";
	protected static final String PURPOSEARR[] = { REGISTRATION, AUTH };
	
	// PurposeValidator values
	public static final String REGISTERED = "registered";
	public static final String RETIRED = "retired";
	public static final String REVOKED = "revoked";
	protected static final String STATUSARR[] = { REGISTERED, RETIRED, REVOKED };
	
	// TypeValidator values
	public static final String FINGERPRINT = "Fingerprint";
	public static final String SLAB_FINGERPRINT = "Slab Fingerprint";
	public static final String IRIS_MONOCULAR = "Iris Monocular";
	public static final String IRIS_BINOCULAR = "Iris Binocular";
	public static final String FACE = "Face";
	protected static final String TYPEARR[] = { FINGERPRINT, SLAB_FINGERPRINT, IRIS_MONOCULAR, IRIS_BINOCULAR, FACE };
	
	// CertificateLevelValidator values
	public static final String L0 = "L0";
	public static final String L1 = "L1";
	protected static final String CERTIFICATELEVELARR[] = { L0, L1 };

}

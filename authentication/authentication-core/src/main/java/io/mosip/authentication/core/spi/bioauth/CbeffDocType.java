package io.mosip.authentication.core.spi.bioauth;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;

/**
 * General-purpose of {@code CbeffDocType} class used to Cbeff Documents Type
 * 
 * @author Dinesh Karuppiah.T
 */
public enum CbeffDocType {

	/**
	 * Enum for Finger
	 */
	FINGER(BiometricType.FINGER.name(), BiometricType.FINGER, CbeffConstant.FORMAT_TYPE_FINGER),
	//To be removed
	/**
	 * Enum for FMR
	 */
	FMR("FMR", BiometricType.FINGER, CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE),
	/**
	 * Enum for IRIS
	 */
	IRIS(BiometricType.IRIS.name(), BiometricType.IRIS, CbeffConstant.FORMAT_TYPE_IRIS),
	/**
	 * Enum for Face
	 */
	FACE(BiometricType.FACE.name(), BiometricType.FACE, CbeffConstant.FORMAT_TYPE_FACE);

	private String name;
	private BiometricType type;
	private long value;

	/**
	 * Constructor for Cbeff Doc type
	 * 
	 * @param name
	 * @param type
	 * @param value
	 */
	private CbeffDocType(String name, BiometricType type, long value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public BiometricType getType() {
		return type;
	}

	public long getValue() {
		return value;
	}

}

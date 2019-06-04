package io.mosip.authentication.core.spi.bioauth;

import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

/**
 * General-purpose of {@code CbeffDocType} class used to Cbeff Documents Type
 * 
 * @author Dinesh Karuppiah.T
 */
public enum CbeffDocType {

	/**
	 * Enum for FIR
	 */
	FIR(SingleType.FINGER.name(), SingleType.FINGER, CbeffConstant.FORMAT_TYPE_FINGER),
	/**
	 * Enum for FMR
	 */
	FMR("FMR", SingleType.FINGER, CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE),
	/**
	 * Enum for IRIS
	 */
	IRIS(SingleType.IRIS.name(), SingleType.IRIS, CbeffConstant.FORMAT_TYPE_IRIS),
	/**
	 * Enum for Face
	 */
	FACE(SingleType.FACE.name(), SingleType.FACE, CbeffConstant.FORMAT_TYPE_FACE);

	private String name;
	private SingleType type;
	private long value;

	/**
	 * Constructor for Cbeff Doc type
	 * 
	 * @param name
	 * @param type
	 * @param value
	 */
	private CbeffDocType(String name, SingleType type, long value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public SingleType getType() {
		return type;
	}

	public long getValue() {
		return value;
	}

}

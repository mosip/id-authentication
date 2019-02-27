package io.mosip.authentication.core.spi.bioauth;

import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

public enum CbeffDocType {

	FIR(SingleType.FINGER.name(), SingleType.FINGER, CbeffConstant.FORMAT_TYPE_FINGER), 
	FMR("FMR", SingleType.FINGER, CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE),
	IRIS(SingleType.IRIS.name(),SingleType.IRIS, CbeffConstant.FORMAT_TYPE_IRIS), 
	FACE(SingleType.FACE.name(), SingleType.FACE, CbeffConstant.FORMAT_TYPE_FACE);

	private String name;
	private SingleType type;
	private long value;

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

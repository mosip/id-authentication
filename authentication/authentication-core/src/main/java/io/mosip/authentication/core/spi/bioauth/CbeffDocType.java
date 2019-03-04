package io.mosip.authentication.core.spi.bioauth;

import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

public enum CbeffDocType {

	FIR(SingleType.FINGER.name(), CbeffConstant.FORMAT_TYPE_FINGER), 
	FMR("FMR", CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE),
	IRIS(SingleType.IRIS.name(), CbeffConstant.FORMAT_TYPE_IRIS), 
	FACE(SingleType.FACE.name(), CbeffConstant.FORMAT_TYPE_FACE);

	private String type;
	private long value;

	private CbeffDocType(String type, long value) {
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public long getValue() {
		return value;
	}

}

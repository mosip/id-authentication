package io.mosip.authentication.core.spi.bioauth;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

public class CbeffDoctypeTest {

	@Test
	public void TestFIRType() {
		assertEquals(CbeffDocType.FIR.getName(), SingleType.FINGER.name());
		assertEquals(CbeffDocType.FIR.getValue(), CbeffConstant.FORMAT_TYPE_FINGER);
	}

	@Test
	public void TestFMRType() {
		assertEquals(CbeffDocType.FMR.getName(), "FMR");
		assertEquals(CbeffDocType.FMR.getValue(), CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE);
	}

	@Test
	public void TestIRISType() {
		assertEquals(CbeffDocType.IRIS.getName(), SingleType.IRIS.name());
		assertEquals(CbeffDocType.IRIS.getValue(), CbeffConstant.FORMAT_TYPE_IRIS);
	}

	@Test
	public void TestFACEType() {
		assertEquals(CbeffDocType.FACE.getName(), SingleType.FACE.name());
		assertEquals(CbeffDocType.FACE.getValue(), CbeffConstant.FORMAT_TYPE_FACE);
	}
}

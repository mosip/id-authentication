package io.mosip.authentication.core.spi.bioauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;

public class CbeffDoctypeTest {

	@Test
	public void TestFIRType() {
		assertEquals(CbeffDocType.FINGER.getName(), BiometricType.FINGER.name());
		assertEquals(CbeffDocType.FINGER.getValue(), CbeffConstant.FORMAT_TYPE_FINGER);
	}

	@Test
	public void TestFMRType() {
		assertEquals(CbeffDocType.FMR.getName(), "FMR");
		assertEquals(CbeffDocType.FMR.getValue(), CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE);
	}

	@Test
	public void TestIRISType() {
		assertEquals(CbeffDocType.IRIS.getName(), BiometricType.IRIS.name());
		assertEquals(CbeffDocType.IRIS.getValue(), CbeffConstant.FORMAT_TYPE_IRIS);
	}

	@Test
	public void TestFACEType() {
		assertEquals(CbeffDocType.FACE.getName(), BiometricType.FACE.name());
		assertEquals(CbeffDocType.FACE.getValue(), CbeffConstant.FORMAT_TYPE_FACE);
	}
}

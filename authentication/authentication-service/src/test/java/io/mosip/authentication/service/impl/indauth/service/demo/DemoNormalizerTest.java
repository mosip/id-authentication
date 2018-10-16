package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DemoNormalizerTest {

	@Test
	public void testNameNormalizer1() {
		assertEquals("mosip", DemoNormalizer.normalizeName("mr mosip"));
	}

	@Test
	public void testNameNormalizer2() {
		assertEquals("mosip mosip", DemoNormalizer.normalizeName("Dr. mr. mrs mosip  ,    mosip*"));

	}
	
	@Test
	public void testNameNormalizer3() {
		assertEquals("mosipmosip", DemoNormalizer.normalizeName("Dr. mr. mrs mosip,mosip*"));

	}

	@Test
	public void testNameNormalizerFailureCase() {
		assertNotEquals("mosipmosip", DemoNormalizer.normalizeName("Dr. mr. mrs mosip  ,    mosip*"));

	}
	
	@Test
	public void testAddressNormalize1r() {
		assertEquals("mosip mosip", DemoNormalizer.normalizeAddress("C/o- Mr.mosip,.*      mosip"));
	}

	@Test
	public void testAddressNormalizer2() {

		assertEquals("mosip mosip", DemoNormalizer.normalizeAddress("c/o- Mr.mosip,.*  no    mosip"));
	}

}

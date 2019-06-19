package io.mosip.authentication.common.service.impl.match;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DemoNormalizerTest {

	/*@Test
	public void testNameNormalizer1() throws IdAuthenticationBusinessException {
		assertEquals("mosip", DemoNormalizer.normalizeName("mr mosip", "fra", () -> createFetcher()));
	}

	@Test
	public void testNameNormalizer2() throws IdAuthenticationBusinessException {
		assertEquals("mosip mosip", DemoNormalizer.normalizeName("Dr. mr. mrs mosip  ,    mosip*", "fra", () -> createFetcher()));

	}
	
	private Map<String, List<String>> createFetcher() {
		List<String> l = new ArrayList<>();
		l.add("Mr");
		l.add("Dr");
		l.add("Mrs");
		Map<String, List<String>> map = new HashMap<>();
		map.put("fra", l);
		return map;
	}

	@Test
	public void testNameNormalizer3() throws IdAuthenticationBusinessException {
		assertEquals("mosipmosip", DemoNormalizer.normalizeName("Dr. mr. mrs mosip,mosip*", "fra", () -> createFetcher()));

	}

	@Test
	public void testNameNormalizerFailureCase() throws IdAuthenticationBusinessException {
		assertNotEquals("mosipmosip", DemoNormalizer.normalizeName("Dr. mr. mrs mosip  ,    mosip*", "fra", () -> createFetcher()));

	}
	
	@Test
	public void testAddressNormalize1r() {
		assertEquals("mosip mosip", DemoNormalizer.normalizeAddress("C/o- Mr.mosip,.*      mosip"));
	}

	@Test
	public void testAddressNormalizer2() {

		assertEquals("mosip mosip", DemoNormalizer.normalizeAddress("c/o- Mr.mosip,.*  no    mosip"));
	}
*/
}

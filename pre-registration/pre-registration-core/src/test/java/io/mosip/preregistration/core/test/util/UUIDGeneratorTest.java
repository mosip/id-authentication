package io.mosip.preregistration.core.test.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;

/**
 * UUID Generator Test
 * 
 * @version 1.0.0
 * @author M1043226
 *
 */
public class UUIDGeneratorTest {
	UUIDGeneratorUtil u = new UUIDGeneratorUtil();

	@Test
	public void generatingIdTest() {
		assertEquals(UUIDGeneratorUtil.generateId().length(), 36);
	}

}

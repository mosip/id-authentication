package io.mosip.registration.test.util.reader;

import org.junit.Assert;
import org.junit.Test;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.util.reader.PropertyFileReader;

public class PropertyFileReaderTest {

	@Test
	public void getPropertyTest() {
		String value = PropertyFileReader.getPropertyValue(RegConstants.AES_KEY_MANAGER_ALG);
		Assert.assertEquals("AES", value);
		value = PropertyFileReader.getPropertyValue("INVALID");
		Assert.assertNull(value);
	}
}

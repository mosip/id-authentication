package io.mosip.preregistration.core.util.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.core.util.GenericUtil;

/**
 * GenericUtil Test
 * 
 * @version 1.0.0
 * @author M1043226
 *
 */

public class GenericUtilTest {

	@Autowired
	GenericUtil genericUtil;
	
	private static String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	@Test
	public void getCurrentResponseTimeTest() {
		String time=GenericUtil.getCurrentResponseTime();
		assertNotNull(time);
	}

}

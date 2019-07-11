package io.mosip.kernel.syncdata.test.utils;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.exception.DateParsingException;
import io.mosip.kernel.syncdata.utils.LocalDateTimeUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LocalDateTimeUtilTest {

	@Autowired
	LocalDateTimeUtil localDateTimeUtil;

	@Test(expected = DateParsingException.class)
	public void getLocalDateTimeFailureTest() {
		localDateTimeUtil.getLocalDateTimeFromTimeStamp(LocalDateTime.now(), "2019-09-09T09.000");
	}

	@Test
	public void getLocalDateTimeTest() {
		localDateTimeUtil.getLocalDateTimeFromTimeStamp(LocalDateTime.now(), "2019-01-09T09:00:00.000Z");
	}

	@Test(expected = DataNotFoundException.class)
	public void getLocalDateTimeExceptionTest() {
		localDateTimeUtil.getLocalDateTimeFromTimeStamp(LocalDateTime.now(), "2019-09-09T09:00:00.000Z");
	}

	@Test()
	public void getLocalDateTimeNullTest() {
		localDateTimeUtil.getLocalDateTimeFromTimeStamp(LocalDateTime.now(), null);
	}
}

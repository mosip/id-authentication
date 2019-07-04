package io.mosip.kernel.applicanttype.api.test;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.applicanttype.exception.InvalidApplicantArgumentException;
import io.mosip.kernel.core.applicanttype.spi.ApplicantType;
import io.mosip.kernel.core.util.DateUtils;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApplicantTypeTest {

	@Autowired
	private ApplicantType applicantType;

	/*
	 * @Before public void setUp() throws Exception { applicantType = new
	 * ApplicantTypeImpl(); }
	 */
	@Autowired
	private Environment env;

	private String childCode;

	private String adultCode;

	private String ageLimit;

	@Test
	public void test() throws Exception {
		childCode = env.getProperty("mosip.kernel.applicant.type.child.code");
		adultCode = env.getProperty("mosip.kernel.applicant.type.adult.code");
		ageLimit = env.getProperty("mosip.kernel.applicant.type.age.limit");
		final String pre = "test-combination-";
		for (int i = 1; i <= 16; i++) {
			String[] arr = env.getProperty(pre + i).split(",");
			Map<String, Object> map = new HashMap<>();
			map.put("individualTypeCode", arr[0]);
			map.put("dateofbirth", DateUtils.formatToISOString(getDobDate(arr[2])));
			map.put("genderCode", arr[1]);
			map.put("biometricAvailable", arr[3]);
			String code = applicantType.getApplicantType(map);
			assertTrue(code.equals(createCode(i)));
		}
	}

	@Test(expected = InvalidApplicantArgumentException.class)
	public void testInvalidApplicantArgumentException() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("individualTypeCode", null);
		map.put("dateofbirth", null);
		map.put("genderCode", null);
		map.put("biometricAvailable", null);
		String code = applicantType.getApplicantType(map);
		assertTrue(code.equals("001"));
	}

	@Test(expected = InvalidApplicantArgumentException.class)
	public void testInvalidApplicantArgumentExceptionAge() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("individualTypeCode", "FR");
		map.put("dateofbirth", "sfhdsfdsugfdsfuygDS");
		map.put("genderCode", "MLE");
		map.put("biometricAvailable", "false");
		String code = applicantType.getApplicantType(map);
		assertTrue(code.equals("001"));
	}

	private String createCode(int i) {

		if (i < 10 && i > 0) {
			return "00" + i;
		} else if (i > 9 && i < 17) {
			return "0" + i;
		}

		return null;
	}

	private LocalDateTime getDobDate(String string) {
		int age = Integer.parseInt(ageLimit);
		LocalDateTime ldt = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime();

		if (string.equals(childCode)) {
			return ldt.minusYears(age - 2);
		} else if (string.equals(adultCode)) {
			return ldt.minusYears(age + 1);
		}
		return null;
	}

}
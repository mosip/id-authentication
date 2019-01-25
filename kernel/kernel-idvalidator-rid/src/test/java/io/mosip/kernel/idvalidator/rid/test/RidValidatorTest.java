package io.mosip.kernel.idvalidator.rid.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidValidatorTest {

	@Autowired
	RidValidator<String> ridValidatorImpl;

	@Value("${mosip.kernel.rid.test.center-id}")
	private String centerId;

	@Value("${mosip.kernel.rid.test.machine-id}")
	private String machineId;

	@Value("${mosip.kernel.rid.centerid-length}")
	private int centerIdLength;

	@Value("${mosip.kernel.rid.machineid-length}")
	private int machineIdLength;

	@Value("${mosip.kernel.rid.sequence-length}")
	private int sequenceLength;

	@Value("${mosip.kernel.rid.timestamp-length}")
	private int timeStampLength;

	@Value("${mosip.kernel.rid.test.valid-rid}")
	private String validRid;

	@Value("${mosip.kernel.rid.test.invalid-centerid-rid}")
	private String invalidCenterIdRid;

	@Value("${mosip.kernel.rid.test.invalid-machineid-rid}")
	private String invalidMachineIdRid;

	@Value("${mosip.kernel.rid.test.invalid-month-rid}")
	private String invalidMonthRid;

	@Value("${mosip.kernel.rid.test.invalid-date-rid}")
	private String invalidDateRid;

	@Value("${mosip.kernel.rid.test.invalid-hour-rid}")
	private String invalidHourRid;

	@Value("${mosip.kernel.rid.test.invalid-minute-rid}")
	private String invalidMinuteRid;

	@Value("${mosip.kernel.rid.test.invalid-second-rid}")
	private String invalidSecondRid;

	@Value("${mosip.kernel.rid.test.invalid-alpha-numeric-rid}")
	private String invalidAlphaNumericRid;

	@Value("${mosip.kernel.rid.test.invalid-length-rid}")
	private String invalidLengthRid;

	@Value("${mosip.kernel.rid.test.valid-custom-sequence-rid}")
	private String validCustomSequenceRid;

	@Test
	public void validRidTest() {
		assertThat(ridValidatorImpl.validateId(validRid, centerId, machineId), is(true));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidCenterIdInRidTest() {
		ridValidatorImpl.validateId(invalidCenterIdRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMachineIdInRidTest() {
		ridValidatorImpl.validateId(invalidMachineIdRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMonthInTimestampOfRidTest() {

		ridValidatorImpl.validateId(invalidMonthRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidDateInTimestampOfRidTest() {

		ridValidatorImpl.validateId(invalidDateRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidHourInTimestampOfRidTest() {

		ridValidatorImpl.validateId(invalidHourRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMinuteInTimestampOfRidTest() {

		ridValidatorImpl.validateId(invalidMinuteRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidSecondIntimestampOfRidTest() {

		ridValidatorImpl.validateId(invalidSecondRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTest() {

		ridValidatorImpl.validateId(invalidAlphaNumericRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void lengthOfRidTest() {

		ridValidatorImpl.validateId(invalidLengthRid, centerId, machineId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidLengthTest() {

		ridValidatorImpl.validateId(invalidLengthRid);
	}

	@Test
	public void validRidIsNumericTest() {

		assertThat(ridValidatorImpl.validateId(validRid), is(true));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTimestampTest() {

		assertThat(ridValidatorImpl.validateId(invalidMonthRid), is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidDateTest() {

		assertThat(ridValidatorImpl.validateId(invalidDateRid), is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTimeTest() {

		assertThat(ridValidatorImpl.validateId(invalidHourRid), is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void nonNumericRidTest() {

		assertThat(ridValidatorImpl.validateId(invalidAlphaNumericRid), is(false));
	}

	@Test
	public void validRidCenterIdMachineIdWithCustomLengthTest() {

		assertThat(ridValidatorImpl.validateId(validRid, centerId, machineId, centerIdLength, machineIdLength, 5,
				timeStampLength), is(true));
	}

	@Test
	public void validRidWithCustomLengthTest() {

		assertThat(
				ridValidatorImpl.validateId(validRid, centerIdLength, machineIdLength, sequenceLength, timeStampLength),
				is(true));
	}

	@Test(expected = InvalidIDException.class)
	public void validRidWithInvalidCustomLengthTest() {

		assertThat(ridValidatorImpl.validateId(validRid, -1, machineIdLength, sequenceLength, timeStampLength),
				is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void validRidWithInvalidSequenceTest() {

		ridValidatorImpl.validateId(validRid, centerId, machineId, centerIdLength, machineIdLength, sequenceLength, 13);
	}

	@Test
	public void validRidWithCustomSequenceTest() {

		ridValidatorImpl.validateId(validCustomSequenceRid, "278476", "573621", 6, 6, 3, timeStampLength);
	}

	@Test(expected = InvalidIDException.class)
	public void validRidWithInvalidCustomSequenceTest() {

		ridValidatorImpl.validateId(validCustomSequenceRid, "278476", "573621", 6, 6, 2, timeStampLength);
	}
}

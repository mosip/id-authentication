package io.mosip.kernel.idvalidator.test.rid;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidValidatorTest {

	@Autowired
	RidValidator<String> ridValidatorImpl;

	String centerId = "27847";

	String dongleId = "65736";

	@Test
	public void validRidTest() {
		String rid = "27847657360002520181208183050";
		assertThat(ridValidatorImpl.validateId(rid, centerId, dongleId), is(true));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidCenterIdInRidTest() {
		String rid = "27846657360002520181208183050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidDongleIdInRidTest() {
		String rid = "27847657340002520181208183050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMonthInTimestampOfRidTest() {
		String rid = "27847657360002520181308183050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidDateInTimestampOfRidTest() {
		String rid = "27847657360002520181232183050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidHourInTimestampOfRidTest() {
		String rid = "27847657360002520181208253050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMinuteInTimestampOfRidTest() {
		String rid = "27847657360002520181208187050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidSecondIntimestampOfRidTest() {
		String rid = "27847657360002520181208183070";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTest() {
		String rid = "278476573600A2520181208183050";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void lengthOfRidTest() {
		String rid = "2784765736000252018120818305";
		ridValidatorImpl.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidLengthTest() {
		String rid = "2784765736000252018120818305";
		ridValidatorImpl.validateId(rid);
	}

	@Test
	public void validRidIsNumericTest() {
		String rid = "27847657360002520181208183059";
		assertThat(ridValidatorImpl.validateId(rid), is(true));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTimestampTest() {
		String rid = "27847657360002520181308183059";
		assertThat(ridValidatorImpl.validateId(rid), is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidDateTest() {
		String rid = "27847657360002520181232183059";
		assertThat(ridValidatorImpl.validateId(rid), is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTimeTest() {
		String rid = "27847657360002520181208253059";
		assertThat(ridValidatorImpl.validateId(rid), is(false));
	}

	@Test(expected = InvalidIDException.class)
	public void nonNumericRidTest() {
		String rid = "278476573600A2520181208183050";
		assertThat(ridValidatorImpl.validateId(rid), is(false));
	}
}

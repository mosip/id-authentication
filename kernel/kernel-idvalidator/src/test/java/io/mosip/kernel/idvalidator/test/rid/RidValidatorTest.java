package io.mosip.kernel.idvalidator.test.rid;

import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.CoreMatchers.is;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidValidatorTest {

	@Autowired
	RidValidatorImpl ridValidator;

	String centerId = "27847";

	String dongleId = "65736";

	@Test
	public void validRidTest() {
		String rid = "27847657360002520181208183050";
		assertThat(ridValidator.validateId(rid, centerId, dongleId), is(true));
	}

	@Test(expected = InvalidIDException.class)
	public void invalidCenterIdInRidTest() {
		String rid = "27846657360002520181208183050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidDongleIdInRidTest() {
		String rid = "27847657340002520181208183050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMonthInTimestampOfRidTest() {
		String rid = "27847657360002520181308183050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidDateInTimestampOfRidTest() {
		String rid = "27847657360002520181232183050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidHourInTimestampOfRidTest() {
		String rid = "27847657360002520181208253050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidMinuteInTimestampOfRidTest() {
		String rid = "27847657360002520181208187050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidSecondIntimestampOfRidTest() {
		String rid = "27847657360002520181208183070";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void invalidRidTest() {
		String rid = "278476573600A2520181208183050";
		ridValidator.validateId(rid, centerId, dongleId);
	}

	@Test(expected = InvalidIDException.class)
	public void lengthOfRidTest() {
		String rid = "2784765736000252018120818305";
		ridValidator.validateId(rid, centerId, dongleId);
	}
}

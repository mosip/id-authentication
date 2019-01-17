package io.mosip.preregistration.core.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;

public class ValidationUtilTest {
	Map<String, String> requestMap = null;
	Map<String, String> requiredRequestMap = null;

	@Before
	public void setUp() throws Exception {
		requestMap = new HashMap<>();
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, Mockito.anyString());

		requiredRequestMap = new HashMap<>();
		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "1.0");
		requiredRequestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requiredRequestMap.put(RequestCodes.REQUEST, Mockito.anyString());
	}

	@Test
	public void emailValidatorTest() {
		String loginId = "user@gmail.com";
		assertThat(ValidationUtil.emailValidator(loginId), is(true));
	}

	@Test
	public void emailValidatorFailureTest() {
		String loginId = "user@gmailcom";
		assertThat(ValidationUtil.emailValidator(loginId), is(false));
	}

	@Test
	public void phoneValidatorTest() {
		String loginId = "9998867755";
		assertThat(ValidationUtil.phoneValidator(loginId), is(true));
	}

	@Test
	public void phoneValidatorFailureTest() {
		String loginId = "999886775";
		assertThat(ValidationUtil.phoneValidator(loginId), is(false));
	}

	@Test
	public void requestValidatorSuccessTest() {
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, "{request}");

		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "1.0");

		assertThat(ValidationUtil.requestValidator(requestMap, requiredRequestMap), is(true));
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest1() {
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, "{request}");

		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration.create");
		requiredRequestMap.put(RequestCodes.VER, "1.0");
		Mockito.when(ValidationUtil.requestValidator(requestMap, requiredRequestMap))
				.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest2() {
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, "{request}");

		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "0.1");
		Mockito.when(ValidationUtil.requestValidator(requestMap, requiredRequestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest4() {
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, "");

		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "1.0");
		requiredRequestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requiredRequestMap.put(RequestCodes.REQUEST, "{request}");
		Mockito.when(ValidationUtil.requestValidator(requestMap, requiredRequestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest5() {
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, null);

		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "1.0");
		requiredRequestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requiredRequestMap.put(RequestCodes.REQUEST, "{request}");
		Mockito.when(ValidationUtil.requestValidator(requestMap, requiredRequestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest6() {
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, null);
		requestMap.put(RequestCodes.REQUEST, "{request}");

		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "1.0");
		requiredRequestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requiredRequestMap.put(RequestCodes.REQUEST, "{request}");
		Mockito.when(ValidationUtil.requestValidator(requestMap, requiredRequestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}
	
//--------------------------------------------------------
	@Test
	public void requstParamValidatorSuccessTest() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		assertThat(ValidationUtil.requstParamValidator(requestMap), is(true));
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest1() {
		requestMap.put(RequestCodes.USER_ID, "");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap))
				.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest2() {
		requestMap.put(RequestCodes.USER_ID, null);
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest3() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest4() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, null);
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest5() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest6() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, null);
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap))
		.thenThrow(InvalidRequestParameterException.class);
	}
	
	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest7() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		assertThat(ValidationUtil.requstParamValidator(requestMap), is(true));
	}
	
	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest8() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, null);
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		assertThat(ValidationUtil.requstParamValidator(requestMap), is(true));
	}
	
	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest9() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "");
		assertThat(ValidationUtil.requstParamValidator(requestMap), is(true));
	}
	
	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest10() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, null);
		assertThat(ValidationUtil.requstParamValidator(requestMap), is(true));
	}
}

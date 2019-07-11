package io.mosip.preregistration.core.util.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.ValidationUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationUtilTest {
	Map<String, String> requestMap = null;
	Map<String, String> requiredRequestMap = null;
	MainRequestDTO<String> mainRequest = new MainRequestDTO<>();

	@Autowired
	ValidationUtil validationUtil;	

	@Before
	public void setUp() throws Exception {
		requestMap = new HashMap<>();
		requestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requestMap.put(RequestCodes.VER, "1.0");
		requestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requestMap.put(RequestCodes.REQUEST, "");

		requiredRequestMap = new HashMap<>();
		requiredRequestMap.put(RequestCodes.ID, "mosip.pre-registration");
		requiredRequestMap.put(RequestCodes.VER, "1.0");
		requiredRequestMap.put(RequestCodes.REQ_TIME, "2018-12-19T18:52:16.239Z");
		requiredRequestMap.put(RequestCodes.REQUEST, "");

		mainRequest.setId("mosip.pre-registration");
		mainRequest.setRequest("");
		mainRequest.setRequesttime(new Date());
		mainRequest.setVersion("1.0");

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
		mainRequest.setId("mosip.pre-registration");
		mainRequest.setRequest("Admin");
		mainRequest.setRequesttime(new Date());
		mainRequest.setVersion("1.0");
		assertThat(ValidationUtil.requestValidator(mainRequest), is(true));
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest1() {
		mainRequest.setVersion(null);
		Mockito.when(ValidationUtil.requestValidator(mainRequest)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest2() {
		mainRequest.setId(null);
		Mockito.when(ValidationUtil.requestValidator(mainRequest)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest4() {
		mainRequest.setRequest(null);
		Mockito.when(ValidationUtil.requestValidator(mainRequest)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requestValidatorFailureTest5() {
		mainRequest.setRequesttime(null);
		Mockito.when(ValidationUtil.requestValidator(mainRequest)).thenThrow(InvalidRequestParameterException.class);
	}

	// --------------------------------------------------------
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
		Mockito.when(ValidationUtil.requstParamValidator(requestMap)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest2() {
		requestMap.put(RequestCodes.USER_ID, null);
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest3() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "");
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest4() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, null);
		requestMap.put(RequestCodes.STATUS_CODE, "Pending_Appointment");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest5() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, "");
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap)).thenThrow(InvalidRequestParameterException.class);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void requstParamValidatorFailureTest6() {
		requestMap.put(RequestCodes.USER_ID, "9900806086");
		requestMap.put(RequestCodes.PRE_REGISTRATION_ID, "70694681371453");
		requestMap.put(RequestCodes.STATUS_CODE, null);
		requestMap.put(RequestCodes.FROM_DATE, "2018-12-19 18:52:16");
		requestMap.put(RequestCodes.TO_DATE, "2018-12-19 19:52:16");
		Mockito.when(ValidationUtil.requstParamValidator(requestMap)).thenThrow(InvalidRequestParameterException.class);
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

	// @Test
	// public void isValidPreIdSuccessTest() {
	// String preId="12345678901234";
	// assertThat(ValidationUtil.isvalidPreRegId(preId), is(true));
	// }
	//
	// @Test(expected = InvalidRequestParameterException.class)
	// public void isValidPreIdFailureTest() {
	// String preId="12345678901";
	// assertThat(ValidationUtil.isvalidPreRegId(preId), is(false));
	// }

	@Test
	public void isValidLangCodeSuccessTest() {
		String langCode = "fra";
		assertThat(validationUtil.langvalidation(langCode), is(true));
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void isValidLangCodeFailureTest() {
		String langCode = "fraaa";
		validationUtil.langvalidation(langCode);
	}
}

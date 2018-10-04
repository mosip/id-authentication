package io.mosip.registration.test.login;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	LoginWithOTPtest.class,
	RestClientUtilTest.class,
	ServiceDelegateUtilTest.class
})
public class LoginTestSuite {

}

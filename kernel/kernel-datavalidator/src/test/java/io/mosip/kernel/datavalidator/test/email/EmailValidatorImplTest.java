
/**
 * 
 */
package io.mosip.kernel.datavalidator.test.email;

/**
 * 
 */
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.datavalidator.exception.InvalideEmailException;
import io.mosip.kernel.core.datavalidator.spi.EmailValidator;

/**
 * Test class for testing emailvalidatorImpl class
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailValidatorImplTest {

	@Autowired
	EmailValidator emailValidatorImpl;

	@Value("${mosip.kernel.email.test.true}")
	String emailTrue;

	@Value("${mosip.kernel.email.test.null}")
	String emailNull;

	@Value("${mosip.kernel.email.test.empty}")
	String emailEmpty;

	@Value("${mosip.kernel.email.test.max-length}")
	String emailMaxLength;

	@Value("${mosip.kernel.email.test.min-length}")
	String emailMinLength;

	@Value("${mosip.kernel.email.test.domain-max-length}")
	String domainMinLength;

	@Value("${mosip.kernel.email.test.domain-min-length}")
	String domainMaxLength;

	@Value("${mosip.kernel.email.test.domain.invalide-special-char}")
	String domainInvalideSpecialChar;

	@Value("${mosip.kernel.email.test.invalide-special-char}")
	String invalideSpecialChar;

	@Value("${mosip.kernel.email.test.space}")
	String emailWithSpace;

	@Test
	public void emailvalidatorImplTestTrue() {

		assertEquals(true, emailValidatorImpl.validateEmail(emailTrue));
	}

	@Test(expected = InvalideEmailException.class)
	public void testNull() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(emailNull);
	}

	@Test(expected = InvalideEmailException.class)
	public void testEmpty() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(emailEmpty);
	}

	@Test(expected = InvalideEmailException.class)
	public void testMaxLenght() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(emailMaxLength);
	}

	@Test(expected = InvalideEmailException.class)
	public void testMinLenght() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(emailMinLength);
	}

	@Test(expected = InvalideEmailException.class)
	public void testMaxDomainLenght() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(domainMaxLength);
	}

	@Test(expected = InvalideEmailException.class)
	public void testMinDomainLenght() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(domainMinLength);
	}

	@Test(expected = InvalideEmailException.class)
	public void testInvalideSpecialCharDomainName() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(domainInvalideSpecialChar);
	}

	@Test(expected = InvalideEmailException.class)
	public void testInvalideSpecialChar() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(invalideSpecialChar);
	}

	@Test(expected = InvalideEmailException.class)
	public void testSpace() throws InvalideEmailException {

		emailValidatorImpl.validateEmail(emailWithSpace);
	}

}

package io.mosip.authentication.core.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Class MaskUtilTest.
 * 
 * @author Manoj SP
 *
 */
public class MaskUtilTest {

    /**
     * Test mask email.
     * @throws IdAuthenticationBusinessException 
     */
    @Test
    public void testMaskEmail() throws IdAuthenticationBusinessException {
	assertEquals("XXaXXhXXh@mail.com", MaskUtil.maskEmail("umamahesh@mail.com"));
    }

    /**
     * Test mask mobile number.
     * @throws IdAuthenticationBusinessException 
     */
    @Test
    public void testMaskMobileNumber() throws IdAuthenticationBusinessException {
	assertEquals("XXXXXX7890", MaskUtil.maskMobile("1234567890"));
    }
    
    @Test
    public void testGenerateMaskValue() {
	assertEquals("XXXX34654512324", MaskUtil.generateMaskValue("123234654512324", 4));
    }
    
    @Test
    public void testGenerateMaskValue2() {
	assertNotEquals("XXXX34654512324", MaskUtil.generateMaskValue("123234654512324", 8));
    }
    
    @Test
    public void testMaskEmailWithNull() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
	MaskUtil.maskEmail(null);
    		});
	}
    
    @Test
    public void testMaskEmailWithEmpty() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
	MaskUtil.maskEmail("");
    		});
	}
    
    @Test
    public void testMaskMobileWithNull() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
	MaskUtil.maskMobile(null);
    		});
	}
    
    @Test
    public void testMaskMobileWithEmpty() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
	MaskUtil.maskMobile("");
    		});
	}
    
    @Test
    public void testGenerateMaskValueWithMaskNoGreaterThanLength() {
	String result = MaskUtil.generateMaskValue("123", 10);
	assertEquals("XXX", result);
    }
    
    @Test
    public void testGenerateMaskValueWithZeroMaskNo() {
	String result = MaskUtil.generateMaskValue("12345", 0);
	assertEquals("12345", result);
    }
    
    @Test
    public void testMaskEmailWithShortEmail() throws IdAuthenticationBusinessException {
	String result = MaskUtil.maskEmail("ab@mail.com");
	assertNotNull(result);
    }

}

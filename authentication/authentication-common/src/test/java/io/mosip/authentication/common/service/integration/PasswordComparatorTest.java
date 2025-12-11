package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PasswordComparatorTest {

    @InjectMocks
    private PasswordComparator passwordComparator;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test successful password match
     */
    @Test
    public void testMatchPassword_Success() throws Exception {
        String password = "test123";
        String salt = "xyz";
        String storedHash = "HASHED";

        // Mock hashing function
        when(securityManager.generateArgon2Hash(password, salt))
                .thenReturn("HASHED");

        boolean result = passwordComparator.matchPasswordFunction(password, storedHash, salt);

        assertTrue(result);
        verify(securityManager, times(1)).generateArgon2Hash(password, salt);
    }

    /**
     * Test password mismatch
     */
    @Test
    public void testMatchPassword_Fail() throws Exception {
        String password = "test123";
        String salt = "xyz";

        when(securityManager.generateArgon2Hash(password, salt))
                .thenReturn("WRONG_HASH");

        boolean result = passwordComparator.matchPasswordFunction(password, "EXPECTED_HASH", salt);

        assertFalse(result);
    }

    /**
     * Test exception from securityManager -->
     * must rethrow IdAuthenticationBusinessException
     */
    @Test(expected = IdAuthenticationBusinessException.class)
    public void testMatchPassword_Exception() throws Exception {
        String password = "test123";
        String salt = "xyz";

        when(securityManager.generateArgon2Hash(password, salt))
                .thenThrow(new RuntimeException("Hash failure"));

        passwordComparator.matchPasswordFunction(password, "ANY", salt);
    }
}

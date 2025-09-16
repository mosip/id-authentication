package io.mosip.authentication.service.kyc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class KycExchangeFilterTest {
    @Autowired
    EnvUtil env;
    KycExchangeFilter kycExchangeFilter = new KycExchangeFilter();
    @Autowired
    ObjectMapper mapper;

    byte[] key = {48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48,
            -126, 1, 10, 2, -126, 1, 1, 0, -56, 41, -49, 92, 30, -78, 87, 22, -103, -23, -14, 106, -89, 84, -73, 51,
            -69, -10, 75, -88, 94, 23, -106, -67, -4, 53, -91, -74, -64, 101, 70, 113, 100, 14, 67, 22, -27, -121, -45,
            -11, -107, 64, -56, -101, 97, 62, 64, 65, 57, -18, -47, 96, -88, 38, -77, 107, 125, 39, -52, -83, -67, -27,
            -20, -9, 27, -15, 69, 78, 74, -36, -114, 20, -121, -119, -55, 26, -50, -69, 16, -21, 84, 6, 66, 117, -39, 0,
            17, -39, -15, 49, -114, -101, -106, -113, -98, -81, 3, 18, -109, -122, -57, -19, 27, 2, 53, 8, -53, -11,
            -73, -84, 9, 55, -33, 8, -93, 16, -103, -4, 117, -35, -63, 43, -97, -74, 48, 101, -108, 38, -54, 18, -36,
            105, -39, 21, 117, -81, 42, -15, -95, 79, -124, -59, -128, 64, 82, 85, -68, -79, 24, -84, 25, -113, 125,
            -17, -20, -57, 50, -63, -13, -79, -60, 81, -104, 111, -84, 62, 123, -40, 12, -7, 65, -5, 23, 3, -91, -17, 2,
            49, -56, 73, 35, 46, -97, 38, -18, 14, 10, 26, 11, 122, 124, 124, -20, -110, -9, 26, 122, 59, 74, -123, -86,
            97, 0, 48, -14, 65, -50, -49, 40, 90, 65, 127, 75, 110, -76, 127, -41, 80, 6, 30, 61, -4, 27, -63, -100,
            115, -79, -87, 107, 66, 73, -14, 13, -98, -108, 55, 26, 58, -72, -103, -35, 46, -15, 45, 23, 84, 93, 31, 44,
            -112, -41, 95, 22, 14, -114, 15, 2, 3, 1, 0, 1};
    @Before
    public void before() {
        ReflectionTestUtils.setField(kycExchangeFilter, "mapper", mapper);
        ReflectionTestUtils.setField(kycExchangeFilter, "env", env);
    }
    @Test
    public void checkAllowedAuthTypeBasedOnPolicyTest() {
        AuthPolicy authPolicy = new AuthPolicy();
        authPolicy.setAuthType("demo");
        authPolicy.setMandatory(true);
        try {
            ReflectionTestUtils.invokeMethod(kycExchangeFilter, "checkAllowedAuthTypeBasedOnPolicy", new HashMap<>(), Collections.singletonList(authPolicy));
        } catch (UndeclaredThrowableException e) {
            String detailMessage = e.getUndeclaredThrowable().getMessage();
            String[] error = detailMessage.split("-->");
            assertEquals("IDA-MPA-026", error[0].trim());
            assertEquals("Partner is unauthorised for KYC-Exchange", error[1].trim());
            assertTrue(e.getCause().getClass().equals(IdAuthenticationAppException.class));
        }
    }
}
 
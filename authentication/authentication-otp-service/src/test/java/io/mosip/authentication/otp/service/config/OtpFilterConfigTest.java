package io.mosip.authentication.otp.service.config;

import io.mosip.authentication.otp.service.filter.OTPFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OtpFilterConfigTest {
    @Test
    void testGetOtpFilter() {
        OtpFilterConfig config = new OtpFilterConfig();
        FilterRegistrationBean<OTPFilter> bean = config.getOtpFilter();

        assertNotNull(bean);
        assertNotNull(bean.getFilter());
        assertTrue(bean.getFilter() instanceof OTPFilter);
        assertEquals(1, bean.getUrlPatterns().size());
        assertTrue(bean.getUrlPatterns().contains("/*"));
    }

}

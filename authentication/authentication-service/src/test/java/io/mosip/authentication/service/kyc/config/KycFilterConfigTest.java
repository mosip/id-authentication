package io.mosip.authentication.service.kyc.config;

import io.mosip.authentication.service.kyc.filter.IdentityKeyBindingFilter;
import io.mosip.authentication.service.kyc.filter.KycAuthFilter;
import io.mosip.authentication.service.kyc.filter.KycAuthenticationFilter;
import io.mosip.authentication.service.kyc.filter.KycExchangeFilter;
import io.mosip.authentication.service.kyc.filter.VciExchangeFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KycFilterConfigTest {

    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void setUp() {
        context = new AnnotationConfigApplicationContext(KycFilterConfig.class);
    }

    @AfterAll
    static void tearDown() {
        context.close();
    }

    @Test
    void testGetEkycFilter() {
        FilterRegistrationBean<?> bean = context.getBean("getEkycFilter", FilterRegistrationBean.class);
        assertNotNull(bean);
        assertTrue(bean.getFilter() instanceof KycAuthenticationFilter);
        assertTrue(bean.getUrlPatterns().contains("/kyc/*"));
    }

    @Test
    void testGetKycAuthFilter() {
        FilterRegistrationBean<?> bean = context.getBean("getKycAuthFilter", FilterRegistrationBean.class);
        assertNotNull(bean);
        assertTrue(bean.getFilter() instanceof KycAuthFilter);
        assertTrue(bean.getUrlPatterns().contains("/kyc-auth/*"));
    }

    @Test
    void testGetKycExchangeFilter() {
        FilterRegistrationBean<?> bean = context.getBean("getKycExchangeFilter", FilterRegistrationBean.class);
        assertNotNull(bean);
        assertTrue(bean.getFilter() instanceof KycExchangeFilter);
        assertTrue(bean.getUrlPatterns().contains("/kyc-exchange/*"));
    }

    @Test
    void testGetKeyBindingFilter() {
        FilterRegistrationBean<?> bean = context.getBean("getKeyBindingFilter", FilterRegistrationBean.class);
        assertNotNull(bean);
        assertTrue(bean.getFilter() instanceof IdentityKeyBindingFilter);
        assertTrue(bean.getUrlPatterns().contains("/identity-key-binding/*"));
    }

    @Test
    void testGetVciExchangeFilter() {
        FilterRegistrationBean<?> bean = context.getBean("getVciExchangeFilter", FilterRegistrationBean.class);
        assertNotNull(bean);
        assertTrue(bean.getFilter() instanceof VciExchangeFilter);
        assertTrue(bean.getUrlPatterns().contains("/vci-exchange/*"));
    }
}

package io.mosip.authentication.service.config;

import io.mosip.authentication.common.service.filter.ExternalAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthFilterConfigTest {

    @Test
    void testGetIdAuthFilterBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AuthFilterConfig.class);
        FilterRegistrationBean<?> bean = context.getBean(FilterRegistrationBean.class);

        assertNotNull(bean);
        assertTrue(bean.getFilter() instanceof ExternalAuthFilter);
        assertTrue(bean.getUrlPatterns().contains("/auth/*"));

        context.close();
    }
}

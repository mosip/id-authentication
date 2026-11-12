package io.mosip.authentication.service.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import io.mosip.authentication.common.service.filter.DefaultAuthTypeFilter;
import io.mosip.authentication.common.service.filter.DefaultInternalFilter;
import io.mosip.authentication.common.service.filter.InternalAuthFilter;
import io.mosip.authentication.common.service.filter.InternalAuthenticationFilter;
import io.mosip.authentication.common.service.filter.InternalOtpFilter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InternalAuthFilterConfigTest {

	@InjectMocks
	private InternalAuthFilterConfig internalAuthFilterConfig;
	
	@Test
	public void testGetInternalAuthFilter() {
		FilterRegistrationBean<InternalAuthFilter> bean = internalAuthFilterConfig.getInternalAuthFilter();
		
		assertNotNull(bean, "Filter registration bean should not be null");
		assertNotNull(bean.getFilter(), "Filter should not be null");
		assertTrue( 
			bean.getFilter() instanceof InternalAuthFilter, "Filter should be instance of InternalAuthFilter");
		assertEquals(1, bean.getUrlPatterns().size(), "Should have correct URL pattern");
		assertTrue(bean.getUrlPatterns().contains("/internal/auth"), "Should match /internal/auth pattern");
	}
	
	@Test
	public void testGetInternalAuthenticationFilter() {
		FilterRegistrationBean<InternalAuthenticationFilter> bean = 
			internalAuthFilterConfig.getInternalAuthenticationFilter();
		
		assertNotNull(bean, "Filter registration bean should not be null");
		assertNotNull(bean.getFilter(), "Filter should not be null");
		assertTrue( 
			bean.getFilter() instanceof InternalAuthenticationFilter, "Filter should be instance of InternalAuthenticationFilter");
		assertEquals(1, bean.getUrlPatterns().size(), "Should have correct URL pattern");
		assertTrue(bean.getUrlPatterns().contains("/internal/verifyidentity"), "Should match /internal/verifyidentity pattern");
	}
	
	@Test
	public void testGetInternalOTPFilter() {
		FilterRegistrationBean<InternalOtpFilter> bean = internalAuthFilterConfig.getInternalOTPFilter();
		
		assertNotNull(bean, "Filter registration bean should not be null");
		assertNotNull(bean.getFilter(), "Filter should not be null");
		assertTrue( 
			bean.getFilter() instanceof InternalOtpFilter, "Filter should be instance of InternalOtpFilter");
		assertEquals(1, bean.getUrlPatterns().size(), "Should have correct URL pattern");
		assertTrue(bean.getUrlPatterns().contains("/internal/otp"), "Should match /internal/otp pattern");
	}
	
	@Test
	public void testGetDefaultInternalFilter() {
		FilterRegistrationBean<DefaultInternalFilter> bean = 
			internalAuthFilterConfig.getDefaultInternalFilter();
		
		assertNotNull(bean, "Filter registration bean should not be null");
		assertNotNull(bean.getFilter(), "Filter should not be null");
		assertTrue( 
			bean.getFilter() instanceof DefaultInternalFilter, "Filter should be instance of DefaultInternalFilter");
		assertTrue( 
			bean.getUrlPatterns().stream().anyMatch(pattern -> pattern.contains("authTransactions")), "Should have URL pattern for auth transactions");
		assertNotNull(bean.getInitParameters(), "Should have init parameters");
	}
	
	@Test
	public void testGetDefaultAuthtypeFilter() {
		FilterRegistrationBean<DefaultAuthTypeFilter> bean = 
			internalAuthFilterConfig.getDefaultAuthtypeFilter();
		
		assertNotNull(bean, "Filter registration bean should not be null");
		assertNotNull(bean.getFilter(), "Filter should not be null");
		assertTrue( 
			bean.getFilter() instanceof DefaultAuthTypeFilter, "Filter should be instance of DefaultAuthTypeFilter");
		assertTrue(
                			bean.getUrlPatterns().stream().anyMatch(pattern -> pattern.equals("/authtypes/status/*")), "Should have URL pattern /authtypes/status/* with wildcard");
        assertNotNull(bean.getInitParameters(), "Should have init parameters");
        assertTrue(bean.getInitParameters().containsKey("IDType"), "Should have IDType init parameter");
        assertTrue(bean.getInitParameters().containsKey("ID"), "Should have ID init parameter");
	}
	
	@Test
	public void testGetDefaultAuthtypeStatusFilter() {
		FilterRegistrationBean<DefaultAuthTypeFilter> bean = 
			internalAuthFilterConfig.getDefaultAuthtypeStatusFilter();
		
		assertNotNull(bean, "Filter registration bean should not be null");
		assertNotNull(bean.getFilter(), "Filter should not be null");
		assertTrue( 
			bean.getFilter() instanceof DefaultAuthTypeFilter, "Filter should be instance of DefaultAuthTypeFilter");
		assertTrue(
                			bean.getUrlPatterns().stream().anyMatch(pattern -> pattern.equals("/authtypes/status")), "Should have exact URL pattern /authtypes/status without wildcard");
        assertTrue(
                			bean.getInitParameters() == null || bean.getInitParameters().isEmpty(), "Should have no init parameters");
	}
}

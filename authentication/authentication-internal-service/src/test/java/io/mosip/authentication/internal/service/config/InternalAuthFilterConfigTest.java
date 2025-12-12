package io.mosip.authentication.internal.service.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import io.mosip.authentication.common.service.filter.DefaultAuthTypeFilter;
import io.mosip.authentication.common.service.filter.DefaultInternalFilter;
import io.mosip.authentication.common.service.filter.InternalAuthFilter;
import io.mosip.authentication.common.service.filter.InternalAuthenticationFilter;
import io.mosip.authentication.common.service.filter.InternalOtpFilter;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InternalAuthFilterConfigTest {

	@InjectMocks
	private InternalAuthFilterConfig internalAuthFilterConfig;
	
	@Test
	public void testGetInternalAuthFilter() {
		FilterRegistrationBean<InternalAuthFilter> bean = internalAuthFilterConfig.getInternalAuthFilter();
		
		assertNotNull("Filter registration bean should not be null", bean);
		assertNotNull("Filter should not be null", bean.getFilter());
		assertTrue("Filter should be instance of InternalAuthFilter", 
			bean.getFilter() instanceof InternalAuthFilter);
		assertEquals("Should have correct URL pattern", 1, bean.getUrlPatterns().size());
		assertTrue("Should match /auth pattern", bean.getUrlPatterns().contains("/auth"));
	}
	
	@Test
	public void testGetInternalAuthenticationFilter() {
		FilterRegistrationBean<InternalAuthenticationFilter> bean = 
			internalAuthFilterConfig.getInternalAuthenticationFilter();
		
		assertNotNull("Filter registration bean should not be null", bean);
		assertNotNull("Filter should not be null", bean.getFilter());
		assertTrue("Filter should be instance of InternalAuthenticationFilter", 
			bean.getFilter() instanceof InternalAuthenticationFilter);
		assertEquals("Should have correct URL pattern", 1, bean.getUrlPatterns().size());
		assertTrue("Should match /verifyidentity pattern", bean.getUrlPatterns().contains("/verifyidentity"));
	}
	
	@Test
	public void testGetInternalOTPFilter() {
		FilterRegistrationBean<InternalOtpFilter> bean = internalAuthFilterConfig.getInternalOTPFilter();
		
		assertNotNull("Filter registration bean should not be null", bean);
		assertNotNull("Filter should not be null", bean.getFilter());
		assertTrue("Filter should be instance of InternalOtpFilter", 
			bean.getFilter() instanceof InternalOtpFilter);
		assertEquals("Should have correct URL pattern", 1, bean.getUrlPatterns().size());
		assertTrue("Should match /otp pattern", bean.getUrlPatterns().contains("/otp"));
	}
	
	@Test
	public void testGetDefaultInternalFilter() {
		FilterRegistrationBean<DefaultInternalFilter> bean = 
			internalAuthFilterConfig.getDefaultInternalFilter();
		
		assertNotNull("Filter registration bean should not be null", bean);
		assertNotNull("Filter should not be null", bean.getFilter());
		assertTrue("Filter should be instance of DefaultInternalFilter", 
			bean.getFilter() instanceof DefaultInternalFilter);
		assertTrue("Should have URL pattern for auth transactions", 
			bean.getUrlPatterns().stream().anyMatch(pattern -> pattern.contains("authTransactions")));
		assertNotNull("Should have init parameters", bean.getInitParameters());
	}
	
	@Test
	public void testGetDefaultAuthtypeFilter() {
		FilterRegistrationBean<DefaultAuthTypeFilter> bean = 
			internalAuthFilterConfig.getDefaultAuthtypeFilter();
		
		assertNotNull("Filter registration bean should not be null", bean);
		assertNotNull("Filter should not be null", bean.getFilter());
		assertTrue("Filter should be instance of DefaultAuthTypeFilter", 
			bean.getFilter() instanceof DefaultAuthTypeFilter);
		assertTrue("Should have URL pattern /authtypes/status/* with wildcard",
                			bean.getUrlPatterns().stream().anyMatch(pattern -> pattern.equals("/authtypes/status/*")));
        assertNotNull("Should have init parameters", bean.getInitParameters());
        assertTrue("Should have IDType init parameter", bean.getInitParameters().containsKey("IDType"));
        assertTrue("Should have ID init parameter", bean.getInitParameters().containsKey("ID"));
	}
	
	@Test
	public void testGetDefaultAuthtypeStatusFilter() {
		FilterRegistrationBean<DefaultAuthTypeFilter> bean = 
			internalAuthFilterConfig.getDefaultAuthtypeStatusFilter();
		
		assertNotNull("Filter registration bean should not be null", bean);
		assertNotNull("Filter should not be null", bean.getFilter());
		assertTrue("Filter should be instance of DefaultAuthTypeFilter", 
			bean.getFilter() instanceof DefaultAuthTypeFilter);
		assertTrue("Should have exact URL pattern /authtypes/status without wildcard",
                			bean.getUrlPatterns().stream().anyMatch(pattern -> pattern.equals("/authtypes/status")));
        assertTrue("Should have no init parameters",
                			bean.getInitParameters() == null || bean.getInitParameters().isEmpty());
	}
}

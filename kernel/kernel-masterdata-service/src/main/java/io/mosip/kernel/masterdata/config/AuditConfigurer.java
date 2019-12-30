package io.mosip.kernel.masterdata.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * The Class AuditConfigurer.
 *
 * @author Srinivasan
 */
@Configuration
//@EnableWebMvc
public class AuditConfigurer implements WebMvcConfigurer {

	/**
	 * Auditable end points.
	 *
	 * @return the string[]
	 */
	private String[] auditableEndPoints() {
		return new String[] { "http://localhost:8086/v1/masterdata/devices/search" };

	}
	
	@Autowired
	private AuditInterceptor auditInterceptor;

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
         System.out.println("add Intercpetors called");
		registry.addInterceptor(auditInterceptor).addPathPatterns("/**/search");
	}
}

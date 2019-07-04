package io.mosip.kernel.auditmanager.config;

import java.util.concurrent.Executor;

import javax.servlet.Filter;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.mosip.kernel.auditmanager.exception.AuditAsyncExceptionHandler;

/**
 * Config class for Audit manager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.scheduling.annotation.AsyncConfigurer#getAsyncExecutor()
	 */
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.initialize();
		return executor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.annotation.AsyncConfigurer#
	 * getAsyncUncaughtExceptionHandler()
	 */
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AuditAsyncExceptionHandler();
	}

	/**
	 * @return the registered filter to the context.
	 */
	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(getReqResFilter());
		corsBean.setOrder(1);
		return corsBean;
	}

	/**
	 * @return a new {@link ReqResFilter} object.
	 */
	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}

}

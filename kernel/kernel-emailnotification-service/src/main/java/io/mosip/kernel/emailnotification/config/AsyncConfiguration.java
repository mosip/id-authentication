package io.mosip.kernel.emailnotification.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import io.mosip.kernel.emailnotification.exception.EmailNotificationAsyncHandler;


/**
 * Configuration class for using @Async, which allows asynchronous e-mail notification.
 * 
 * @author Sagar Mahapatra
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {
	/**
	 * Autowired reference for {@link EmailNotificationAsyncHandler}
	 */
	@Autowired
	EmailNotificationAsyncHandler mailNotifierAsyncHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.annotation.AsyncConfigurer#
	 * getAsyncUncaughtExceptionHandler()
	 */
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return mailNotifierAsyncHandler;
	}
}
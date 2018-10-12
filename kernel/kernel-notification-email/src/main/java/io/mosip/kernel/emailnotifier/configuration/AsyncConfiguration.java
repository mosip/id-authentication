package io.mosip.kernel.emailnotifier.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import io.mosip.kernel.emailnotifier.exceptionhandler.MailNotifierAsyncHandler;


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
	 * Autowired reference for {@link MailNotifierAsyncHandler}
	 */
	@Autowired
	MailNotifierAsyncHandler mailNotifierAsyncHandler;

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
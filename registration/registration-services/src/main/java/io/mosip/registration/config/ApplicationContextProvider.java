package io.mosip.registration.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Refresh the application context
 * 
 * @author Omsai Eswar M.
 *
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	/**
	 * setApplicationContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * refreshContext
	 */
	public void refreshContext() {
		((AnnotationConfigApplicationContext) applicationContext).refresh();
	}

}

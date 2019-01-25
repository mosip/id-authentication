package io.mosip.registration.builder;

import java.util.function.Consumer;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;

/**
 * Generic Builder Pattern
 * 
 * @author M1045980
 *
 * @param <T>
 */
public class Builder<T> {
	
	private static final Logger LOGGER = AppConfig.getLogger(Builder.class);
	private T instance;

	/**
	 * Constructor Initialization
	 * 
	 * @param clazz
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Builder(Class<T> clazz) {
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException exception) {
			LOGGER.error("REGISTRATION-INDIVIDUAL_REGISTRATION-BUILDER", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
		}
	}

	/**
	 * Set the parameter with desired value
	 * 
	 * @param setter
	 * @return the instance of the {@link Builder}
	 */
	public Builder<T> with(Consumer<T> setter) {
		setter.accept(instance);
		return this;
	}

	/**
	 * Get the initialized instance
	 * 
	 * @return the instance of the specified class
	 */
	public T get() {
		return instance;
	}

	/**
	 * Build the instance
	 * 
	 * @param clazz
	 * @return the instance of the {@link Builder}
	 */
	public static <T> Builder<T> build(Class<T> clazz) {
		return new Builder<>(clazz);
	}
}

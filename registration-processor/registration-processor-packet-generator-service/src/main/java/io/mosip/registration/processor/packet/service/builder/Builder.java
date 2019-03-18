package io.mosip.registration.processor.packet.service.builder;

import java.util.function.Consumer;

/**
 * Generic Builder Pattern
 * 
 * @author Sowmya
 *
 * @param <T>
 */
public class Builder<T> {

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

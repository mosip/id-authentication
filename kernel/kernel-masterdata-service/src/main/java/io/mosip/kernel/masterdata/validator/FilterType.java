package io.mosip.kernel.masterdata.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare filter types
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterType {
	/**
	 * field to hold the declared filter types
	 * 
	 * @return filter types
	 */
	public FilterTypeEnum[] types();
}

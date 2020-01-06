package io.mosip.kernel.masterdata.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that validates the Filter Column value.
 * 
 * @author Sagar Mahapatra
 * @since 1.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterColumn {
	public FilterColumnEnum[] columns();

}

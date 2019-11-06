package io.mosip.kernel.masterdata.dto.request.registereddevice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that validates the StatucCode Column value.
 * 
 * @author Megha Tanga 
 * @since 1.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatusCodeColumn {
	public StatusCodeValue[] columns();

}

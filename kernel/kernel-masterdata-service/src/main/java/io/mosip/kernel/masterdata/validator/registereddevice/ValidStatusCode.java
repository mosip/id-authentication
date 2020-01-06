package io.mosip.kernel.masterdata.validator.registereddevice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation that validates the StatucCode Column value.
 * 
 * @author Megha Tanga
 * @since 1.0
 *
 */
@Documented
@Constraint(validatedBy = StatusCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatusCode {
	String message() default "Status code not supported";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

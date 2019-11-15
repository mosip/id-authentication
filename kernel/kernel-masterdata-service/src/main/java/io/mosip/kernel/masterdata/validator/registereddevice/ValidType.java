package io.mosip.kernel.masterdata.validator.registereddevice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation that validates the Type Column value.
 * 
 * @author Megha Tanga
 * @since 1.0
 *
 */
@Documented
@Constraint(validatedBy = TypeValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidType {
	String message() default "Type not supported";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	/*String baseField(); 
	
	String matchField();*/

}

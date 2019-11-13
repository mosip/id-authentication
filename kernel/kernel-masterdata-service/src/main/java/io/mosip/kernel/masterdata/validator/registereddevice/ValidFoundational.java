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
@Constraint(validatedBy = FoundationalValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFoundational {
	String message() default "If certification level received is L1 then FoundationalTPId OR FoundationalTrustSignature OR FoundationalTrustCertificate should not be null or empty";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String baseField();

	String[] matchField();

}

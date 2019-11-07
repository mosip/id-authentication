package io.mosip.kernel.masterdata.validator.registereddevice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation that validates the Certificate Level value.
 * 
 * @author Megha Tanga 
 * @since 1.0
 *
 */
@Documented
@Constraint(validatedBy = CertificateLevelValidator.class)
@Target(ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCertificateLevel {	
	String message() default "Certificate Level not supported";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}


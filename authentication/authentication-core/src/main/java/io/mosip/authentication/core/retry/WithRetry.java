package io.mosip.authentication.core.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The WithRetry annotation - to be used on a method to apply retry mechanism
 * based on the configured retry/backoff policy.
 * 
 * @author Loganathan Sekar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WithRetry {

}

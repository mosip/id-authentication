package io.mosip.authentication.core.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.util.RetryUtil;

@Aspect
@Component
public class RetryAspect {

	@Autowired
	private RetryUtil retryUtil;

	@Pointcut("@annotation(WithRetry)")
	public void withRetryMethods() {
	}

	@Around("withRetryMethods()")
	public Object processMethodsWithRetry(final ProceedingJoinPoint pjp) throws Throwable {
		return retryUtil.<Object, Throwable>doWithRetry(() -> pjp.proceed());
	}
}

package io.mosip.kernel.synchandler.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class MethodTimeLogger {

	@Around("methodJoinPoint()")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		long start = System.currentTimeMillis();
		System.out.println("Going to call::" + method + " method.");
		Object output = joinPoint.proceed();
		System.out.println("::" + method + " method. execution completed.");
		long elapsedTime = System.currentTimeMillis() - start;
		System.out.println("::" + method + " Method execution time: " + elapsedTime + " milliseconds.");
		return output;
	}

	@Pointcut("execution(* io.mosip.kernel.synchandler.service.*.*(..))")
	private void methodJoinPoint() {
	}
}

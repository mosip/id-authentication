package io.mosip.authentication.core.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_RETRY_EXPONENTIAL_BACKOFF_INITIAL_INTERVAL_MILLISECS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_RETRY_EXPONENTIAL_BACKOFF_MAX_INTERVAL_MILLISECS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_RETRY_EXPONENTIAL_BACKOFF_MULTIPLIER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_RETRY_SIMPLE_LIMIT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {
	
	@Bean
	public RetryPolicy retryPolicy(@Value("${" + IDA_RETRY_SIMPLE_LIMIT + ":3}") int retryLimit) {
		// Adding 1 as the limit is inclusive of the first attempt for this policy
		SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(retryLimit + 1);
		return simpleRetryPolicy;
	}
	
	@Bean
	public BackOffPolicy backOffPolicy(
			@Value("${" + IDA_RETRY_EXPONENTIAL_BACKOFF_INITIAL_INTERVAL_MILLISECS + "}:100") long initialIntervalMilliSecs,
			@Value("${" + IDA_RETRY_EXPONENTIAL_BACKOFF_MULTIPLIER + "}:2.0") double multiplier,
			@Value("${" + IDA_RETRY_EXPONENTIAL_BACKOFF_MAX_INTERVAL_MILLISECS + "}:30000") long maxIntervalMilliSecs
			) {
		ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
		exponentialBackOffPolicy.setInitialInterval(initialIntervalMilliSecs);
		exponentialBackOffPolicy.setMultiplier(multiplier);
		exponentialBackOffPolicy.setMaxInterval(maxIntervalMilliSecs);
		return exponentialBackOffPolicy;
	}
	
	@Bean
	public RetryTemplate retryTemplate(RetryPolicy retryPolicy, BackOffPolicy backOffPolicy) {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		retryTemplate.setThrowLastExceptionOnExhausted(true);
		return retryTemplate;
	}

}

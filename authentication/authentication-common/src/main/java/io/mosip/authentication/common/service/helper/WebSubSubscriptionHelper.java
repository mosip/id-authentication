package io.mosip.authentication.common.service.helper;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;

@Component
public class WebSubSubscriptionHelper {

	public void initSubscriber(WebSubEventSubcriber subscriber) {
		initSubscriber(subscriber, null);
	}
	
	public void initSubscriber(WebSubEventSubcriber subscriber, Supplier<Boolean> enableTester) {
		subscriber.initialize(enableTester);
	}
}

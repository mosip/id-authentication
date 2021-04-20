package io.mosip.authentication.common.service.websub;

import java.util.function.Supplier;

/**
 * The Interface WebSubEventTopicRegistrar.
 * @author Loganathan Sekar
 */
public interface WebSubEventTopicRegistrar {
	
	/**
	 * Initialize.
	 *
	 * @param enableTester the enable tester
	 */
	void register(Supplier<Boolean> enableTester);
	
}

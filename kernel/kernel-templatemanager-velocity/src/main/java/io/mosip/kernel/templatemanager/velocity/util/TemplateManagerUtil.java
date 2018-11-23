package io.mosip.kernel.templatemanager.velocity.util;

import java.util.Map;

import org.apache.velocity.VelocityContext;

/**
 * TemplateManagerUtil contain Utility methods
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 01-10-2018
 */
public class TemplateManagerUtil {

	private TemplateManagerUtil() {
	}

	/**
	 * Method to bind the map of values into VelocityContext
	 * 
	 * @param input
	 *            as Map&lt;String,Object&gt; where key will be placeholder name and
	 *            Object is the actual value for the placeholder
	 * @return VelocityContext
	 */
	public static VelocityContext bindInputToContext(Map<String, Object> input) {
		VelocityContext context = null;
		if (input != null && !input.isEmpty()) {
			context = new VelocityContext(input);
		}

		return context;
	}
}

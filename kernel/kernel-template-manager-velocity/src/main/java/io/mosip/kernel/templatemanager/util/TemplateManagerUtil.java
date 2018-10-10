package io.mosip.kernel.templatemanager.util;

import java.util.Map;

import org.apache.velocity.VelocityContext;
/**
 * Mosip Template Manager Util wrap the template values to VeclocityContext and return back
 * 
 * @author Abhishek Kumar
 * @version 1.0
 * @since   2018-10-01 
 */
public class TemplateManagerUtil {
	
	private TemplateManagerUtil() {
	}
	/**
	 * 
	 * @param values
	 * @return VelocityContext
	 */
	public static VelocityContext bindInputToContext(Map<String, Object> input) {
		VelocityContext context = null;
		if (input!=null && !input.isEmpty()) {
			context=new VelocityContext(input);
		}

		return context;
	}
}

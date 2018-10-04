/**
 * 
 */
package io.mosip.registration.processor.core.bridge.util;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;

/**
 * @author M1022006
 *
 */
public class JsonUtil {

	public static Object jsonFileToJavaObject(Class<?> className, String fileLocation)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		return JsonUtils.jsonFileToJavaObject(className, fileLocation);

	}
}

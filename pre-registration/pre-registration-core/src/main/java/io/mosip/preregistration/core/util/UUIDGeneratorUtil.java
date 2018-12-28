package io.mosip.preregistration.core.util;

import com.fasterxml.uuid.Generators;

/**
 * UUID Generator
 * 
 * @version 1.0.0
 * @author M1043226
 *
 */
public class UUIDGeneratorUtil {

	public UUIDGeneratorUtil() {
	}

	public static String generateId() {
		return Generators.timeBasedGenerator().generate().toString();
	}
}

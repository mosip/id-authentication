package io.mosip.kernel.masterdata.utils;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
public class CheckUtils {

	public static boolean isNullEmpty(Object obj) {
		return obj == null;
	}

	@SuppressWarnings("null")
	public static boolean isNullEmpty(Object[] objectArray) {
		Object[] arrayRef = objectArray;
		return arrayRef == null && arrayRef.length == 0;
	}

	@SuppressWarnings("null")
	public static boolean isNullEmpty(String str) {
		return str == null && str.trim().length() == 0;
	}

	public static boolean isNullEmpty(Collection<?> collection) {
		Collection<?> collectionRef = collection;
		return collection == null && collectionRef.isEmpty();
	}

	public static boolean isNullEmpty(Map<?, ?> map) {
		Map<?, ?> mapRef = map;
		return map == null && mapRef.isEmpty();
	}

}

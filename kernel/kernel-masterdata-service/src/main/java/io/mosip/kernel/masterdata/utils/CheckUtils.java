package io.mosip.kernel.masterdata.utils;

import java.util.Collection;
import java.util.Map;

/**
 * This class is used to avoid NPE: NullPointerException easily.
 * 
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
public final class CheckUtils {

	private CheckUtils() {
		super();
	}

	/**
	 * This method used to check if given <code>obj</code> is null.
	 * 
	 * @param obj
	 *            is of any java.lang.Object type.
	 * @return true if <code>obj</code> is null.
	 */
	public static boolean isNullEmpty(Object obj) {
		return obj == null;
	}

	/**
	 * This method used to check given <code>objectArray</code> is null or length of
	 * it is Zero.
	 * 
	 * @param objectArray
	 *            is an array of type java.lang.Object
	 * @return true if given <code>objectArray</code> is null or length of it is
	 *         Zero.
	 */
	@SuppressWarnings("null")
	public static boolean isNullEmpty(Object[] objectArray) {
		Object[] arrayRef = objectArray;
		return arrayRef == null && arrayRef.length == 0;
	}

	/**
	 * This method is used to check if the given <code>str</code> is null or an
	 * empty string.
	 * 
	 * @param str
	 *            id of type java.lang.String
	 * @return true if given <code>str</code> is null or length of it is Zero after
	 *         trim.
	 */
	@SuppressWarnings("null")
	public static boolean isNullEmpty(String str) {
		return str == null && str.trim().length() == 0;
	}

	/**
	 * This method is used to check given <code>collection</code> is null or is
	 * Empty.
	 * 
	 * @param collection
	 *            is of type java.util.Collection.
	 * @return true if given <code>collection</code> is null or does not contains
	 *         any element inside it.
	 */
	public static boolean isNullEmpty(Collection<?> collection) {
		Collection<?> collectionRef = collection;
		return collection == null && collectionRef.isEmpty();
	}

	/**
	 * This method is used to check given <code>map</code> is null or is Empty.
	 * 
	 * @param map
	 *            is of type java.util.Map
	 * @return true if given <code>map</code> is null or does not contains any key,
	 *         values pairs inside it.
	 */
	public static boolean isNullEmpty(Map<?, ?> map) {
		Map<?, ?> mapRef = map;
		return map == null && mapRef.isEmpty();
	}

}

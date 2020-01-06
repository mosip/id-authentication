package io.mosip.registration.dao;

import io.mosip.registration.entity.GlobalParam;

/**
 * This class is used to fetch only specified columns from {@link GlobalParam} table
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamName {

	/**
	 * This method is used to fetch only name column from {@link GlobalParam} table
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * This method is used to fetch only value column from {@link GlobalParam} table
	 * 
	 * @return value
	 */
	String getVal();
}

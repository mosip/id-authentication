package io.mosip.registration.dao;

/**
 * Interface for GlobalParam to fetch only specified columns from table
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamName {

	/**
	 * To fetch only name column from table
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * To fetch only value column from table
	 * 
	 * @return value
	 */
	String getVal();
}

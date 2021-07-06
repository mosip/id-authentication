package io.mosip.authentication.common.service.websub.dto;

import java.util.Map;

/**
 * The Interface EventInterface.
 */
public interface EventInterface {
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	void setId(String id);
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	Map<String, Object> getData();
	
	/**
	 * Sets the data.
	 *
	 * @param data the data
	 */
	void setData(Map<String, Object> data);

}

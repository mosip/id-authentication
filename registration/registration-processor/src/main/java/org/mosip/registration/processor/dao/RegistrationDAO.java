/**
 * 
 */
package org.mosip.registration.processor.dao;

import java.util.List;

/**
 * @author M1045980
 *
 */
public interface RegistrationDAO {
	
	public void save(String zipFileName);
	
	public int update(String zipFileName);
	
	public boolean upload(Object object);
	
	public List<String> view(String zipFileName);
}

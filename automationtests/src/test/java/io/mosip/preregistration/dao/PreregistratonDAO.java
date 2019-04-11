package io.mosip.preregistration.dao;

import java.util.List;



import io.mosip.dbaccess.prereg_dbread;


public class PreregistratonDAO 
{
	public List<?> preregFetchPreregDetails()
	{
		//String hql = "SELECT preRegistrationId,statusCode FROM DemographicEntity E WHERE E.id = '20137492065386'";
		String hql = "SELECT preRegistrationId,statusCode FROM DemographicEntity E WHERE E.statusCode = 'Pending_Appointment'";
		//String hql = "SELECT preRegistrationId,statusCode FROM DemographicRequestDTO d WHERE d.preRegistrationId = '20137492065386'";
		
		List<?> result = prereg_dbread.validateDB(hql);
		
		return result;
		
	}
	
	
	

	
}

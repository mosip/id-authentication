package io.mosip.preregistration.dao;

import java.util.List;
import java.util.logging.Logger;

import io.mosip.dbaccess.prereg_dbread;


public class PreregistratonDAO 
{
	public List<? extends Object> preregFetchPreregDetails(String preRegId)
	{
		String hql = "SELECT preRegistrationId,statusCode FROM DemographicEntity E WHERE E.preRegistrationId = '"+preRegId+"'";
		
		List<? extends Object> result = prereg_dbread.validateDB(hql);
		
		return result;
		
	}
	
	public int updateStatusCode(String statusCode,String preRegId)
	{
		String hql="UPDATE DemographicEntity SET statusCode ='"+statusCode+"' WHERE preRegistrationId = '"+preRegId+"'";
		System.out.println("My info::"+hql);
		int result = prereg_dbread.validateDBUpdate(hql);
		return result;
	}
	
	

	
}

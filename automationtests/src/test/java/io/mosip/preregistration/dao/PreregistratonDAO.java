package io.mosip.preregistration.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import io.mosip.dbaccess.prereg_dbread;
import io.mosip.preregistration.entity.RegistrationBookingEntity;


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
	public void setDate(String preRegId)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, -1); 
		String date = sdf.format(c.getTime());
		String hql="update prereg.reg_appointment set appointment_date='"+date+"' where prereg_id='"+preRegId+"'";
		prereg_dbread.dbConnectionUpdate(hql, RegistrationBookingEntity.class, "auditdev.cfg.xml", "preregqa.cfg.xml");
	}
	
	

	
}

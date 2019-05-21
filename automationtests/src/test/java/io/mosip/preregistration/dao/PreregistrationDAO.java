package io.mosip.preregistration.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import io.mosip.dbaccess.prereg_dbread;
import io.mosip.dbdto.Audit;
import io.mosip.preregistration.entity.RegistrationBookingEntity;
import io.mosip.preregistration.util.PreRegistartionDataBaseAccess;


public class PreregistrationDAO 
{
	public PreRegistartionDataBaseAccess dbAccess=new PreRegistartionDataBaseAccess();
	public List<? extends Object> preregFetchPreregDetails(String preRegId)
	{
		String hql = "SELECT preRegistrationId,statusCode FROM DemographicEntity E WHERE E.preRegistrationId = '"+preRegId+"'";
		
		List<? extends Object> result = prereg_dbread.validateDB(hql);
		//List<? extends Object> result = dbAccess.updateDbData(hql, "prereg");
		return result;
		
	}
	
	public int updateStatusCode(String statusCode,String preRegId)
	{
		String hql="UPDATE DemographicEntity SET statusCode ='"+statusCode+"' WHERE preRegistrationId = '"+preRegId+"'";
		
		int result = prereg_dbread.validateDBUpdate(hql);
		return result;
	}
	/*public void setDate(String preRegId)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, -1); 
		String date = sdf.format(c.getTime());
		String hql="update prereg.reg_appointment set appointment_date='"+date+"' where prereg_id='"+preRegId+"'";
		prereg_dbread.dbConnectionUpdate(hql, RegistrationBookingEntity.class, "preregdev.cfg.xml", "preregqa.cfg.xml");
	}*/
	public void setDate(String preRegId)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, -1); 
		String date = sdf.format(c.getTime());
		String queryString="update prereg.reg_appointment set appointment_date='"+date+"' where prereg_id='"+preRegId+"'";
		dbAccess.updateDbData(queryString, "prereg");
	}
	/*public List<Object> getAuditData(String userId)
	{
		String query = "SELECT  log_desc, event_id, event_type, event_name, session_user_id,module_name,ref_id,ref_id_type FROM audit.app_audit_log Where session_user_id='"+userId+"'";
		List<Object> auditData = prereg_dbread.dbConnection(query, Audit.class, "auditdev.cfg.xml", "auditqa.cfg.xml");
		return auditData;
	}*/
	public List<String> getAuditData(String userId)
	{
		String queryString = "SELECT  log_desc, event_id, event_type, event_name, session_user_id,module_name,ref_id,ref_id_type FROM audit.app_audit_log Where session_user_id='"+userId+"'";
		List<String> auditData = dbAccess.getDbData(queryString, "audit");
		return auditData;
	}
	public List<String> getOTP(String userId)
	{
		String queryString = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<String> otp = dbAccess.getDbData(queryString, "kernel");
		return otp;
	}
	
	

	
}

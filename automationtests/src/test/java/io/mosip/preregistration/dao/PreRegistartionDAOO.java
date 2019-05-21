package io.mosip.preregistration.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.mosip.dbaccess.prereg_dbread;
import io.mosip.preregistration.entity.RegistrationBookingEntity;
import io.mosip.preregistration.util.PreRegistartionDataBaseAccess;

public class PreRegistartionDAOO {
	public PreRegistartionDataBaseAccess dbAccess=new PreRegistartionDataBaseAccess();
	public List<String> getOTP(String userId)
	{
		String queryString = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<String> otp = dbAccess.getDbData(queryString, "kernel");
		return otp;
	}
	public List<String> getAuditData(String userId)
	{
		String queryString = "SELECT  log_desc, event_id, event_type, event_name, session_user_id,module_name,ref_id,ref_id_type FROM audit.app_audit_log Where session_user_id='"+userId+"'";
		List<String> auditData = dbAccess.getDbData(queryString, "audit");
		return auditData;
	}
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

}

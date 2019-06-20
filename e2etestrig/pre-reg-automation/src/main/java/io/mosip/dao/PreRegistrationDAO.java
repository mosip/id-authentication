package io.mosip.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

@SuppressWarnings("unused")
public class PreRegistrationDAO {
	static Session session;
	protected static Logger logger = Logger.getLogger(PreRegistrationDAO.class);
	public static SessionFactory factory;

	public List<String> getOTP(String userId) {
		String queryString = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<String> otp = getDbData(queryString, "kernel");
		return otp;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDbData(String queryString, String dbName) {
		return (List<String>) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();

	}

	public Session getDataBaseConnection(String dbName) {

		String dbConfigXml = dbName + "qa" + ".cfg.xml";
		try {
			factory = new Configuration().configure(dbConfigXml).buildSessionFactory();
		} catch (HibernateException e) {
			logger.info("Exception in Database Connection with following message: ");
			logger.info(e.getMessage());
		}
		session = factory.getCurrentSession();
		session.beginTransaction();
		return session;
	}
	public void setDate(String preRegId)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, -1); 
		String date = sdf.format(c.getTime());
		String queryString="update prereg.reg_appointment set appointment_date='"+date+"' where prereg_id='"+preRegId+"'";
		updateDbData(queryString, "prereg");
	}
	@SuppressWarnings("unchecked")
	public void updateDbData(String queryString, String dbName) {
		Query query = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString);
		int res = query.executeUpdate();
		session.getTransaction().commit();	
	}


}

package io.mosip.preregistration.util;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterClass;

public class PreRegistartionDataBaseAccess {
	public SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(PreRegistartionDataBaseAccess.class);

	public String env = System.getProperty("env.user");

	public Session getDataBaseConnection(String dbName) {

		String dbConfigXml = dbName+env+".cfg.xml";
		try {
		factory = new Configuration().configure(dbConfigXml).buildSessionFactory();
		} catch (HibernateException e) {
			logger.info(e.getMessage());
		}
		session = factory.getCurrentSession();
		session.beginTransaction();
		return session;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDbData(String queryString, String dbName) {
		return (List<String>) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();

	}
	@SuppressWarnings("unchecked")
	public void updateDbData(String queryString, String dbName) {
		Query query = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString);
		int res = query.executeUpdate();
		session.getTransaction().commit();	
	}
	@SuppressWarnings("unchecked")
	public List<String> getConsumedStatus(String queryString, String dbName) {
		return (List<String>) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();
	}
	
	@AfterClass(alwaysRun = true)
	public void closingSession() {
		if (session != null)
			session.getTransaction().commit();
		session.close();
		factory.close();
	}

	
	
}

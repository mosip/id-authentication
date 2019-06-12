
package io.mosip.kernel.util;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

/**
 * 
 * @author Arunakumar.Rati
 * @author Ravi Kant
 *
 */

public class KernelDataBaseAccess {

	public SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(KernelDataBaseAccess.class);

	public String env = System.getProperty("env.user");

	public Session getDataBaseConnection(String dbName) {

		String dbConfigXml = dbName+env.toLowerCase()+".cfg.xml";
		try {
		factory = new Configuration().configure(dbConfigXml).buildSessionFactory();
		session = factory.getCurrentSession();
		} 
		catch (HibernateException e) {
			logger.info("Exception in Database Connection with following message: ");
			logger.info(e.getMessage());
			Assert.assertTrue(false, "Exception in creating the sessionFactory");
		}
		
		catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception in getting the session");
		}
		session.beginTransaction();
		logger.info("==========session  begins=============");
		return session;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDbData(String queryString, String dbName) {

		return  getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();

	}
	@SuppressWarnings("unchecked")
	public List<Object> getData(String queryString, String dbName) {

		return  getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();

	}
	public long validateDBCount(String queryStr, String dbName) {
		long count = 0;
		count = ((BigInteger) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryStr).getSingleResult()).longValue();
		logger.info("obtained objects count from DB is : " + count);
		return count;
	}

	public boolean validateDataInDb(String queryString, String dbName) {
		int size = getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list().size();
		logger.info("Size is : " + size);
		return (size == 1) ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getArrayData(String queryString, String dbName) {
		return (List<Object[]>) getDataBaseConnection(dbName.toLowerCase()).createSQLQuery(queryString).list();
	}

	@AfterClass(alwaysRun = true)
	public void closingSession() {
		if (session != null)
			session.getTransaction().commit();
		session.close();
		factory.close();
	}

}
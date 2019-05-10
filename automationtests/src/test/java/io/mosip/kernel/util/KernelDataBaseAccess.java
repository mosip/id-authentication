package io.mosip.kernel.util;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
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

	public Session getDataBaseConnection() {
		String kernelDb = null;
		String masterDb = null;
		switch (env) {
		case "dev":
			kernelDb = "kerneldev.cfg.xml";
			masterDb = "masterdatadev.cfg.xml";
			break;

		case "qa":
			kernelDb = "kernelqa.cfg.xml";
			masterDb = "masterdataqa.cfg.xml";
			break;

		}

		try {
			factory = new Configuration().configure(kernelDb).configure(masterDb).buildSessionFactory();

		} catch (HibernateException e) {
			logger.info("Exception in Database Connection with following message: ");
			logger.info(e.getMessage());
		}
		session = factory.getCurrentSession();
		session.beginTransaction();
		logger.info("==========session  begins=============");
		return session;
	}

	@SuppressWarnings("unchecked")
	public List<String> getDbData(String queryString) {

		return (List<String>) getDataBaseConnection().createSQLQuery(queryString).list();

	}

	public long validateDBCount(String queryStr) {
		long count = 0;
		count = ((BigInteger) getDataBaseConnection().createSQLQuery(queryStr).getSingleResult()).longValue();
		logger.info("obtained objects count from DB is : " + count);
		return count;
	}

	public boolean validateDataInDb(String queryString) {
		int size = getDataBaseConnection().createSQLQuery(queryString).list().size();
		logger.info("Size is : " + size);
		return (size == 1) ? true : false;
	}

	@SuppressWarnings("unchecked")
	public List<String[]> getArrayData(String queryString) {
		return (List<String[]>) getDataBaseConnection().createSQLQuery(queryString).list();
	}

	@AfterClass(alwaysRun = true)
	public void closingSession() {
		if (session != null)
			session.getTransaction().commit();
		session.close();
		factory.close();
	}
}
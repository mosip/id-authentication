package io.mosip.dbaccess;

import java.util.List;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;

public class PreRegDbread {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(PreRegDbread.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();

	@SuppressWarnings("deprecation")
	public static boolean prereg_dbconnectivityCheck() {
		boolean flag = false;
		try {
			if(BaseTestCase.environment.equalsIgnoreCase("dev"))				
				factory = new Configuration().configure("preregdev.cfg.xml").addAnnotatedClass(OtpEntity.class)
						.buildSessionFactory();
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))				
						factory = new Configuration().configure("preregqa.cfg.xml").addAnnotatedClass(OtpEntity.class)
								.buildSessionFactory();
				}
			session = factory.getCurrentSession();
			session.beginTransaction();
			logger.info("Session value is :" + session);

			flag = session != null;
			// Assert.assertTrue(flag);
			logger.info("Flag is : " + flag);
			if (flag) {
				session.close();
				factory.close();
				return flag;
			}

			else
				return flag;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Connection exception Received");
			return flag;
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean prereg_dbDataPersistenceCheck(String preId) {
		boolean flag = false;


		if(BaseTestCase.environment.equalsIgnoreCase("dev"))
			factory = new Configuration().configure("preregdev.cfg.xml")
		.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))
						factory = new Configuration().configure("preregqa.cfg.xml")
					.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				}

	
		session = factory.getCurrentSession();
		session.beginTransaction();

		flag = validatePreIdinDB(session, preId);
		// Assert.assertTrue(flag);
		logger.info("Flag is : " + flag);
		if (flag) {
			// session.close();
			return flag;
		}

		/*
		 * else return flag;
		 */

		return flag;

	}

	private static boolean validatePreIdinDB(Session session, String preId) {
		int size;
		String status_code = null;

		String queryString = " Select prereg_id, status_code"
				+ " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";

		/*
		 * String queryString=
		 * "  SELECT * FROM prereg.applicant_demographic where prereg_id='74157648721735' "
		 * ;
		 * 
		 */

		Query query = session.createSQLQuery(queryString);
		query.setParameter("preId_value", preId);
		@SuppressWarnings("unchecked")

		List<Object> objs = (List<Object>) query.list();
		// logger.info("First Element of List Elements are : " +objs.get(1));
		size = objs.size();
		logger.info("Size is : " + size);

		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			status_code = (String) (TestData[9]);

			logger.info("Status is : " + status_code);

			// commit the transaction
			session.getTransaction().commit();

			// Query q=session.createQuery(" from otp_transaction where ID='917248' ");

		}

		try {

			if (size == 1) {
				Assert.assertEquals(status_code, "Pending_Appointment");
				return true;
			} else
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public static boolean prereg_db_CleanUp(List<String> preIds) {

		boolean flag = false;
		// String preId;


		if(BaseTestCase.environment.equalsIgnoreCase("dev"))				
			factory = new Configuration().configure("preregdev.cfg.xml").addAnnotatedClass(OtpEntity.class)
					.buildSessionFactory();
			else
			{
				if(BaseTestCase.environment.equalsIgnoreCase("qa"))				
					factory = new Configuration().configure("preregqa.cfg.xml").addAnnotatedClass(OtpEntity.class)
							.buildSessionFactory();
			}


		for (String preId : preIds)

		{
			session = factory.getCurrentSession();
			session.beginTransaction();

			/*
			 * Query to Delete PreId data in applicant_demographic table
			 */
			String queryString = "Delete from prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";
			// String queryString= "Delete from prereg.applicant_demographic where
			// prereg.applicant_demographic.prereg_id= '97846295631728' ";
			int size = 0;

			List<Object> objs = null;
			/*
			 * Query query = session.executeUpdate(queryString); query =
			 * session.createSQLQuery(queryString);
			 */

			Query query = session.createSQLQuery(queryString);
			 logger.info("Query after replacing PreId =================== :" + query);
			query.setParameter("preId_value", preId);

			int res = query.executeUpdate();
			session.getTransaction().commit();

			 logger.info("Result size is ============: " + res);
			if (res == 1) {
				logger.info("Data Deleted Successfully ======");
				flag = true;
			} else {
				logger.info("Data NOT Deleted Successfully ======");
				flag = false;
			}

		}
		session.close();
		factory.close();
		return flag;

	}

	public static boolean prereg_db_Update(String preId) {

		boolean flag = false;
		// String preId;


		if(BaseTestCase.environment.equalsIgnoreCase("dev"))				
			factory = new Configuration().configure("preregdev.cfg.xml").addAnnotatedClass(OtpEntity.class)
					.buildSessionFactory();
			else
			{
				if(BaseTestCase.environment.equalsIgnoreCase("qa"))				
					factory = new Configuration().configure("preregqa.cfg.xml").addAnnotatedClass(OtpEntity.class)
							.buildSessionFactory();
			}

		session = factory.getCurrentSession();
		session.beginTransaction();

		/*
		 * Query to Delete PreId data in applicant_demographic table
		 */
		String queryString = "Update prereg.applicant_demographic set encrypted_dtimes= encrypted_dtimes - interval '48' hour where prereg.applicant_demographic.prereg_id= :preId_value ";
		// String queryString= "Update prereg.applicant_demographic set
		// encrypted_dtimes= encrypted_dtimes - interval '48' hour WHERE
		// prereg_id='49758245369132' ";
		int size = 0;

		Query query = session.createSQLQuery(queryString);
		 logger.info("Query after replacing PreId =================== :" + query);
		query.setParameter("preId_value", preId);

		int res = query.executeUpdate();
		session.getTransaction().commit();

		 logger.info("Result size is ============: " + res);
		if (res == 1) {
			logger.info("Data Updated Successfully ======");

			flag = true;
		} else {
			logger.info("Data NOT Updated Successfully ======");
			flag = false;
		}

		session.close();
		factory.close();
		return flag;

	}

}
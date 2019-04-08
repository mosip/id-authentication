package io.mosip.dbaccess;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.Test;

import io.mosip.dbentity.Gender;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;

@Test
public class read_otpTransactiondb {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(PreRegDbread.class);
	
	@SuppressWarnings("deprecation")
	public static boolean readotpTransaction(String otp)
	{
		boolean flag=false;
		
		if(BaseTestCase.environment.equalsIgnoreCase("dev"))		
			factory = new Configuration().configure("kerneldev.cfg.xml")                      
		.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			else
			{
				if(BaseTestCase.environment.equalsIgnoreCase("qa"))		
					factory = new Configuration().configure("kernelqa.cfg.xml")                      
				.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			}
		session = factory.getCurrentSession();
		session.beginTransaction();
		flag=validateOTPinDB(session, otp);
		logger.info("flag is : " +flag);
		return flag;
		
		//session.close();
	}
	
	@SuppressWarnings("unchecked")
	private static boolean validateOTPinDB(Session session, String otp)
	{
		int size;
				
		String queryString=" Select kernel.otp_transaction.*"+
                        " From kernel.otp_transaction where kernel.otp_transaction.otp= :otp_value ";
		
		Query query = session.createSQLQuery(queryString); 
		query.setParameter("otp_value", otp);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			String status_code = (String) TestData[3];
			logger.info("Status is : " +status_code);
			
			// commit the transaction
					session.getTransaction().commit();
						
						factory.close();

		//Query q=session.createQuery(" from otp_transaction where ID='917248' ");
	}
		
		if(size==1)
			return true;
		else
			return false;
	
	}
	
	@SuppressWarnings("deprecation")
	public static boolean readGenderType(String code)
	{
		boolean flag=false;
		
		if(BaseTestCase.environment.equalsIgnoreCase("dev"))		
			factory = new Configuration().configure("masterdatadev.cfg.xml")                      
		.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			else
			{
				if(BaseTestCase.environment.equalsIgnoreCase("qa"))		
					factory = new Configuration().configure("masterdataqa.cfg.xml")                      
				.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			}
		

		session = factory.getCurrentSession();
		session.beginTransaction();
		flag=validateGenderCode(session, code);
		logger.info("flag is : " +flag);
		return flag;
		
		//session.close();
	}
	
	@SuppressWarnings("unchecked")
	private static boolean validateGenderCode(Session session, String code)
	{
		int size;
				
		String queryString="SELECT master.gender.* FROM master.gender where master.gender.code= :code_value";
				
		Query query = session.createSQLQuery(queryString);
		query.setParameter("code_value", code);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			String status_code = (String) TestData[code.length()];
			logger.info("Status is : " +status_code);
			
			// commit the transaction
					session.getTransaction().commit();
						
						factory.close();

		//Query q=session.createQuery(" from otp_transaction where ID='917248' ");
	}
		
		if(size==1)
			return true;
		else
			return false;
	
	}
	
}
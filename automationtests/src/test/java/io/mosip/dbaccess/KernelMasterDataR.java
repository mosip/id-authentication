package io.mosip.dbaccess;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.fasterxml.classmate.AnnotationConfiguration;

import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;



/**
 * @author Ravi Kant
 * @author Arunakumar.Rati
 *
 */
public class KernelMasterDataR {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(KernelMasterDataR.class);
	
	@SuppressWarnings("deprecation")
	public static boolean kernelMasterData_dbconnectivityCheck()
	{
		boolean flag=false;
		try {	
			/*
			 * Based on the environemnt configuration file is set
			 */
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
		
		logger.info("Session value is :" +session);
		
			flag=session != null;
		//	Assert.assertTrue(flag);
			logger.info("Flag is : " +flag);
			if(flag)
			{
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
	public static boolean validateDB(String queryStr, Class dtoClass)
	{
		boolean flag=false;

		if(BaseTestCase.environment.equalsIgnoreCase("dev"))
			factory = new Configuration().configure("masterdatadev.cfg.xml")
		.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))
						factory = new Configuration().configure("masterdatainteg.cfg.xml")
					.addAnnotatedClass(dtoClass).buildSessionFactory();	
				}
		session = factory.getCurrentSession();
		session.beginTransaction();
		flag=validateDBdata(session, queryStr);
		logger.info("flag is : " +flag);
		return flag;
		

	}
	
	@SuppressWarnings("unchecked")
	private static boolean validateDBdata(Session session, String queryStr)
	{
		int size;
				
		String queryString=queryStr;
		
		Query query = session.createSQLQuery(queryString);
	
		List<Object> objs = (List<Object>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
		
		// commit the transaction
		session.getTransaction().commit();
			
			factory.close();
		
		if(size==1)
			return true;
		else
			return false;
	
	}
		@SuppressWarnings("deprecation")
		public static boolean masterDataDBConnection(Class dtoClass,String query)
		{
			boolean flag=false;

			if(BaseTestCase.environment.equalsIgnoreCase("dev"))
				factory = new Configuration().configure("masterdatadev.cfg.xml")
			.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
					else
					{
						if(BaseTestCase.environment.equalsIgnoreCase("qa"))
							factory = new Configuration().configure("masterdatainteg.cfg.xml")
						.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
					}
			session = factory.getCurrentSession();
			session.beginTransaction();
			flag=validateMasterDatainDB(session, query);
			logger.info("flag is : " +flag);
			return flag;
			
			//session.close();
		}
		
		

		@SuppressWarnings("unchecked")
		private static boolean validateMasterDatainDB(Session session, String queryString)
		{
			int size;
			Query query = session.createSQLQuery(queryString); 
			//query.setParameter("otp_value", otp);
		
			List<Object> objs = (List<Object>) query.list();
			size=objs.size();
			logger.info("Size is : " +size);
			Object[] TestData = null;
			// reading data retrieved from query
			for (Object obj : objs) {
				TestData = (Object[]) obj;
				Object status_code =  TestData[3];
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
		public static boolean masterDataDBConnection1(Class dtoClass,String queryString,String columnName,String value)
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
			Transaction txn=session.beginTransaction();
			//txn.begin();
			flag=validateMasterDatainDB1(queryString,columnName, value);
			logger.info("flag is : " +flag);
					
			session.close();
			return flag;
			
//			Transaction tx=session.beginTransaction();  
//			Query q=session.createQuery(queryString);  
//			q.setParameter("m","POI");  
//			q.setParameter("n","CIN");  
//			  
//			int status=q.executeUpdate();  
//		 
//			tx.commit();  
//			return flag;
		}
		@SuppressWarnings({ "unused", "deprecation" })
		public
		static boolean validateMasterDatainDB1(String queryString,String columnName,String value)
		{
			
			Query query = session.createQuery(queryString); 
		   // query.setParameter(columnName, value);
		    int result = query.executeUpdate();
			logger.info("update completed");
			session.getTransaction().commit();
		    return true;
			
		}
		
		
		@SuppressWarnings({ "unused", "deprecation" })
		public
		static boolean validateMasterDatainDB1(String queryString)
		{
			Query query = session.createQuery(queryString); 
		   // query.setParameter(columnName, value);
		    int result = query.executeUpdate();
			logger.info("update completed");
			session.getTransaction().commit();
		    return true;
			
		} 
		

		@SuppressWarnings("deprecation")
		public static List<String> getDataFromDB(Class dtoClass,String query)
		{
			List<String> flag=null;

			if(BaseTestCase.environment.equalsIgnoreCase("dev"))
				factory = new Configuration().configure("kerneldev.cfg.xml")
			.addAnnotatedClass(dtoClass).buildSessionFactory();	
					else
					{
						if(BaseTestCase.environment.equalsIgnoreCase("qa"))
							factory = new Configuration().configure("kernelqa.cfg.xml")
						.addAnnotatedClass(dtoClass).buildSessionFactory();	
					}
			session = factory.getCurrentSession();
			session.beginTransaction();
			flag=getData(session, query);
			//logger.info("flag is : " +flag);
			return flag;
			
			//session.close();
		}
		
		

		@SuppressWarnings("unchecked")
		private static List<String> getData(Session session, String queryString)
		{
		  int size;
			Query query = session.createSQLQuery(queryString); 
			
		
			List<String> objs = (List<String>) query.list();
			size=objs.size();
			logger.info("Size is : " +size);
				// commit the transaction
						session.getTransaction().commit();
							
							factory.close();

		
			return objs;
				
		}

}
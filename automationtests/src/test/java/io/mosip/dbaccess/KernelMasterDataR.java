package io.mosip.dbaccess;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.mosip.dbentity.OtpEntity;
import io.mosip.dbentity.UinEntity;
import io.mosip.service.BaseTestCase;



/**
 * @author Ravi Kant
 * @author Arunakumar.Rati
 *
 */
public class KernelMasterDataR {
	public static SessionFactory factory;
	static Session session;
	public static Session session1;
	public static List<Object> objs = null;
	private static Logger logger = Logger.getLogger(KernelMasterDataR.class);
	
	public static String env=System.getProperty("env.user");
	
	
	@BeforeClass
	public static Session dbCheck()
	{
		switch(env) 
		{
		case "dev": 
			factory = new Configuration().configure("masterdatadev.cfg.xml")
					.addAnnotatedClass(UinEntity.class).buildSessionFactory();
		break;
		
		case "qa":
				factory = new Configuration().configure("masterdataqa.cfg.xml")
			.addAnnotatedClass(UinEntity.class).buildSessionFactory();
		
		break;
		}
		session1 = factory.getCurrentSession();
		session1.beginTransaction();
		logger.info("----------------session has began----------------");
		return session1;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getData(String queryString)
	{
	  int size;
		Query query = session1.createSQLQuery(queryString); 
		
	
		List<String> objs = (List<String>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
			// commit the transaction
		//session1.getTransaction().commit();
						
		return objs;
			
	}
	
	public List<String[]> getArrayData(String queryString)
	{
	  int size;
		Query query = session1.createSQLQuery(queryString); 
		
	
		List<String[]> objs = (List<String[]>) query.list();
		size=objs.size();
		logger.info("Size is : " +size);
			// commit the transaction
		//session1.getTransaction().commit();
						
		return objs;
			
	}
	@SuppressWarnings("unchecked")
	private static boolean validateMasterDatainDB(Session session, String queryString)
	{
		int size;
		Query query = session.createSQLQuery(queryString); 
		//query.setParameter("otp_value", otp);
	
		 objs = (List<Object>) query.list();
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
	public static List<String> getDataFromDB(Class dtoClass,String query)
	{
		List<String> flag=null;

		if(BaseTestCase.environment.equalsIgnoreCase("dev"))
			factory = new Configuration().configure("masterdatadev.cfg.xml")
		.addAnnotatedClass(dtoClass).buildSessionFactory();	
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))
						factory = new Configuration().configure("masterdataqa.cfg.xml")
					.addAnnotatedClass(dtoClass).buildSessionFactory();	
				}
		session = factory.getCurrentSession();
		session.beginTransaction();
		flag=getDbData(session, query);
		//logger.info("flag is : " +flag);
		return flag;
		
		
	}
	
	



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
		public static boolean masterDataDBConnection(Class dtoClass,String query)
		{
			boolean flag=false;


			if(BaseTestCase.environment.equalsIgnoreCase("dev"))
				factory = new Configuration().configure("masterdatadev.cfg.xml")
			.addAnnotatedClass(dtoClass).buildSessionFactory();	
					else
					{
						if(BaseTestCase.environment.equalsIgnoreCase("qa"))
							factory = new Configuration().configure("masterdataqa.cfg.xml")
						.addAnnotatedClass(dtoClass).buildSessionFactory();	

					}
			session = factory.getCurrentSession();
			session.beginTransaction();
			flag=validateMasterDatainDB(session, query);
			logger.info("flag is : " +flag);
			return flag;
			
		
		}
		
		

		
		
		/**
		 * @param queryStr containing query to obtain data count in table
		 * @return count obtained from db
		 */
		@SuppressWarnings("deprecation")
		public static long validateDBCount(String queryStr)
		{
			long flag=0;
			
			try {
				if(BaseTestCase.environment.equalsIgnoreCase("integration"))
					factory = new Configuration().configure("masterdatainteg.cfg.xml").buildSessionFactory();
				else 
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))
						factory = new Configuration().configure("masterdataqa.cfg.xml").buildSessionFactory();
				
				session = factory.getCurrentSession();
				session.beginTransaction();
			} catch (HibernateException e) {
				logger.info("Exception recived in DB Connection");
				e.printStackTrace();
				return 0;
			}
			
			
			flag=((BigInteger)session.createSQLQuery(queryStr).getSingleResult()).longValue();
			
			// commit the transaction
					session.getTransaction().commit();
					session.close();
					factory.close();
			logger.info("obtained objects count from DB is : " +flag);
			return flag;
			
	}
		public Session dbCheck1(String qaConfig,String devConig, Class entity)
		{
			switch(env) 
			{
			case "dev": 
				factory = new Configuration().configure(devConig)
						.addAnnotatedClass(entity).buildSessionFactory();
			break;
			
			case "qa":
					factory = new Configuration().configure(qaConfig)
				.addAnnotatedClass(entity).buildSessionFactory();
			
			break;
			}
			session1 = factory.getCurrentSession();
			session1.beginTransaction();
			logger.info("----------------session has began----------------");
			return session1;
		}
		
		public List<String> getData(Session session1,String queryString)
		{
		  int size;
			Query query = session1.createSQLQuery(queryString); 
			
		
			List<String> objs = (List<String>) query.list();
			size=objs.size();
			logger.info("Size is : " +size);
				// commit the transaction
			//session1.getTransaction().commit();
							
			return objs;
				
		}
		public static List<String> getDbData(Session session1,String queryString)
		{
		  int size;
			Query query = session1.createSQLQuery(queryString); 
			
		
			List<String> objs = (List<String>) query.list();
			size=objs.size();
			logger.info("Size is : " +size);
				// commit the transaction
			//session1.getTransaction().commit();
							
			return objs;
				
		}

@AfterClass
public void closingSession()
{
	session1.getTransaction().commit();
	factory.close();
	session1.close();
}

}
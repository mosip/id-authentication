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
@SuppressWarnings("deprecation")
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
			factory = new Configuration().configure("kerneldev.cfg.xml")
					.addAnnotatedClass(UinEntity.class).buildSessionFactory();
		break;
		
		case "qa":
				factory = new Configuration().configure("kernelqa.cfg.xml")
			.addAnnotatedClass(UinEntity.class).buildSessionFactory();
		
		break;

		case "int":
				factory = new Configuration().configure("kernelqa.cfg.xml")
			.addAnnotatedClass(UinEntity.class).buildSessionFactory();
		
		}
		session1 = factory.getCurrentSession();
		session1.beginTransaction();
		logger.info("----------------session has began----------------");
		return session1;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	private static boolean validateDatainDB(Session session, String queryString)
	{
		int size;
		 objs = session.createSQLQuery(queryString).list();
		size=objs.size();
		logger.info("Size is : " +size);
	
			// commit the transaction
					session.getTransaction().commit();
		
		if(size==1)
			return true;
		else
			return false;
	
	}
	
	

	@SuppressWarnings("rawtypes")
	public static List<String> getDataFromDB(Class dtoClass,String query)
	{
		List<String> data=null;

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
		data=getDbData(session, query);
		//logger.info("flag is : " +flag);
		return data;
		
		
	}
	


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
		
		logger.info("Connection exception Received");
		return flag;
		}
	}
		
	

		@SuppressWarnings("rawtypes")
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
			flag=validateDatainDB(session, query);
			session.close();
			factory.close();
			logger.info("flag is : " +flag);
			return flag;
			
		
		}
		
		public static boolean validateKernelDB(String queryStr)
		{
			boolean flag=false;
		
			try {
				if(BaseTestCase.environment.equalsIgnoreCase("dev"))
				factory = new Configuration().configure("kerneldev.cfg.xml").buildSessionFactory();
				else if(BaseTestCase.environment.equalsIgnoreCase("qa"))
					factory = new Configuration().configure("kernelqa.cfg.xml").buildSessionFactory();
				session = factory.getCurrentSession();
				session.beginTransaction();
			} catch (HibernateException e) {
				logger.info("Exception recived in DB Connection");
				e.printStackTrace();
				return false;
			}
			
			
			flag=validateDatainDB(session, queryStr);
				session.close();
				factory.close();
			logger.info("obtained objects count from DB is : " +flag);
			return flag;
			

		}

		
		
		/**
		 * @param queryStr containing query to obtain data count in table
		 * @return count obtained from db
		 */
		public static long validateDBCount(String queryStr)
		{
			long flag=0;
			
			try {
				if(BaseTestCase.environment.equalsIgnoreCase("dev"))
					factory = new Configuration().configure("masterdatadev.cfg.xml").buildSessionFactory();
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
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
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

@AfterClass(alwaysRun=true)
public void closingSession()
{
	
	session1.getTransaction().commit();
	factory.close();
	session1.close();
	
	
}

}
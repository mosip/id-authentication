package io.mosip.dbaccess;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;

/**
 * @author Ravi Kant
 *
 */
public class KernelTables {
	public static SessionFactory factory;
	static Session session;
	public static List<Object> objs = null;
	private static Logger logger = Logger.getLogger(KernelMasterDataR.class);
	
	@SuppressWarnings("deprecation")
	public static boolean kernelMasterData_dbconnectivityCheck()
	{
		boolean flag=false;
		try {	
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
	
		 objs = (List<Object>) query.list();
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
}
package io.mosip.dbaccess;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.transform.Transformers;


/**
 * @author Ravi Kant
 *
 */
public class MasterDataGetRequests {
	
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(KernelMasterDataR.class);

	@SuppressWarnings("deprecation")
	public static long validateDB(String queryStr)
	{
		long flag=0;
		
		try {
			factory = new Configuration().configure("masterdatainteg.cfg.xml").buildSessionFactory();	
			session = factory.getCurrentSession();
			session.beginTransaction();
		} catch (HibernateException e) {
			logger.info("Exception recived in DB Connection");
			e.printStackTrace();
			return 0;
		}
		
		
		flag=getdata(session, queryStr);
		
		// commit the transaction
				session.getTransaction().commit();
				session.close();
				factory.close();
		logger.info("obtained objects count from DB is : " +flag);
		return flag;
		

	}
	
	@SuppressWarnings("unchecked")
	private static long getdata(Session session, String queryStr)
	{
				
		String queryString=queryStr;
		
		Query query = session.createSQLQuery(queryString);
	
		long count = ((BigInteger) query.getSingleResult()).longValue();
		
		
		return count;
	
	}
}
	
/*	

public class MasterDataGetRequests {
	
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(KernelMasterDataR.class);

	@SuppressWarnings("deprecation")
	public static List<?> getData(String queryStr, Class dtoClass){
		
		try {
			factory = new Configuration().configure("masterData.cfg.xml")
					.addAnnotatedClass(dtoClass).buildSessionFactory();	
						session = factory.getCurrentSession();
		} catch (HibernateException e) {
			logger.info("Connection exception Received");
			return null;
		}
					
					session.beginTransaction();
					
					Query query = session.createNativeQuery(queryStr);
					query.unwrap( org.hibernate.query.NativeQuery.class )
					.setResultTransformer( Transformers.aliasToBean(dtoClass) );
					
					List<?> result = query.getResultList();
					
					session.getTransaction().commit();
					session.close();
					factory.close();
					return result;
	}
}

	
	*/



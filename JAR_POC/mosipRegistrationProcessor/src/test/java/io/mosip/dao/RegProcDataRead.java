package io.mosip.dao;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.mosip.dto.AuditRequestDto;
import io.mosip.dto.SyncRegistrationDto;
import io.mosip.dto.SyncStatusDto;
import io.mosip.entity.AuditEntity;
import io.mosip.entity.RegistrationStatusEntity;

/**
 * 
 * @author M1047227
 *
 */

public class RegProcDataRead {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(RegProcDataRead.class);
	static String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc.cfg.xml";
	static String auditLogConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\audit.cfg.xml";
	static File registrationListConfigFile=new File(registrationListConfigFilePath);
	static File auditLogConfigFile=new File(auditLogConfigFilePath);
	@SuppressWarnings("deprecation")
	public static boolean regproc_dbconnectivityCheck()
	{
		boolean flag=false;
		try {	
			factory = new Configuration().configure(registrationListConfigFile)
					.addAnnotatedClass(RegistrationStatusEntity.class).buildSessionFactory();	
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
	public static SyncRegistrationDto regproc_dbDataInRegistrationList(String regId)
	{
		boolean flag=false;
		String hibernateConfigFile=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc.cfg.xml";
		File f=new File(hibernateConfigFile);
		factory = new Configuration().configure(registrationListConfigFile)
				.addAnnotatedClass(RegistrationStatusEntity.class).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		SyncRegistrationDto dto =validateRegIdinRegistrationList(session, regId);
		//	Assert.assertTrue(flag);
		logger.info("Flag is : " +flag);
		if(dto!=null)
		{
			//session.close();
			return dto;
		}

		/*else
				return flag;*/
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static RegistrationStatusEntity regproc_dbDataInRegistration(String regId)
	{
		boolean flag=false;

		factory = new Configuration().configure(registrationListConfigFile)
				.addAnnotatedClass(RegistrationStatusEntity.class).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		RegistrationStatusEntity dto =validateRegIdinRegistration(session, regId);
		//	Assert.assertTrue(flag);
		logger.info("Flag is : " +flag);
		if(dto!=null)
		{
			//session.close();
			return dto;
		}

		/*else
				return flag;*/
		return null;
	}


	private static RegistrationStatusEntity validateRegIdinRegistration(Session session,String regID)
	{
		logger.info("REg id inside db query :"+regID);
		int size ;
		String status_code = null;
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity();
		

		/*String queryString=" Select *"+
                        " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";*/
		String queryString=" Select *"+
				" From regprc.registration where regprc.registration.id= :regId_value ";
		
		logger.info("regId is : " +regID);																																			
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId_value", regID);
		@SuppressWarnings("unchecked")

		List<Object> objs = (List<Object>) query.list();
		//logger.info("First Element of List Elements are : " +objs.get(1));
		size=objs.size();
		logger.info("Size is : " +size);

		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			//status_code = (String) (TestData[3]);
			registrationStatusEntity.setId((String)TestData[0]);
			registrationStatusEntity.setRegistrationType((String)TestData[1]);
			registrationStatusEntity.setReferenceRegistrationId((String)TestData[2]);
			registrationStatusEntity.setStatusCode((String)TestData[3]);
			registrationStatusEntity.setLangCode((String)TestData[4]);
			registrationStatusEntity.setStatusComment((String)TestData[5]);
			registrationStatusEntity.setLatestRegistrationTransactionId((String)TestData[6]);
			registrationStatusEntity.setIsActive((boolean)TestData[9]);
			registrationStatusEntity.setCreatedBy((String)TestData[10]);
//			registrationStatusEntity.setCreateDateTime((LocalDateTime)TestData[11]);
			registrationStatusEntity.setUpdatedBy((String)TestData[12]);
//			registrationStatusEntity.setUpdateDateTime((LocalDateTime)TestData[13]);
			registrationStatusEntity.setIsDeleted((boolean)TestData[14]);
//			registrationStatusEntity.setDeletedDateTime((LocalDateTime)TestData[15]);
			registrationStatusEntity.setApplicantType((String)TestData[16]);
			

			logger.info("Status is : " +status_code);

			// commit the transaction
			session.getTransaction().commit();



			//Query q=session.createQuery(" from otp_transaction where ID='917248' ");


		}

		try {

			if(size==1)
			{
				// Assert.assertEquals(status_code, "PACKET_UPLOADED_TO_VIRUS_SCAN");
				return registrationStatusEntity;
			}
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static SyncRegistrationDto validateRegIdinRegistrationList(Session session,String regID)
	{
		logger.info("REg id inside db query :"+regID);
		int size ;
		String status_code = null;
		SyncRegistrationDto syncregistrationDto = new SyncRegistrationDto();
		

		/*String queryString=" Select *"+
                        " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";*/
		String queryString= "Select *"+
				" From regprc.registration_list where regprc.registration_list.reg_id= :regId_value";
		
		logger.info("regId is : " +regID);																																			
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId_value", regID);
		@SuppressWarnings("unchecked")

		List<Object> objs = (List<Object>) query.list();
		//logger.info("First Element of List Elements are : " +objs.get(1));
		size=objs.size();
		logger.info("Size is : " +size);

		Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			//status_code = (String) (TestData[3]);
			syncregistrationDto.setRegistrationId((String)TestData[1]);
			syncregistrationDto.setSyncType((String)TestData[2]);
			syncregistrationDto.setParentRegistrationId((String)TestData[3]);
			logger.info("TestData[4]: "+TestData[4].toString());
			syncregistrationDto.setSyncStatus(SyncStatusDto.valueOf((String)TestData[4]));
			syncregistrationDto.setStatusComment((String)TestData[5]);
			syncregistrationDto.setLangCode((String)TestData[6]);
			syncregistrationDto.setIsActive((boolean)TestData[7]);
			syncregistrationDto.setIsDeleted((boolean)TestData[12]);

			logger.info("Status is : " +status_code);
			session.getTransaction().commit();
		}
		try {
			if(size==1)
			{
				return syncregistrationDto;
			}
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
    public static boolean regproc_dbDeleteRecordInRegistrationList(String regId)
    {
          boolean flag=false;

          factory = new Configuration().configure(registrationListConfigFile)
                .addAnnotatedClass(RegistrationStatusEntity.class).buildSessionFactory();      
          session = factory.getCurrentSession();
          session.beginTransaction();

          int result =deleteRegIdinRegistrationList(session, regId);
          //    Assert.assertTrue(flag);
          logger.info("Flag is : " +flag);
          if(result>0)
          {
                //session.close();
                flag = true;
          }

          /*else
                      return flag;*/
          return flag;
    }
    
    @SuppressWarnings("deprecation")
    public static boolean regproc_dbDeleteRecordInRegistration(String regId)
    {
          boolean flag=false;

          factory = new Configuration().configure(registrationListConfigFile)
                .addAnnotatedClass(RegistrationStatusEntity.class).buildSessionFactory();      
          session = factory.getCurrentSession();
          session.beginTransaction();

          int result =deleteRegIdinRegistration(session, regId);
          //    Assert.assertTrue(flag);
          logger.info("Flag is : " +flag);
          if(result>0)
          {
                //session.close();
                flag = true;
          }

          /*else
                      return flag;*/
          return flag;
    }

    private static int  deleteRegIdinRegistrationList(Session session2, String regId) {
        logger.info("REg id inside db query :"+regId);
        int size ;
        String status_code = null;
        SyncRegistrationDto syncregistrationDto = new SyncRegistrationDto();
        

        /*String queryString=" Select *"+
                    " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";*/
        String queryString= "DELETE"+
                    " From regprc.registration_list where regprc.registration_list.reg_id= :regId_value";
        
        logger.info("regId is : " +regId);                                                                                                                                                                                                              
        Query query = session.createSQLQuery(queryString);
        query.setParameter("regId_value", regId);
        @SuppressWarnings("unchecked")

        int result=query.executeUpdate();
              
              

              logger.info("Status is : " +status_code);

              // commit the transaction
              session.getTransaction().commit();
              return result;



              //Query q=session.createQuery(" from otp_transaction where ID='917248' ");

  }
  
  private static int deleteRegIdinRegistration(Session session2, String regId) {
        logger.info("REg id inside db query :"+regId);
        int size ;
        String status_code = null;
        SyncRegistrationDto syncregistrationDto = new SyncRegistrationDto();
        

        /*String queryString=" Select *"+
                    " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";*/
        String queryString= "DELETE"+
                    " From regprc.registration where regprc.registration.id= :regId_value";
        
        logger.info("regId is : " +regId);                                                                                                                                                                                                              
        Query query = session.createSQLQuery(queryString);
        query.setParameter("regId_value", regId);
        @SuppressWarnings("unchecked")

        int result=query.executeUpdate();
              
              

              logger.info("Status is : " +status_code);

              // commit the transaction
              session.getTransaction().commit();
              return result;



              //Query q=session.createQuery(" from otp_transaction where ID='917248' ");

  }
  
  @SuppressWarnings("deprecation")
	public static AuditRequestDto regproc_dbDataInAuditLog(String regId, String refIdType, String appName, String eventName, LocalDateTime logTime )
	{
		boolean flag=false;
		
		factory = new Configuration().configure(auditLogConfigFile)
				.addAnnotatedClass(AuditEntity.class).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		AuditRequestDto dto =validateRegIdinAuditLog(session, regId, refIdType, appName, eventName, logTime );
		//	Assert.assertTrue(flag);
		logger.info("Flag is : " +flag);
		if(dto!=null)
		{
			//session.close();
			return dto;
		}

		/*else
				return flag;*/
		return null;
	}


private static AuditRequestDto validateRegIdinAuditLog(Session session2, String regId, String refIdType, String appName, String eventName, LocalDateTime logTime) {
	 logger.info("REg id inside validateRegIdinAuditLog :"+regId);
     int size ;
     String status_code = null;
     AuditRequestDto auditDto = new AuditRequestDto();
     Timestamp timestamp = Timestamp.valueOf(logTime);

     /*String queryString=" Select *"+
                 " From prereg.applicant_demographic where prereg.applicant_demographic.prereg_id= :preId_value ";*/
     String queryString= "Select *"+
                 " From audit.app_audit_log where audit.app_audit_log.app_name = :appName and audit.app_audit_log.ref_id_type= :refIdType "
                 + "and audit.app_audit_log.ref_id= :regId and audit.app_audit_log.event_name= :eventName and audit.app_audit_log.action_dtimes= :logTime";
     /*String queryString= "Select *"+
             " From audit.app_audit_log where audit.app_audit_log.app_name= :appName";*/
     
     logger.info("regId is : " +regId);                                                                                                                                                                                                              
     Query query = session.createSQLQuery(queryString);
 //   
     query.setParameter("appName", appName);
     query.setParameter("refIdType", refIdType);
     query.setParameter("regId", regId);
     query.setParameter("eventName", eventName);
     query.setParameter("logTime", timestamp);

     @SuppressWarnings("unchecked")

     List<Object> objs = (List<Object>) query.getResultList();
     size = objs.size();
     logger.info("size :"+size);
     Object[] TestData = null;
		// reading data retrieved from query
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			//status_code = (String) (TestData[3]);
			auditDto.setApplicationName((String)TestData[12]);
			auditDto.setEventId((String)TestData[3]);
			
			session.getTransaction().commit();
		}
		logger.info("auditDto============================ : "+auditDto.getApplicationName());

           // commit the transaction
           session.getTransaction().commit();
           return auditDto;

}


}

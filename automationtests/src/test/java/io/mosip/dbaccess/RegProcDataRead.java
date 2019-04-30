package io.mosip.dbaccess;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.Assert;

import io.mosip.dbentity.OtpEntity;
import io.mosip.dbentity.RegistrationStatusEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.dbdto.SyncStatusDto;
import io.mosip.dbdto.SyncTypeDto;



public class RegProcDataRead {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(RegProcDataRead.class);

	@SuppressWarnings("deprecation")
	public static boolean regproc_dbconnectivityCheck()
	{
		boolean flag=false;
		try {	
			if(BaseTestCase.environment.equalsIgnoreCase("dev"))	
				factory = new Configuration().configure("regprocdev.cfg.xml")
						.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))	
						factory = new Configuration().configure("regprocqa.cfg.xml")
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
	public static SyncRegistrationDto regproc_dbDataInRegistrationList(String regId)
	{
		boolean flag=false;

		if(BaseTestCase.environment.equalsIgnoreCase("dev"))	
			factory = new Configuration().configure("regprocdev.cfg.xml")
					.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			else
			{
				if(BaseTestCase.environment.equalsIgnoreCase("qa"))	
					factory = new Configuration().configure("regprocqa.cfg.xml")
							.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			}
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

		if(BaseTestCase.environment.equalsIgnoreCase("dev"))	
			factory = new Configuration().configure("regprocdev.cfg.xml")
					.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			else
			{
				if(BaseTestCase.environment.equalsIgnoreCase("qa"))	
					factory = new Configuration().configure("regprocqa.cfg.xml")
							.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
			}
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

			// commit the transaction
			session.getTransaction().commit();



			//Query q=session.createQuery(" from otp_transaction where ID='917248' ");


		}

		try {

			if(size==1)
			{
				// Assert.assertEquals(status_code, "PACKET_UPLOADED_TO_VIRUS_SCAN");
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

          if(BaseTestCase.environment.equalsIgnoreCase("dev"))	
				factory = new Configuration().configure("regprocdev.cfg.xml")
						.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))	
						factory = new Configuration().configure("regprocqa.cfg.xml")
								.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				}    
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

          if(BaseTestCase.environment.equalsIgnoreCase("dev"))	
				factory = new Configuration().configure("regprocdev.cfg.xml")
						.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				else
				{
					if(BaseTestCase.environment.equalsIgnoreCase("qa"))	
						factory = new Configuration().configure("regprocqa.cfg.xml")
								.addAnnotatedClass(OtpEntity.class).buildSessionFactory();	
				}     
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


}
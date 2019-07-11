package io.mosip.dbaccess;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.dbdto.AuditRequestDto;
import io.mosip.dbdto.ManualVerificationDTO;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.dbdto.TransactionStatusDTO;
import io.mosip.dbentity.RegistrationStatusEntity;
import io.mosip.registrationProcessor.util.RegProcApiRequests;


/**
 * This class contains all the required data operations for reg proc module
 * 
 * @author Sayeri Mishra
 *
 */
public class RegProcDataRead {
	SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(RegProcDataRead.class);
	RegProcApiRequests apiRequests =  new RegProcApiRequests(); 
	String auditLogConfigFilePath=apiRequests.getResourcePath()+"auditinteg.cfg.xml";
	File auditLogConfigFile=new File(auditLogConfigFilePath);
	
	public Session getCurrentSession() {
		SessionFactory factory;
		Session session;
		factory=new Configuration().configure("regproc_qa.cfg.xml").buildSessionFactory();
	 session = factory.getCurrentSession();
	 return session;
	}

	//String registrationListConfigFilePath=apiRequests.getResourcePath()+"regproc_qa.cfg.xml";
	//File registrationListConfigFile=new File(registrationListConfigFilePath);
	
	public SyncRegistrationDto regproc_dbDataInRegistrationList(String regId){
	
		factory = new Configuration().configure("regproc_qa.cfg.xml").buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		SyncRegistrationDto dto =validateRegIdinRegistrationList(session,regId);
//		int count = countRegIdInRegistrationList(session,regId);
		if(dto!=null /*&& count== 0*/)
		{
			session.close();
			factory.close();
			return dto;
		}
		return null;
	}

	

	public RegistrationStatusEntity regproc_dbDataInRegistration(String regId)
	{
		factory = new Configuration().configure("regproc_qa.cfg.xml").buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		RegistrationStatusEntity dto =validateRegIdinRegistration(session, regId);
		if(dto!=null)
		{
			/*session.close();
			factory.close();*/
			return dto;
		}
		return null;
	}


	private RegistrationStatusEntity validateRegIdinRegistration(Session session,String regID)
	{
		logger.info("Reg id inside validateRegIdinRegistration method :"+regID);
		int size ;
		String status_code = null;
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity();
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
			registrationStatusEntity.setId((String)TestData[0]);
			/*registrationStatusEntity.setRegistrationType((String)TestData[1]);
			registrationStatusEntity.setReferenceRegistrationId((String)TestData[2]);
			registrationStatusEntity.setStatusCode((String)TestData[3]);
			registrationStatusEntity.setLangCode((String)TestData[4]);
			registrationStatusEntity.setStatusComment((String)TestData[5]);
			registrationStatusEntity.setLatestRegistrationTransactionId((String)TestData[6]);
			registrationStatusEntity.setIsActive((boolean)TestData[9]);
			registrationStatusEntity.setCreatedBy((String)TestData[10]);
			registrationStatusEntity.setUpdatedBy((String)TestData[12]);
			registrationStatusEntity.setIsDeleted((boolean)TestData[14]);
			registrationStatusEntity.setApplicantType((String)TestData[16]);
			logger.info("Status is : " +status_code);*/

			// commit the transaction
			session.getTransaction().commit();

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

	private SyncRegistrationDto validateRegIdinRegistrationList(Session session, String regID)
	{
		logger.info("REg id inside db query :"+regID);
		int size ;
		String status_code = null;
		SyncRegistrationDto syncregistrationDto = new SyncRegistrationDto();

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
			/*syncregistrationDto.setSyncType((String)TestData[2]);
			syncregistrationDto.setParentRegistrationId((String)TestData[3]);
			syncregistrationDto.setSyncStatus(SyncStatusDto.valueOf((String)TestData[4]));
			syncregistrationDto.setStatusComment((String)TestData[5]);
			syncregistrationDto.setLangCode((String)TestData[6]);
			syncregistrationDto.setIsActive((boolean)TestData[7]);
			syncregistrationDto.setIsDeleted((boolean)TestData[12]);*/

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

	public List<Object> countRegIdInRegistrationList(String regId) {
		factory = new Configuration().configure("regproc_qa.cfg.xml").buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();
		
		String queryString= "Select regprc.registration_list.reg_id, count(1)"+
				" From regprc.registration_list where regprc.registration_list.reg_id= :regId_value "
				+ "Group By regprc.registration_list.reg_id Having count(1)>1";

		logger.info("regId is : " +regId);																																			
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId_value", regId);
		List<Object> result = query.getResultList();
		logger.info("result==== : "+result);
		
		session.getTransaction().commit();
		return result;
	}
	
	public List<Object> countRegIdInRegistration(String regIds) {
		factory = new Configuration().configure("regproc_qa.cfg.xml").buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();
		
		String queryString= "Select regprc.registration.id, count(1)"+
				" From regprc.registration where regprc.registration.id= :regId_value "
				+ "Group By regprc.registration.id Having count(1)>1";

		logger.info("regId is : " +regIds);																																			
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId_value", regIds);
		List<Object> result = query.getResultList();
		logger.info("result==== : "+result);
		
		session.getTransaction().commit();
		return result;
	}
	
	public boolean regproc_dbDeleteRecordInRegistrationList(String regId)
	{
		boolean flag=false;


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

	public boolean regproc_dbDeleteRecordInRegistration(String regId)
	{
		boolean flag=false;

		session = factory.getCurrentSession();
		session.beginTransaction();

		int result =deleteRegIdinRegistration(session, regId);
		//    Assert.assertTrue(flag);
		logger.info("Flag is : " +flag);
		if(result>0)
		{
			/*session.close();
			factory.close();*/
			flag = true;
		}
		return flag;
	}

	private int  deleteRegIdinRegistrationList(Session session2, String regId) {
		logger.info("REg id inside db query :"+regId);
		String status_code = null;
		String queryString= "DELETE"+
				" From regprc.registration_list where regprc.registration_list.reg_id= :regId_value";

		logger.info("regId is : " +regId);                                                                                                                                                                                                              
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId_value", regId);
		int result=query.executeUpdate();

		logger.info("Status is : " +status_code);

		// commit the transaction
		session.getTransaction().commit();
		return result;

	}

	private int deleteRegIdinRegistration(Session session2, String regId) {
		logger.info("REg id inside db query :"+regId);
		String status_code = null;

		String queryString= "DELETE"+
				" From regprc.registration where regprc.registration.id= :regId_value";

		logger.info("regId is : " +regId);                                                                                                                                                                                                              
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId_value", regId);

		int result=query.executeUpdate();
		logger.info("Status is : " +status_code);

		// commit the transaction
		session.getTransaction().commit();
		return result;

	}

	public AuditRequestDto regproc_dbDataInAuditLog(String regId, String refIdType, String appName, String eventName, LocalDateTime logTime )
	{
		factory = new Configuration().configure(auditLogConfigFile).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		AuditRequestDto dto =validateRegIdinAuditLog(session, regId, refIdType, appName, eventName, logTime );
		if(dto!=null)
		{
			/*session.close();
			factory.close();*/
			return dto;
		}
		return null;
	}


	private AuditRequestDto validateRegIdinAuditLog(Session session2, String regId, String refIdType, String appName, String eventName, LocalDateTime logTime) {
		logger.info("REg id inside validateRegIdinAuditLog :"+regId);
		int size ;
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

		List<Object> objs = (List<Object>) query.list();
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



	public ManualVerificationDTO regproc_dbDataInManualVerification(String regIds, String matchedRegIds,
			String statusCodeRes) {
		factory = new Configuration().configure("regproc_qa.cfg.xml").buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		ManualVerificationDTO dto =validateInManualVerification(session, regIds,matchedRegIds,statusCodeRes);
		
		if(dto!=null)
		{
			/*session.close();
			factory.close();*/
			return dto;
		}
		return null;
	}



	private ManualVerificationDTO validateInManualVerification(Session session2, String regIds, String matchedRegIds,
			String statusCodeRes) {
		logger.info("REg id inside db query :"+regIds);
		int size ;
		String status_code = null;
		ManualVerificationDTO manualVerificationDto = new ManualVerificationDTO();

		String queryString= "Select *"+
				" From regprc.reg_manual_verification where regprc.reg_manual_verification.reg_id= :regIds AND "
				+ "regprc.reg_manual_verification.matched_ref_id= :matchedRegIds AND regprc.reg_manual_verification.status_code= :statusCodeRes";

		logger.info("regId is : " +regIds);																																			
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regIds", regIds);
		query.setParameter("matchedRegIds", matchedRegIds);
		query.setParameter("statusCodeRes", statusCodeRes);

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
			
			manualVerificationDto.setRegId((String)TestData[0]);
			manualVerificationDto.setMatchedRefId((String)TestData[1]);
			manualVerificationDto.setStatusCode((String)TestData[5]);

			logger.info("Status is : " +status_code);
			session.getTransaction().commit();
		}
		try {
			if(size==1)
			{
				return manualVerificationDto;
			}
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}



	public boolean updateStatusInManualVerification(String userId, String statusCode) {
		
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		
		 
		 String queryString = "Update regprc.reg_manual_verification set status_code= :statusCode "
			+ " where mv_usr_id= :userId";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("statusCode", statusCode); 
		 query.setParameter("userId", userId); 
		
		 int update = query.executeUpdate();
		 
		 t.commit();
		 if(update!= 0 ) {
			 return true;
		 }
		return false;
	}



	


}
package io.mosip.dbaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



public class RegProcDBCleanUp {
	SessionFactory factory;
	Session session;
	private static Logger logger = Logger.getLogger(RegProcDBCleanUp.class);
	String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc_qa.cfg.xml";
	File registrationListConfigFile=new File(registrationListConfigFilePath);
	public SessionFactory getSessionFactory() {
		factory=new Configuration().configure(registrationListConfigFile).buildSessionFactory();
		return factory;
	}
	
	public void deleteFromRegProcTables(String regID,String queryString) {
		logger.info("Reistration ID is :: "+regID);
		SessionFactory sessionFactory= getSessionFactory();
		session=sessionFactory.getCurrentSession();
		session.beginTransaction();
		//String queryString1="DELETE"+" FROM regprc.applicant_iris WHERE reg_id = :regIdValue";
		Query query=session.createSQLQuery(queryString);
		query.setParameter("regIdValue",regID);
		int result=query.executeUpdate();
		session.getTransaction().commit();
		session.close();
		sessionFactory.close();
		logger.info(regID + " Packet has been cleared from all tables");
	}
	
	public void prepareQueryList(String regID) {
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		String deleteTransaction="DELETE"+" FROM regprc.registration_transaction WHERE reg_id = :regIdValue";
		String deleteApplicantDocument="DELETE"+" FROM regprc.applicant_document WHERE reg_id = :regIdValue";
		String deleteApplicantDemographic="DELETE"+" FROM regprc.applicant_demographic WHERE reg_id = :regIdValue";
		
		String deleteApplicantFingerprint="DELETE"+" FROM regprc.applicant_fingerprint WHERE reg_id = :regIdValue";
		String deleteApplicantIris="DELETE"+" FROM regprc.applicant_iris WHERE reg_id = :regIdValue";
		String deleteApplicantPhotograph="DELETE"+" FROM regprc.applicant_photograph WHERE reg_id = :regIdValue";
		String deleteBiometricException="DELETE"+" FROM regprc.biometric_exception WHERE reg_id = :regIdValue";
		String deleteDemographicDedupe="DELETE"+" FROM regprc.individual_demographic_dedup WHERE reg_id = :regIdValue";
		String deleteUserRegistration="DELETE"+" FROM regprc.qcuser_registration WHERE reg_id = :regIdValue";
		String deleteCenterMachine="DELETE"+" FROM regprc.reg_center_machine WHERE reg_id = :regIdValue";
		String deleteManualVerification="DELETE"+" FROM regprc.reg_manual_verification WHERE reg_id = :regIdValue";
		String deleteOsi="DELETE"+" FROM regprc.reg_osi WHERE reg_id = :regIdValue";
		String deleteAbis="DELETE"+" FROM regprc.reg_abisref WHERE reg_id = :regIdValue";
		String deleteRegistration="DELETE"+" FROM regprc.registration WHERE id = :regIdValue";
		List<String> queryList=new ArrayList<String>();
		queryList.add(deleteTransaction);
		queryList.add(deleteApplicantDocument);
		queryList.add(deleteApplicantDemographic);
		queryList.add(deleteApplicantFingerprint);
		queryList.add(deleteApplicantIris);
		queryList.add(deleteApplicantPhotograph);
		queryList.add(deleteBiometricException);
		queryList.add(deleteDemographicDedupe);
		queryList.add(deleteUserRegistration);
		queryList.add(deleteCenterMachine);
		queryList.add(deleteManualVerification);
		queryList.add(deleteOsi);
		queryList.add(deleteAbis);
		queryList.add(deleteRegistration);
		for(String query:queryList) {
			cleanUp.deleteFromRegProcTables(regID, query);
		}

	}
}

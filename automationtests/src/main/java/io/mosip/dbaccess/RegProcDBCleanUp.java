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
		logger.info("Registration ID is :: "+regID);
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
		String deleteApplicantAbis="DELETE"+" FROM regprc.reg_abisref WHERE reg_id = :regIdValue";
		String deleteApplicantUin="DELETE"+" FROM regprc.reg_uin WHERE reg_id = :regIdValue";
		String deleteRegistrationList="DELETE"+" FROM regprc.registration_list WHERE reg_id = :regIdValue";
		String deleteManualVerification="DELETE"+" FROM regprc.reg_manual_verification WHERE reg_id = :regIdValue";
		String deleteRegistration="DELETE"+" FROM regprc.registration WHERE id = :regIdValue";
		String deleteIndividualDemographicDedup="DELETE" +" FROM regprc.individual_demographic_dedup WHERE reg_id= :regIdValue";
		List<String> queryList=new ArrayList<String>();
		queryList.add(deleteTransaction);
		queryList.add(deleteApplicantAbis);
		queryList.add(deleteApplicantUin);
		queryList.add(deleteRegistrationList);
		queryList.add(deleteManualVerification);
		queryList.add(deleteRegistration);
		queryList.add(deleteIndividualDemographicDedup);
		for(String query:queryList) {
			cleanUp.deleteFromRegProcTables(regID, query);
		}

	}
	public static void main(String[] args) {
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		String deleteTransaction="DELETE"+" FROM regprc.registration_transaction WHERE reg_id = :regIdValue";
		String deleteApplicantAbis="DELETE"+" FROM regprc.reg_abisref WHERE reg_id = :regIdValue";
		String deleteApplicantUin="DELETE"+" FROM regprc.reg_uin WHERE reg_id = :regIdValue";
		String deleteRegistrationList="DELETE"+" FROM regprc.registration_list WHERE reg_id = :regIdValue";
		String deleteManualVerification="DELETE"+" FROM regprc.reg_manual_verification WHERE reg_id = :regIdValue";
		String deleteRegistration="DELETE"+" FROM regprc.registration WHERE id = :regIdValue";
		String deleteIndividualDemographicDedup="DELETE" +" FROM regprc.individual_demographic_dedup WHERE reg_id= :regIdValue";
		List<String> queryList=new ArrayList<String>();
		queryList.add(deleteIndividualDemographicDedup);
		queryList.add(deleteTransaction);
		queryList.add(deleteApplicantAbis);
		queryList.add(deleteApplicantUin);
		queryList.add(deleteRegistrationList);
		queryList.add(deleteManualVerification);
		queryList.add(deleteRegistration);
		
		for(String query:queryList) {
			cleanUp.deleteFromRegProcTables("10007100260001220190522065002", query);
		}

	}
}

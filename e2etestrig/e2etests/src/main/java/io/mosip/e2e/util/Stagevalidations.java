package io.mosip.e2e.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.dto.TransactionStatusDTO;

public class Stagevalidations {
	TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
	String registrationListConfigFilePath=BaseUtil.getGlobalResourcePath()+"/src/test/resources/regproc_qa.cfg.xml";
	File registrationListConfigFile=new File(registrationListConfigFilePath);
	public Session getCurrentSession() {
		SessionFactory factory;
		Session session;
		factory=new Configuration().configure(registrationListConfigFile).buildSessionFactory();
	 session = factory.getCurrentSession();
	 return session;
	}
	/**
	 * 
	 * @param regId
	 * @return set of status of stages in reg proc
	 */
	public boolean readStatus(String regId) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		 String queryString="SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.trn_type_code,regprc.registration_transaction.status_code,regprc.registration_transaction.cr_dtimes,regprc.registration_transaction.status_comment" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId order by cr_dtimes";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 
		 List<String> list=query.getResultList();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;

			 if(TestData[1].toString().equalsIgnoreCase("UIN_GENERATOR")) {
				 if(TestData[2].toString().equalsIgnoreCase("PROCESSED") || TestData[2].toString().equalsIgnoreCase("IN_PROGRESS")) {
					 return true;
				 }
			 }
			 }
	        t.commit();
	        session.close();
			return false;
	}
	public String readStatusComment(String regId) {
		String comment="";
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		 String queryString="SELECT regprc.registration_transaction.status_code,regprc.registration_transaction.cr_dtimes,regprc.registration_transaction.status_comment" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId order by cr_dtimes";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 Map<String,String> errorMap=new HashMap<String,String>();
		 List<String> list=query.getResultList();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;
			 errorMap.put(TestData[0].toString(), TestData[2].toString());
			 }
		 for(Map.Entry<String, String> entry:errorMap.entrySet()) {
			 if(entry.getKey().equals("REPROCESS") || entry.getKey().equals("ERROR") || entry.getKey().equals("FAILED")) {
				 comment=entry.getValue();
			 }
		 }
	        t.commit();
	        session.close();
	        return comment;
	}
	
}

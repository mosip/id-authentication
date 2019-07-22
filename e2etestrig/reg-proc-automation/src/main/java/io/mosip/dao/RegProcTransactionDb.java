package io.mosip.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.dto.TransactionStatusDTO;


/**
 * 
 * @author M1047227
 *
 */
public class RegProcTransactionDb {
	
	
	TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
	String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc_qa.cfg.xml";
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
	public List<String> readStatus(String regId) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		
		 
		 String queryString="SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.trn_type_code,regprc.registration_transaction.status_code,regprc.registration_transaction.cr_dtimes,regprc.registration_transaction.status_comment" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId order by cr_dtimes";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 List<String> statusComment=new ArrayList<String>();
		 List<String> errorComment=new ArrayList<String>();
		 List<String> list=query.getResultList();
		 Map<String,String> mapOfTransaction=new LinkedHashMap<String,String>();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;
			 transactionStatus.setReg_id(TestData[0].toString());
			 transactionStatus.setTrn_type_code(TestData[1].toString());
			 transactionStatus.setStatus_Code(TestData[2].toString());
			 transactionStatus.setCr_dtimes(TestData[3].toString());
			 statusComment.add((String) TestData[1]);
			 errorComment.add(TestData[4].toString());
			 mapOfTransaction.put(TestData[1].toString(),TestData[2].toString());
			 }
		
		
		 	List<String> listOfEntries=new ArrayList<String>(mapOfTransaction.values());
		 	List<String> transactionList=new ArrayList<String>();
		 	for(int i=0;i<listOfEntries.size();i++) {
		 		transactionList.add(listOfEntries.get(i));
		 	}
		 	for(String status:transactionList) {
		 		if(status.equals("REPROCESS") || status.equalsIgnoreCase("ERROR") || status.equalsIgnoreCase("FAILURE")) {
		 			return errorComment;
		 		} else 
		 			continue;
		 	}
		 	
	        t.commit();
	        session.close();
			return transactionList;
	}
	

}
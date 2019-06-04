package io.mosip.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
	
	
	
	String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc_int.cfg.xml";
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
	public Set<String> readStatus(String regId) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		
		 
		 String queryString="SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.trn_type_code,regprc.registration_transaction.status_code,regprc.registration_transaction.cr_dtimes" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId"+" order by cr_dtimes";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 List<String> statusComment=new ArrayList<String>();
		 List<TransactionStatusDTO> listOfEntries=new ArrayList<TransactionStatusDTO>();
		 List<String> list=query.getResultList();
		 Map<String,String> packetTransactionStatus=new HashMap<String,String>();
		 for(Object obj: list) {
			 TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
			 TestData = (Object[]) obj;
			 statusComment.add((String) TestData[1]);
			 transactionStatus.setRegistrationId(TestData[0].toString());
			 transactionStatus.setStatus_code(TestData[2].toString());
			 transactionStatus.setTrn_type_code(TestData[1].toString());
			 transactionStatus.setCr_dtimes(TestData[3].toString());
			 listOfEntries.add(transactionStatus);
			 packetTransactionStatus.put(TestData[1].toString(),TestData[2].toString());
			 }
		
		 
		 List<TransactionStatusDTO> sortedregId=new ArrayList<TransactionStatusDTO>();
		  listOfEntries.sort((d1,d2)->d2.getCr_dtimes().compareTo(d1.getCr_dtimes()));
		 sortedregId.addAll(listOfEntries);
		 
		 Set<String> statusFromDb=new LinkedHashSet<String>();
		 for(TransactionStatusDTO statusDto:sortedregId) {
			 statusFromDb.add(statusDto.getStatus_code());
		 }
	        t.commit();
	        session.close();
	        
			return statusFromDb;
	}
	

}
package io.mosip.dbaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.dbdto.TransactionStatusDTO;
/**
 * 
 * @author M1047227
 *
 */
public class RegProcTransactionDb {
	
	private static Logger logger = Logger.getLogger(RegProcTransactionDb.class);
	
	String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc_qa.cfg.xml";
	File registrationListConfigFile=new File(registrationListConfigFilePath);
	public Session getCurrentSession() {
		SessionFactory factory;
		Session session;
		factory=new Configuration().configure(registrationListConfigFile).buildSessionFactory();
	 session = factory.getCurrentSession();
	 return session;
	}
	public List<String> readStatus(String regId) {
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
			 transactionStatus.setStatus_code(TestData[1].toString());
			 transactionStatus.setTrn_type_code(TestData[2].toString());
			 transactionStatus.setCr_dtimes(TestData[3].toString());
			 listOfEntries.add(transactionStatus);
			 packetTransactionStatus.put(TestData[1].toString(),TestData[2].toString());
			 }
		 	System.out.println(listOfEntries);
		 List<TransactionStatusDTO> sortedregId=listOfEntries.stream().sorted(Comparator.comparing(TransactionStatusDTO :: getCr_dtimes)).collect(Collectors.toList());
		 System.out.println("Sorted Order is :: "+sortedregId);
	        t.commit();
	        session.close();
			return statusComment;
	}
	public boolean compareTransactionOfDeactivatePackets(String regId,String testCaseName) {
		Session session=getCurrentSession();
		 Transaction t=session.beginTransaction();
		 boolean compareStatus=false;
		// String queryString="SELECT regprc.reg_id,regprc.status_code,regprc.status_comment FROM regprc.registration_transaction where reg_id='20916100110014920190218154630'";
		 
		 String queryString="SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.status_code,regprc.registration_transaction.status_comment" + 
		 		"	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId";
		 Query<String> query=session.createSQLQuery(queryString);
		 query.setParameter("regId", regId); 
		 Object[] TestData = null;
		 List<String> statusComment=new ArrayList<String>();
		 List<String> list=query.getResultList();
		 for(Object obj: list) {
			 TestData = (Object[]) obj;
			 statusComment.add((String) TestData[1]);
			 }
		 logger.info("DB Status_Codes are :: "+statusComment);
	        t.commit();
	        session.close();
	        if(testCaseName.equals("invalid_deactivatedUin")) {
	        	for(String status:statusComment) {
	        		if(status.equals("PACKET_UIN_UPDATION_FAILURE")) {
	        			compareStatus=true;
	        		}
	        	}
	        } else {
	        	for(String status:statusComment) {
	        		if(status.equals("PACKET_UIN_UPDATION_SUCCESS")) {
	        			compareStatus=true;
	        		}
	        	}
	        }
		return compareStatus;	
	}
	public static void main(String[] args) {
		RegProcTransactionDb db=new RegProcTransactionDb();
		db.readStatus("10007100260001220190522065002");
	}
}

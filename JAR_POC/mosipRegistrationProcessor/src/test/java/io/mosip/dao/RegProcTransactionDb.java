package io.mosip.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.entity.TransactionEntity;
import io.mosip.dto.TransactionStatusDTO;
/**
 * 
 * @author M1047227
 *
 */
public class RegProcTransactionDb {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(RegProcTransactionDb.class);
	TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
	public List<String> readStatus(String regId) {
		 factory = new Configuration().configure("regproc.cfg.xml")
	                .addAnnotatedClass(TransactionEntity.class).buildSessionFactory();
		 session = factory.getCurrentSession();
		 Transaction t=session.beginTransaction();
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
	        t.commit();
	        session.close();
			return statusComment;
	}
}

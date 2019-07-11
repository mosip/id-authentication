package io.mosip.dbaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.dbdto.TransactionStatusDTO;

public class GetUinFromDb {
	private static Logger logger = Logger.getLogger(GetUinFromDb.class);
	TransactionStatusDTO transactionStatus=new TransactionStatusDTO();
	String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\idRepo.cfg.xml";
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
		// String queryString="SELECT regprc.reg_id,regprc.status_code,regprc.status_comment FROM regprc.registration_transaction where reg_id='20916100110014920190218154630'";
		 
		 String queryString="SELECT  idrepo.uin.uin" + 
		 		" FROM idrepo.uin where idrepo.uin.reg_id= :regId";
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
	public static void main(String[] args) {
		GetUinFromDb getUin=new GetUinFromDb();
		getUin.readStatus("27847657360002520190318163030");
	}
}

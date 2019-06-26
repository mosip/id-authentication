package io.mosip.dbaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import io.mosip.dbdto.TransactionStatusDTO;
import io.mosip.registrationProcessor.util.RegProcApiRequests;

public class AbisDbTransactions {
	private static Logger logger = Logger.getLogger(AbisDbTransactions.class);
	TransactionStatusDTO transactionStatus = new TransactionStatusDTO();
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	String registrationListConfigFilePath = apiRequests.getResourcePath()+"regproc_qa.cfg.xml";
	File registrationListConfigFile = new File(registrationListConfigFilePath);

	public Session getCurrentSession() {
		SessionFactory factory;
		Session session;
		factory = new Configuration().configure(registrationListConfigFile).buildSessionFactory();
		session = factory.getCurrentSession();
		return session;
	}

	public String bioDedupeFailedCheck(String regId) {
		String transaction_status = "";

		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		String queryString = "SELECT regprc.registration_transaction.reg_id,regprc.registration_transaction.status_code,regprc.registration_transaction.status_comment,regprc.registration_transaction.upd_dtimes,regprc.registration_transaction.trn_type_code,regprc.registration_transaction.id"
				+ "	FROM regprc.registration_transaction where regprc.registration_transaction.reg_id= :regId order by upd_dtimes desc";
		Query<String> query = session.createSQLQuery(queryString);
		query.setParameter("regId", regId);
		Object[] TestData = null;
		List<String> statusComment = new ArrayList<String>();
		List<String> list = query.getResultList();
		Map<String, String> transactionIds = new HashMap<String, String>(); 
		//
		for (Object obj : list) {
			TestData = (Object[]) obj;
			statusComment.add((String) TestData[2]);
			transactionIds.put((String) TestData[4], (String) TestData[5]);
		}

		for (String status : statusComment) {
			if (status.equals("Potential match found while processing bio dedupe")) {
				transaction_status = transactionIds.get("BIOGRAPHIC_VERIFICATION");
				break;
			}
		}
		t.commit();
		session.close();
		return transaction_status;
	}

	public List<String> sendToAbisRequest(String refId) {
		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		String queryString = "SELECT regprc.abis_request.id,regprc.abis_request.req_batch_id,regprc.abis_request.request_type,regprc.abis_request.ref_regtrn_id,regprc.abis_request.abis_app_code"
				+ "	FROM regprc.abis_request where regprc.abis_request.ref_regtrn_id= :refId order by upd_dtimes desc";
		Query<String> query = session.createSQLQuery(queryString);
		query.setParameter("refId", refId);
		Object[] TestData = null;
		List<String> statusComment = new ArrayList<String>();
		List<String> list = query.getResultList();

		Map<String, String> id_appCode = new HashMap<String, String>();
		//
		for (Object obj : list) {
			TestData = (Object[]) obj;
			statusComment.add((String) TestData[0]);
			id_appCode.put(TestData[4].toString() + "_" + TestData[2], TestData[0].toString());

		}
		List<String> abisRequestTransactionIds = new ArrayList<String>();
		for (Map.Entry<String, String> entry : id_appCode.entrySet()) {
			if (entry.getKey().contains("IDENTIFY")) {
				abisRequestTransactionIds.add(entry.getValue());
			}
		}
		t.commit();
		session.close();
		return abisRequestTransactionIds;
	}

	public List<String> sendToAbisResponse(List<String> abisRequestTransactionIds) {
		String queryVar = "";
		String executingQueryVar = "";
		for (String transaction : abisRequestTransactionIds) {
			queryVar = queryVar + "'" + transaction + "',";
			executingQueryVar = queryVar.substring(0, queryVar.length() - 1);
		}
		executingQueryVar = "(" + executingQueryVar + ")";
		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		String queryString = "SELECT regprc.abis_response.id,regprc.abis_response.abis_req_id,regprc.abis_response.status_code"
				+ "	FROM regprc.abis_response where regprc.abis_response.abis_req_id in " + executingQueryVar
				+ " order by upd_dtimes desc";
		Query<String> query = session.createSQLQuery(queryString);
		// query.setParameter("refId", listOfRefIds);
		Object[] TestData = null;
		List<String> statusComment = new ArrayList<String>();
		List<String> list = query.getResultList();
		Map<String, String> id_statusCode = new HashMap<String, String>();
		for (Object obj : list) {
			TestData = (Object[]) obj;
			statusComment.add((String) TestData[0]);
			id_statusCode.put(TestData[0].toString(), TestData[2].toString());
		}
		List<String> transactionIds = new ArrayList<String>();
		{
			for (Map.Entry<String, String> entry : id_statusCode.entrySet()) {
				if (entry.getValue().equals("SUCCESS")) {
					transactionIds.add(entry.getKey());
				} else
					break;
			}
		}
		t.commit();
		session.close();

		return transactionIds;
	}

	public Map<String, String> sendToAbisDetail(List<String> abisResponseTransactionIds) {
		String queryVar = "";
		String executingQueryVar = "";
		for (String transaction : abisResponseTransactionIds) {
			queryVar = queryVar + "'" + transaction + "',";
			executingQueryVar = queryVar.substring(0, queryVar.length() - 1);
		}
		executingQueryVar = "(" + executingQueryVar + ")";
		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		String queryString = "SELECT regprc.abis_response_det.abis_resp_id,regprc.abis_response_det.matched_bio_ref_id,regprc.abis_response_det.score"
				+ "	FROM regprc.abis_response_det where regprc.abis_response_det.abis_resp_id in " + executingQueryVar
				+ " order by upd_dtimes desc";
		Query<String> query = session.createSQLQuery(queryString);
		// query.setParameter("refId", listOfRefIds);
		Object[] TestData = null;
		List<String> statusComment = new ArrayList<String>();
		List<String> list = query.getResultList();
		Map<String, String> id_statusCode = new HashMap<String, String>();
		for (Object obj : list) {
			TestData = (Object[]) obj;
			statusComment.add((String) TestData[0]);
			id_statusCode.put(TestData[0].toString() + "_" + TestData[1].toString(), TestData[2].toString());
		}
		t.commit();
		session.close();
		return id_statusCode;
	}

	public Map<String, Set<String>> extractIds(Map<String, String> getMatchedId) {
/*		AbisDbTransactions db = new AbisDbTransactions();
		String ref_id = db.bioDedupeFailedCheck(regId);
		List<String> abisRequestTransactionIds = db.sendToAbisRequest(ref_id);
		List<String> abisResponseTransactionIds = db.sendToAbisResponse(abisRequestTransactionIds);
		Map<String, String> getMatchedId = db.sendToAbisDetail(abisResponseTransactionIds);*/
		Set<String> abisResponseIds = new LinkedHashSet<String>();
		Set<String> matchedBioRefIds = new LinkedHashSet<String>();
		for (Map.Entry<String, String> entry : getMatchedId.entrySet()) {
			String abisResponseId = entry.getKey().substring(0, entry.getKey().lastIndexOf('_'));
			String matchedBioRefId = entry.getKey().substring(entry.getKey().indexOf('_') + 1, entry.getKey().length());
			abisResponseIds.add(abisResponseId);
			matchedBioRefIds.add(matchedBioRefId);
		}
		Map<String, Set<String>> mapOfIds = new HashMap<String, Set<String>>();
		mapOfIds.put("abisResponseIds", abisResponseIds);
		mapOfIds.put("matchedBioRefId", matchedBioRefIds);

		return mapOfIds;
	}

	public List<String> extractRegIds(Set<String> matchedBio) {
		String queryVar = "";
		String executingQueryVar = "";
		for (String transaction : matchedBio) {
			queryVar = queryVar + "'" + transaction + "',";
			executingQueryVar = queryVar.substring(0, queryVar.length() - 1);
		}
		executingQueryVar = "(" + executingQueryVar + ")";
		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		String queryString = "SELECT regprc.reg_bio_ref.reg_id,regprc.reg_bio_ref.bio_ref_id"
				+ "	FROM regprc.reg_bio_ref where regprc.reg_bio_ref.bio_ref_id in " + executingQueryVar
				+ " order by upd_dtimes desc";
		Query<String> query = session.createSQLQuery(queryString);
		// query.setParameter("refId", listOfRefIds);
		Object[] TestData = null; 
		List<String> regIds = new ArrayList<String>();
		List<String> list = query.getResultList();
		Map<String, String> id_statusCode = new HashMap<String, String>();
		for (Object obj : list) {
			TestData = (Object[]) obj;
			regIds.add((String) TestData[0]);

		}
		t.commit();
		session.close();
		return regIds;
	}

	public List<String> getStatusOfBiOmAtchIds(List<String> regIds) {
		String queryVar = "";
		String executingQueryVar = "";
		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		for (String transaction : regIds) {
			queryVar = queryVar + "'" + transaction + "',";
			executingQueryVar = queryVar.substring(0, queryVar.length() - 1);
		}
		executingQueryVar = "(" + executingQueryVar + ")";
		String queryString = "SELECT regprc.registration.id,regprc.registration.status_code"
				+ "	FROM regprc.registration where regprc.registration.id in " + executingQueryVar
				+ " order by upd_dtimes desc";

		Query<String> query = session.createSQLQuery(queryString);
		// query.setParameter("refId", listOfRefIds);
		Object[] TestData = null;

		List<String> list = query.getResultList();
		Map<String, String> id_statusCode = new HashMap<String, String>();
		for (Object obj : list) {
			TestData = (Object[]) obj;
			id_statusCode.put(TestData[0].toString(), TestData[1].toString());
		}
		List<String> listOfRids = new ArrayList<String>();
		for (Map.Entry<String, String> entry : id_statusCode.entrySet()) {
			if (entry.getValue().equals("PROCESSED") || entry.getValue().equals("REJECTED")) {
				listOfRids.add(entry.getKey());
			}
		}
		t.commit();
		session.close();

		return listOfRids;
	}

	public List<String> getMatchedRegIds(String regId) {
		Session session = getCurrentSession();
		Transaction t = session.beginTransaction();
		String queryString = "SELECT regprc.reg_manual_verification.reg_id,regprc.reg_manual_verification.matched_ref_id"
				+ "	FROM regprc.reg_manual_verification where regprc.reg_manual_verification.reg_id= :regId order by upd_dtimes desc";
		Query<String> query = session.createSQLQuery(queryString);
		query.setParameter("regId", regId);
		Object[] TestData = null;
		List<String> matchedRegIds = new ArrayList<String>();
		List<String> list = query.getResultList();

		for (Object obj : list) {
			TestData = (Object[]) obj;
			matchedRegIds.add((String) TestData[1]);

		}

		t.commit();
		session.close();
		return matchedRegIds;
	} 


}

package app.dbaccess;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import app.entity.TransactionEntity;

public class DBAccessImpl {

	public List<TransactionEntity> getRegTransactionForRegId(String regid) {
		List<TransactionEntity> list = null;
		String query = "from TransactionEntity trEntity where reg_id='" + regid + "' order by trEntity.createDateTime";
		Session session = DBUtil.obtainSession();
		Query q = session.createQuery(query);
		list = q.getResultList();
		return list;
	}

	public List<TransactionEntity> getTransactionsForRegIds(List<String> regIds) {
		List<TransactionEntity> list = null;
		return list;
	}

}

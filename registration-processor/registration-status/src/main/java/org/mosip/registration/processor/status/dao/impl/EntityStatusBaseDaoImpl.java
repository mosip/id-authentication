package org.mosip.registration.processor.status.dao.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.mosip.registration.processor.status.dao.EntityStatusBaseDao;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Shashank Agrawal
 * @author Jyoti Prakash Nayak
 *
 */
@Component("entityStatusBaseDao")
public class EntityStatusBaseDaoImpl implements EntityStatusBaseDao {

	@Autowired
	EntityManager manager;

	public List<RegistrationStatusEntity> findbyfilesByThreshold(String statusCode, int threshholdTime) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
		LocalDateTime localDateTime = LocalDateTime.now();

		LocalDateTime expiredDateTime = localDateTime.minusMinutes(threshholdTime);
		String queryStr = "SELECT DISTINCT " + alias + " FROM  " + className + " " + alias + " WHERE " + alias
				+ ".status=:statusCode" + " AND " + alias + ".updateDateTime < :date";

		params.put("statusCode", statusCode);
		params.put("date", expiredDateTime);

		return getRegistrationStatusList(queryStr, params, RegistrationStatusEntity.class);

	}

	public List<RegistrationStatusEntity> getByIds(List<String> ids) {

		Map<String, Object> params = new HashMap<>();
		String className = RegistrationStatusEntity.class.getSimpleName();

		String alias = RegistrationStatusEntity.class.getName().toLowerCase().substring(0, 1);
		String queryStr = "SELECT " + alias + " FROM  " + className + " " + alias + " WHERE " + alias
				+ ".enrolmentId IN :ids";
		params.put("ids", ids);

		return getRegistrationStatusList(queryStr, params, RegistrationStatusEntity.class);
	}

	/**
	 * @param queryExp
	 * @param params
	 * @param clazz
	 * @return the list of Registration Status
	 */
	@SuppressWarnings("unchecked")
	private List<RegistrationStatusEntity> getRegistrationStatusList(String queryExp, Map<String, Object> params,
			Class<?> clazz) {

		Query query = manager.createQuery(queryExp, clazz);
		if (params != null) {
			params.forEach(query::setParameter);
		}
		return query.getResultList();
	}
}

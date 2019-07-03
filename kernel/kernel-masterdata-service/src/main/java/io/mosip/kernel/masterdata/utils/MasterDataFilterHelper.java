package io.mosip.kernel.masterdata.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sagar Mahapatra
 * @since 1.00
 *
 */
@Repository
@Transactional(readOnly = true)
public class MasterDataFilterHelper {

	private static final String LANGCODE_COLUMN_NAME = "langCode";
	private static final String FILTER_VALUE_UNIQUE = "unique";
	private static final String FILTER_VALUE_ALL = "all";

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${mosip.kernel.filtervalue.max_columns:20}")
	int filterValueMaxColumns;

	public MasterDataFilterHelper(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public <E> List<String> filterValues(Class<E> entity, String columnName, String columnType, String languageCode) {
		List<String> results;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<E> root = criteriaQuery.from(entity);
		criteriaQuery.select(root.get(columnName));
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
		criteriaQuery.where(criteriaBuilder.equal(root.get(LANGCODE_COLUMN_NAME), languageCode));
		if (columnType.equals(FILTER_VALUE_UNIQUE)) {
			criteriaQuery.distinct(true);
		} else if (columnType.equals(FILTER_VALUE_ALL)) {
			criteriaQuery.distinct(false);
		}
		TypedQuery<String> q = entityManager.createQuery(criteriaQuery);
		results = q.setMaxResults(filterValueMaxColumns).getResultList();
		return results;
	}

}

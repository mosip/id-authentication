package io.mosip.kernel.masterdata.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class that provides generic methods for implementation of filter values
 * search.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0
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

	public <E> List<?> filterValues(Class<E> entity, String columnName, String columnType, String languageCode) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<E> root = criteriaQuery.from(entity);
		Path<Object> path = root.get(columnName);
		Class<? extends Object> type = path.getJavaType();
		String fieldType = type.getTypeName();
		if (LocalDateTime.class.getName().equals(fieldType)) {
			return filterValuesByType(entity, LocalDateTime.class, columnName, columnType, languageCode);
		} else if (String.class.getName().equals(fieldType)) {
			return filterValuesByType(entity, String.class, columnName, columnType, languageCode);
		} else if (Boolean.class.getName().equals(fieldType)) {
			return filterValuesByType(entity, Boolean.class, columnName, columnType, languageCode);
		} else if (LocalDate.class.getName().equals(fieldType)) {
			return filterValuesByType(entity, LocalDate.class, columnName, columnType, languageCode);
		} else if (LocalTime.class.getName().equals(fieldType)) {
			return filterValuesByType(entity, LocalTime.class, columnName, columnType, languageCode);
		}
		return Collections.emptyList();
	}

	public <E, T> List<T> filterValuesByType(Class<E> entity, Class<T> type, String columnName, String columnType,
			String languageCode) {
		List<T> results;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
		Root<E> root = criteriaQuery.from(entity);
		criteriaQuery.select(root.get(columnName));
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
		criteriaQuery.where(criteriaBuilder.equal(root.get(LANGCODE_COLUMN_NAME), languageCode));
		if (columnType.equals(FILTER_VALUE_UNIQUE)) {
			criteriaQuery.distinct(true);
		} else if (columnType.equals(FILTER_VALUE_ALL)) {
			criteriaQuery.distinct(false);
		}
		TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
		results = typedQuery.setMaxResults(filterValueMaxColumns).getResultList();
		return results;
	}
}

package io.mosip.kernel.masterdata.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;

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
	private static final String WILD_CARD_CHARACTER = "%";
	private static final String STATUS_ATTRIBUTE = "isActive";
	private static final String STATUS_TRUE_FLAG = "true";

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${mosip.kernel.filtervalue.max_columns:20}")
	int filterValueMaxColumns;

	public MasterDataFilterHelper(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public <E, T> List<T> filterValues(Class<E> entity, FilterDto filterDto, FilterValueDto filterValueDto) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<E> root = criteriaQuery.from(entity);
		Path<Object> path = root.get(filterDto.getColumnName());
		return (List<T>) filterValuesByType(entity, path.getJavaType(), filterDto, filterValueDto.getLanguageCode());
	}

	private <E, T> List<T> filterValuesByType(Class<E> entity, Class<T> type, FilterDto filterDto,
			String languageCode) {
		String columnName = filterDto.getColumnName();
		String text = filterDto.getText();
		String columnType = filterDto.getType();
		List<T> results;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
		Root<E> root = criteriaQuery.from(entity);
		criteriaQuery.select(root.get(columnName));
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
		criteriaQuery.where(criteriaBuilder.equal(root.get(LANGCODE_COLUMN_NAME), languageCode));
		if (!(root.get(columnName).getJavaType().equals(Boolean.class))) {
			criteriaQuery.where(
					criteriaBuilder.like(root.get(columnName), WILD_CARD_CHARACTER + text + WILD_CARD_CHARACTER));
		}
		if (root.get(columnName).getJavaType().equals(Boolean.class) && columnType.equals(FILTER_VALUE_UNIQUE)) {
			buildFilterColumnListForBoolean(columnName, text, criteriaBuilder, criteriaQuery, root);
		}
		if (columnType.equals(FILTER_VALUE_UNIQUE)) {
			criteriaQuery.distinct(true);
		} else if (columnType.equals(FILTER_VALUE_ALL)) {
			criteriaQuery.distinct(false);
		}
		TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
		results = typedQuery.setMaxResults(filterValueMaxColumns).getResultList();
		return results;
	}

	private <E, T> void buildFilterColumnListForBoolean(String columnName, String text, CriteriaBuilder criteriaBuilder,
			CriteriaQuery<T> criteriaQuery, Root<E> root) {
		boolean flag = false;
		if (text.equals(STATUS_TRUE_FLAG)) {
			flag = true;
		}
		criteriaQuery.where(criteriaBuilder.equal(root.get(columnName), flag));
	}

	public <E> List<E> filterValueEntities(Class<E> entity, FilterDto filterDto, String languageCode) {
		List<E> values;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = criteriaBuilder.createQuery(entity);
		Root<E> rootEntity = query.from(entity);
		Predicate langCodePredicate = criteriaBuilder.equal(rootEntity.get(LANGCODE_COLUMN_NAME), languageCode);
		Predicate wildCardPredicate = criteriaBuilder.like(rootEntity.get(filterDto.getColumnName()),
				WILD_CARD_CHARACTER + filterDto.getText() + WILD_CARD_CHARACTER);
		Predicate activeStatusPredicate = criteriaBuilder.isTrue(rootEntity.get(STATUS_ATTRIBUTE));
		Predicate inActiveStatusPredicate = criteriaBuilder.isFalse(rootEntity.get(STATUS_ATTRIBUTE));
		if (rootEntity.get(filterDto.getColumnName()).getJavaType().equals(Boolean.class)) {
			if (filterDto.getText().equals(STATUS_TRUE_FLAG)) {
				query.select(rootEntity).where(criteriaBuilder.and(langCodePredicate, activeStatusPredicate))
						.orderBy(criteriaBuilder.asc(rootEntity.get(filterDto.getColumnName())));
			} else {
				query.select(rootEntity).where(criteriaBuilder.and(langCodePredicate, inActiveStatusPredicate))
						.orderBy(criteriaBuilder.asc(rootEntity.get(filterDto.getColumnName())));
			}
		} else {
			query.select(rootEntity).where(criteriaBuilder.and(langCodePredicate, wildCardPredicate))
					.orderBy(criteriaBuilder.asc(rootEntity.get(filterDto.getColumnName())));
		}

		TypedQuery<E> results = entityManager.createQuery(query);
		values = results.setMaxResults(filterValueMaxColumns).getResultList();
		return values;
	}
}

package io.mosip.kernel.masterdata.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
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

import io.mosip.kernel.masterdata.constant.ValidationErrorCode;
import io.mosip.kernel.masterdata.dto.FilterData;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Class that provides generic methods for implementation of filter values
 * search.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @author Urvil Joshi
 * 
 * @since 1.0
 *
 */
@Repository
@Transactional(readOnly = true)
public class MasterDataFilterHelper {

	private static List<Class<?>> classes = null;

	@PostConstruct
	private static void init() {
		classes = new ArrayList<>();
		classes.add(LocalDateTime.class);
		classes.add(LocalDate.class);
		classes.add(LocalTime.class);
		classes.add(Short.class);
		classes.add(Integer.class);
		classes.add(Double.class);
		classes.add(Float.class);
	}

	private static final String LANGCODE_COLUMN_NAME = "langCode";
	private static final String MAP_STATUS_COLUMN_NAME = "mapStatus";
	private static final String FILTER_VALUE_UNIQUE = "unique";
	private static final String FILTER_VALUE_ALL = "all";
	private static final String WILD_CARD_CHARACTER = "%";
	private static final String FILTER_VALUE_EMPTY = "";

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${mosip.kernel.filtervalue.max_columns:20}")
	int filterValueMaxColumns;

	public MasterDataFilterHelper(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public <E, T> List<T> filterValues(Class<E> entity, FilterDto filterDto, FilterValueDto filterValueDto) {
		String columnName = filterDto.getColumnName();
		String columnType = filterDto.getType();
		if (columnName.equals(MAP_STATUS_COLUMN_NAME)
				&& (columnType.equals(FILTER_VALUE_UNIQUE) || columnType.equals(FILTER_VALUE_ALL))) {
			return (List<T>) valuesForMapStatusColumn();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQueryByString = criteriaBuilder.createQuery(String.class);
		Root<E> root = criteriaQueryByString.from(entity);
		Path<Object> path = root.get(filterDto.getColumnName());
		List<T> results;

		CriteriaQuery<T> criteriaQueryByType = criteriaBuilder.createQuery((Class<T>) path.getJavaType());
		Root<E> rootType = criteriaQueryByType.from(entity);

		Predicate langCodePredicate = criteriaBuilder.equal(rootType.get(LANGCODE_COLUMN_NAME),
				filterValueDto.getLanguageCode());
		Predicate caseSensitivePredicate = criteriaBuilder.and(criteriaBuilder
				.like(criteriaBuilder.lower(rootType.get(filterDto.getColumnName())), criteriaBuilder.lower(
						criteriaBuilder.literal(WILD_CARD_CHARACTER + filterDto.getText() + WILD_CARD_CHARACTER))));

		criteriaQueryByType.select(rootType.get(columnName));

		columnTypeValidator(rootType, columnName);

		// check column type is not boolean
		if (!(rootType.get(columnName).getJavaType().equals(Boolean.class)
				&& filterValueDto.getLanguageCode().equals("all"))) {
			criteriaQueryByType.where(criteriaBuilder.and(caseSensitivePredicate));
		} else if (!(rootType.get(columnName).getJavaType().equals(Boolean.class))) {
			criteriaQueryByType.where(criteriaBuilder.and(langCodePredicate, caseSensitivePredicate));
		}
		criteriaQueryByType.orderBy(criteriaBuilder.asc(rootType.get(columnName)));

		// check if column type is boolean then return true/false
		if (rootType.get(columnName).getJavaType().equals(Boolean.class) && (columnType.equals(FILTER_VALUE_UNIQUE)
				|| columnType.equals(FILTER_VALUE_ALL) || columnType.equals(FILTER_VALUE_EMPTY))) {
			return (List<T>) valuesForStatusColumn();
		}

		if (columnType.equals(FILTER_VALUE_UNIQUE) || columnType.equals(FILTER_VALUE_EMPTY)) {
			criteriaQueryByType.distinct(true);
		} else if (columnType.equals(FILTER_VALUE_ALL)) {
			criteriaQueryByType.distinct(false);
		}
		TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQueryByType);
		results = typedQuery.setMaxResults(filterValueMaxColumns).getResultList();
		return results;

	}

	public <E> List<FilterData> filterValuesWithCode(Class<E> entity, FilterDto filterDto,
			FilterValueDto filterValueDto, String fieldCodeColumnName) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		String columnName = filterDto.getColumnName();
		String columnType = filterDto.getType();
		List<FilterData> results;
		CriteriaQuery<FilterData> criteriaQueryByType = criteriaBuilder.createQuery(FilterData.class);
		Root<E> rootType = criteriaQueryByType.from(entity);

		Predicate langCodePredicate = criteriaBuilder.equal(rootType.get(LANGCODE_COLUMN_NAME),
				filterValueDto.getLanguageCode());
		Predicate caseSensitivePredicate = criteriaBuilder.and(criteriaBuilder
				.like(criteriaBuilder.lower(rootType.get(filterDto.getColumnName())), criteriaBuilder.lower(
						criteriaBuilder.literal(WILD_CARD_CHARACTER + filterDto.getText() + WILD_CARD_CHARACTER))));

		criteriaQueryByType.multiselect(rootType.get(fieldCodeColumnName), rootType.get(columnName));

		columnTypeValidator(rootType, columnName);

		if (filterValueDto.getLanguageCode().equals("all") && !(rootType.get(columnName).getJavaType().equals(Boolean.class))) {
			criteriaQueryByType.where(criteriaBuilder.and(caseSensitivePredicate));
		} else if (!(rootType.get(columnName).getJavaType().equals(Boolean.class))) {
			criteriaQueryByType.where(criteriaBuilder.and(langCodePredicate, caseSensitivePredicate));
		}
		criteriaQueryByType.orderBy(criteriaBuilder.asc(rootType.get(columnName)));

		// if column type is Boolean then add value as true or false
		if (rootType.get(columnName).getJavaType().equals(Boolean.class) && (columnType.equals(FILTER_VALUE_UNIQUE)
				|| columnType.equals(FILTER_VALUE_ALL) || columnType.equals(FILTER_VALUE_EMPTY))) {
			return valuesForStatusColumnCode();
		}

		// if column type is other than Boolean
		if (columnType.equals(FILTER_VALUE_UNIQUE) || columnType.equals(FILTER_VALUE_EMPTY)) {
			criteriaQueryByType.distinct(true);
		} else if (columnType.equals(FILTER_VALUE_ALL)) {
			criteriaQueryByType.distinct(false);
		}
		TypedQuery<FilterData> typedQuery = entityManager.createQuery(criteriaQueryByType);
		results = typedQuery.setMaxResults(filterValueMaxColumns).getResultList();
		return results;

	}

	private <E> void columnTypeValidator(Root<E> root, String columnName) {
		if (classes.contains(root.get(columnName).getJavaType())) {
			throw new MasterDataServiceException(ValidationErrorCode.FILTER_COLUMN_NOT_SUPPORTED.getErrorCode(),
					ValidationErrorCode.FILTER_COLUMN_NOT_SUPPORTED.getErrorMessage());

		}
	}

	private List<FilterData> valuesForStatusColumnCode() {
		FilterData trueFilterData = new FilterData("", "true");
		FilterData falseFilterData = new FilterData("", "false");
		List<FilterData> filterDataList = new ArrayList<>();
		filterDataList.add(trueFilterData);
		filterDataList.add(falseFilterData);
		return filterDataList;
	}

	private List<String> valuesForStatusColumn() {
		List<String> filterDataList = new ArrayList<>();
		filterDataList.add("true");
		filterDataList.add("false");
		return filterDataList;
	}

	private List<String> valuesForMapStatusColumn() {
		List<String> filterDataList = new ArrayList<>();
		filterDataList.add("Assigned");
		filterDataList.add("Unassigned");
		return filterDataList;
	}
}

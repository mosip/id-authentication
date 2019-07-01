package io.mosip.kernel.masterdata.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.kernel.masterdata.constant.MasterdataSearchErrorCode;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;

/**
 * Generating dynamic query for masterdata based on the search filters.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Repository
@Transactional(readOnly = true)
public class MasterdataSearchHelper {
	private static final String LANGCODE_COLUMN_NAME = "langCode";
	private static final String ENTITY_IS_NULL = "enitity is null";
	private static final String NO_SPECIAL_CHAR_REGEX = "[^\\w\\s]";

	/**
	 * Field for interface used to interact with the persistence context.
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Constructor for MasterdataSearchHelper having EntityManager
	 * 
	 * @param entityManager
	 *            The entityManager
	 */
	public MasterdataSearchHelper(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Method to search and sort the masterdata.
	 * 
	 * @param entity
	 *            the entity class for which search will be applied
	 * @param searchDto
	 *            which contains the list of filters, sort and pagination
	 * @param optionalFilters
	 *            filters to be considered as 'or' statements
	 * 
	 * @return {@link Page} of entity
	 */
	public <E> Page<E> searchMasterdata(Class<E> entity, SearchDto searchDto, List<SearchFilter> optionalFilters) {
		long rows = 0l;
		List<E> result;
		Objects.requireNonNull(entity, ENTITY_IS_NULL);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> selectQuery = criteriaBuilder.createQuery(entity);
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		// root Query
		Root<E> rootQuery = selectQuery.from(entity);
		// count query
		countQuery.select(criteriaBuilder.count(countQuery.from(entity)));
		// applying filters
		filterQuery(criteriaBuilder, rootQuery, selectQuery, countQuery, searchDto.getFilters(), optionalFilters,
				searchDto.getLanguageCode());

		// applying sorting
		sortQuery(criteriaBuilder, rootQuery, selectQuery, searchDto.getSort());

		sortQuery(criteriaBuilder, rootQuery, selectQuery, searchDto.getSort());

		try {
			// creating executable query from select criteria query
			TypedQuery<E> executableQuery = entityManager.createQuery(selectQuery);
			// creating executable query from count criteria query
			TypedQuery<Long> countExecutableQuery = entityManager.createQuery(countQuery);
			// getting the rows count
			rows = countExecutableQuery.getSingleResult();
			// adding pagination
			paginationQuery(executableQuery, searchDto.getPagination());
			// executing query and returning data
			result = executableQuery.getResultList();
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCode.HIBERNATE_EXCEPTION.getErrorCode(),
					hibernateException.getMessage(), hibernateException);
		} catch (RuntimeException runtimeException) {
			throw new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
					runtimeException.getMessage(), runtimeException);
		}
		return new PageImpl<>(result,
				PageRequest.of(searchDto.getPagination().getPageStart(), searchDto.getPagination().getPageFetch()),
				rows);
	}

	/**
	 * Method to add the filters to the criteria query
	 * 
	 * @param builder
	 *            used to construct criteria queries
	 * @param root
	 *            root type in the from clause,always refers entity
	 * @param selectQuery
	 *            criteria select query
	 * @param countQuery
	 *            criteria count query
	 * @param filters
	 *            list of {@link SearchFilter}
	 * @param langCode
	 *            language code if applicable
	 */
	private <E> void filterQuery(CriteriaBuilder builder, Root<E> root, CriteriaQuery<E> selectQuery,
			CriteriaQuery<Long> countQuery, List<SearchFilter> filters, List<SearchFilter> optionalFilters,
			String langCode) {
		List<Predicate> predicates = new ArrayList<>();
		List<Predicate> optionalPredicates = new ArrayList<>();
		if (filters != null && !filters.isEmpty()) {
			predicates = filters.stream().filter(this::validateFilters).map(i -> buildFilters(builder, root, i))
					.filter(Objects::nonNull).collect(Collectors.toList());
		}

		if (optionalFilters != null && !optionalFilters.isEmpty()) {
			optionalPredicates = optionalFilters.stream().filter(this::validateFilters)
					.map(i -> buildFilters(builder, root, i)).filter(Objects::nonNull).collect(Collectors.toList());
		}
		Predicate langCodePredicate = setLangCode(builder, root, langCode);
		if (langCodePredicate != null) {
			predicates.add(langCodePredicate);
		}
		if (!predicates.isEmpty()) {
			if (!optionalPredicates.isEmpty()) {
				Predicate orPredicate = builder
						.or(optionalPredicates.toArray(new Predicate[optionalPredicates.size()]));
				predicates.add(orPredicate);
			}
			Predicate whereClause = builder.and(predicates.toArray(new Predicate[predicates.size()]));
			selectQuery.where(whereClause);
			countQuery.where(whereClause);
		}
	}

	/**
	 * Method to build {@link Predicate} out the {@link SearchFilter}
	 * 
	 * @param builder
	 *            used to construct criteria queries
	 * @param root
	 *            root type in the from clause,always refers entity
	 * @param filter
	 *            search filter
	 * @return {@link Predicate}
	 */
	private <E> Predicate buildFilters(CriteriaBuilder builder, Root<E> root, SearchFilter filter) {
		String columnName = filter.getColumnName();
		String value = filter.getValue();
		String filterType = filter.getType();
		if (FilterTypeEnum.CONTAINS.name().equalsIgnoreCase(filterType)) {
			String replacedValue = value.replaceAll(NO_SPECIAL_CHAR_REGEX, "").toLowerCase();
			Expression<String> lowerCase = builder.lower(root.get(columnName));
			if (value.startsWith("*") && value.endsWith("*")) {
				return builder.like(lowerCase, "%" + replacedValue + "%");
			} else if (value.startsWith("*")) {
				return builder.like(lowerCase, "%" + replacedValue);
			} else {
				return builder.like(lowerCase, "%" + replacedValue + "%");
			}
		}
		if (FilterTypeEnum.EQUALS.name().equalsIgnoreCase(filterType)) {
			return buildPredicate(builder, root, columnName, value);
		}
		if (FilterTypeEnum.STARTSWITH.name().equalsIgnoreCase(filterType)) {
			String replacedValue = value.replaceAll(NO_SPECIAL_CHAR_REGEX, "").toLowerCase();
			Expression<String> lowerCase = builder.lower(root.get(columnName));
			return builder.like(lowerCase, replacedValue + "%");
		}
		if (FilterTypeEnum.BETWEEN.name().equalsIgnoreCase(filterType)) {
			return setBetweenValue(builder, root, filter);
		}
		return null;
	}

	/**
	 * Method to add sorting statement in criteria query
	 * 
	 * @param builder
	 *            used to construct criteria query
	 * @param root
	 *            root type in the from clause,always refers entity
	 * @param criteriaQuery
	 *            query in which sorting to be added
	 * @param sortFilter
	 *            by the query to be sorted
	 */
	private <E> void sortQuery(CriteriaBuilder builder, Root<E> root, CriteriaQuery<E> criteriaQuery,
			List<SearchSort> sortFilter) {
		if (sortFilter != null && !sortFilter.isEmpty()) {
			List<Order> orders = sortFilter.stream().filter(this::validateSort).map(i -> {
				if (OrderEnum.asc.name().equalsIgnoreCase(i.getSortType()))
					return builder.asc(root.get(i.getSortField()));
				if (OrderEnum.desc.name().equalsIgnoreCase(i.getSortType()))
					return builder.desc(root.get(i.getSortField()));
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toList());
			criteriaQuery.orderBy(orders);
		}
	}

	/**
	 * Method to add pagination in criteria query
	 * 
	 * @param query
	 *            to be added with pagination
	 * @param page
	 *            contains the pagination details
	 */
	public void paginationQuery(Query query, Pagination page) {
		if (page != null) {
			if (page.getPageStart() < 0 && page.getPageFetch() < 1) {
				throw new RequestException(MasterdataSearchErrorCode.INVALID_PAGINATION_VALUE.getErrorCode(),
						String.format(MasterdataSearchErrorCode.INVALID_PAGINATION_VALUE.getErrorMessage(),
								page.getPageStart(), page.getPageFetch()),
						null);
			}
			query.setFirstResult(page.getPageStart());
			query.setMaxResults(page.getPageFetch());
		}
	}

	/**
	 * Method to add the Language Code in the criteria query
	 * 
	 * @param builder
	 *            used to construct the criteria query
	 * @param root
	 *            root type in the from clause,always refers entity
	 * @param langCode
	 *            language code
	 * @return {@link Predicate}
	 */
	private <E> Predicate setLangCode(CriteriaBuilder builder, Root<E> root, String langCode) {
		if (langCode != null && !langCode.isEmpty()) {
			Path<Object> langCodePath = root.get(LANGCODE_COLUMN_NAME);
			if (langCodePath != null) {
				return builder.equal(langCodePath, langCode);
			}
		}
		return null;
	}

	/**
	 * Method to handle type safe between {@link Predicate}
	 * 
	 * @param builder
	 *            use to construct the criteria query
	 * @param root
	 *            type in the from clause,always refers entity
	 * @param filter
	 *            search filter with the between type.
	 * @return {@link Predicate}
	 */
	private <E> Predicate setBetweenValue(CriteriaBuilder builder, Root<E> root, SearchFilter filter) {
		String columnName = filter.getColumnName();
		Path<Object> path = root.get(columnName);
		if (path != null) {
			Class<? extends Object> type = path.getJavaType();
			String fieldType = type.getTypeName();
			String toValue = filter.getToValue();
			String fromValue = filter.getFromValue();
			if (LocalDateTime.class.getName().equals(fieldType)) {
				return builder.between(root.get(columnName), DateUtils.parseToLocalDateTime(fromValue),
						DateUtils.convertUTCToLocalDateTime(toValue));
			}
			if (Long.class.getName().equals(fieldType)) {
				return builder.between(root.get(columnName), Long.parseLong(fromValue), Long.parseLong(toValue));
			}
			if (Integer.class.getName().equals(fieldType)) {
				return builder.between(root.get(columnName), Integer.parseInt(fromValue), Integer.parseInt(toValue));
			}
			if (Float.class.getName().equals(fieldType)) {
				return builder.between(root.get(columnName), Float.parseFloat(fromValue), Float.parseFloat(toValue));
			}
			if (Double.class.getName().equals(fieldType)) {
				return builder.between(root.get(columnName), Double.parseDouble(fromValue),
						Double.parseDouble(toValue));
			}
			if (String.class.getName().equals(fieldType)) {
				return builder.between(root.get(columnName), fromValue, toValue);
			}
		} else {
			throw new RequestException(MasterdataSearchErrorCode.INVALID_COLUMN.getErrorCode(),
					String.format(MasterdataSearchErrorCode.INVALID_COLUMN.getErrorMessage(), filter.getColumnName()),
					null);
		}
		return null;
	}

	/**
	 * Method to cast the data into the column type data type
	 * 
	 * @param root
	 *            type in the from clause,always refers entity
	 * @param column
	 *            name of the column
	 * @param value
	 *            value to be cast based on the column data type
	 * @return the value
	 */
	private <E> Object parseDataType(Root<E> root, String column, String value) {
		Path<Object> path = root.get(column);
		if (path != null) {
			Class<? extends Object> type = path.getJavaType();
			String fieldType = type.getTypeName();
			if (LocalDateTime.class.getName().equals(fieldType)) {
				return DateUtils.parseToLocalDateTime(value);
			}
			if (Long.class.getName().equals(fieldType)) {
				return Long.parseLong(value);
			}
			if (Integer.class.getName().equals(fieldType)) {
				return Integer.parseInt(value);
			}
			if (Float.class.getName().equals(fieldType)) {
				return Float.parseFloat(value);
			}
			if (Double.class.getName().equals(fieldType)) {
				return Double.parseDouble(value);
			}
			if (Boolean.class.getName().equals(fieldType)) {
				return Boolean.valueOf(value);
			}
		}
		return value;
	}

	/**
	 * Method to create the predicate
	 * 
	 * @param builder
	 *            used to construct criteria query
	 * @param root
	 *            type in the from clause,always refers entity
	 * @param column
	 *            name of the column
	 * @param value
	 *            column value
	 * @return {@link Predicate}
	 */
	private <E> Predicate buildPredicate(CriteriaBuilder builder, Root<E> root, String column, String value) {
		Predicate predicate = null;
		Path<Object> path = root.get(column);
		if (path != null) {
			Class<? extends Object> type = path.getJavaType();
			String fieldType = type.getTypeName();
			if (LocalDateTime.class.getName().equals(fieldType)) {
				LocalDateTime start = DateUtils.parseToLocalDateTime(value);
				predicate = builder.between(root.get(column), start, start.plusNanos(1000000l));
			} else {
				predicate = builder.equal(root.get(column), parseDataType(root, column, value));
			}
		}
		return predicate;
	}

	/**
	 * Validate the filter column and values
	 * 
	 * @param filter
	 *            search filter to be validated
	 * @return true if valid false otherwise
	 */
	private boolean validateFilters(SearchFilter filter) {
		if (filter != null) {
			if (filter.getColumnName() != null && !filter.getColumnName().isEmpty()) {
				if (filter.getType() != null && !filter.getType().isEmpty()) {
					if (!FilterTypeEnum.BETWEEN.name().equalsIgnoreCase(filter.getType())) {
						String value = filter.getValue();
						if (value != null && !value.isEmpty()) {
							return true;
						}
					} else {
						String fromValue = filter.getFromValue();
						String toValue = filter.getToValue();
						if (fromValue != null && !fromValue.isEmpty() && toValue != null && !toValue.isEmpty()) {
							return true;
						} else {
							throw new RequestException(MasterdataSearchErrorCode.INVALID_BETWEEN_VALUES.getErrorCode(),
									String.format(MasterdataSearchErrorCode.INVALID_BETWEEN_VALUES.getErrorMessage(),
											filter.getColumnName()));
						}
					}
				} else {
					throw new RequestException(MasterdataSearchErrorCode.FILTER_TYPE_NOT_AVAILABLE.getErrorCode(),
							String.format(MasterdataSearchErrorCode.FILTER_TYPE_NOT_AVAILABLE.getErrorMessage(),
									filter.getColumnName()));
				}
			} else {
				throw new RequestException(MasterdataSearchErrorCode.MISSING_FILTER_COLUMN.getErrorCode(),
						MasterdataSearchErrorCode.MISSING_FILTER_COLUMN.getErrorMessage());
			}
		}
		return true;
	}

	/**
	 * Method to validate the Sort Filter
	 * 
	 * @param sort
	 *            sort filter to be validated
	 * @return true if valid false otherwise
	 */
	private boolean validateSort(SearchSort sort) {
		if (sort != null) {
			String field = sort.getSortField();
			String type = sort.getSortType();
			if (field != null && !field.isEmpty() && type != null && !type.isEmpty()) {
				return true;
			} else {
				throw new RequestException(MasterdataSearchErrorCode.INVALID_SORT_INPUT.getErrorCode(),
						MasterdataSearchErrorCode.INVALID_SORT_INPUT.getErrorMessage());
			}
		}
		return false;
	}
}

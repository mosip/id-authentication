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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Generating dynamic query for masterdata based on the search filters.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Repository
public class MasterdataSearchHelper {

	/**
	 * Instance of {@link EntityManager}
	 */
	@PersistenceContext
	EntityManager entityManager;

	@Transactional(readOnly = true)
	public <T extends BaseEntity> Page<T> searchMasterdata(Class<T> entity, SearchDto searchDto) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> selectQuery = criteriaBuilder.createQuery(entity);
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		// root Query
		Root<T> rootQuery = selectQuery.from(entity);
		// count query
		countQuery.select(criteriaBuilder.count(countQuery.from(entity)));
		// applying filters
		filterQuery(criteriaBuilder, rootQuery, selectQuery, countQuery, searchDto.getFilters(),
				searchDto.getLanguageCode());
		// applying sorting
		sortQuery(criteriaBuilder, rootQuery, selectQuery, searchDto.getSort());
		// creating executable query from select criteria query
		TypedQuery<T> executableQuery = entityManager.createQuery(selectQuery);
		// creating executable query from count criteria query
		TypedQuery<Long> countExecutableQuery = entityManager.createQuery(countQuery);

		System.out.println("Select Query :" + executableQuery.unwrap(org.hibernate.query.Query.class).getQueryString());

		System.out.println(
				"Count Query :" + countExecutableQuery.unwrap(org.hibernate.query.Query.class).getQueryString());
		// getting the rows count
		Long rows = countExecutableQuery.getSingleResult();
		// adding pagination
		paginationQuery(executableQuery, searchDto.getPagination());
		// executing query and returning data
		List<T> result = executableQuery.getResultList();
		return new PageImpl<>(result,
				PageRequest.of(searchDto.getPagination().getPageStart(), searchDto.getPagination().getPageFetch()),
				rows);
	}

	public <T> void filterQuery(CriteriaBuilder builder, Root<T> rootQuery, CriteriaQuery<T> selectQuery,
			CriteriaQuery<Long> countQuery, List<SearchFilter> filters, String langCode) {
		List<Predicate> predicates = new ArrayList<>();
		if (filters != null && !filters.isEmpty()) {
			predicates = filters.parallelStream().map(i -> buildFilters(builder, rootQuery, i)).filter(Objects::nonNull)
					.collect(Collectors.toList());
		}
		Predicate langCodePredicate = setLangCode(builder, rootQuery, langCode);
		if (langCodePredicate != null) {
			predicates.add(langCodePredicate);
		}
		if (!predicates.isEmpty()) {
			Predicate whereClause = builder.and(predicates.toArray(new Predicate[predicates.size()]));
			selectQuery.where(whereClause);
			countQuery.where(whereClause);
		}
	}

	private <T> Predicate buildFilters(CriteriaBuilder builder, Root<T> rootQuery, SearchFilter filter) {
		if ("IN".equalsIgnoreCase(filter.getType())) {
			String value = filter.getValue();
			String replacedValue = value.replaceAll("[^\\w\\s]", "").toLowerCase();
			Expression<String> lowerCase = builder.lower(rootQuery.get(filter.getColumnName()));
			if (value.startsWith("*") && value.endsWith("*")) {
				return builder.like(lowerCase, "%" + replacedValue + "%");
			} else if (value.startsWith("*")) {
				return builder.like(lowerCase, "%" + replacedValue);
			} else if (value.endsWith("*")) {
				return builder.like(lowerCase, replacedValue + "%");
			} else {
				return builder.like(lowerCase, "%" + replacedValue + "%");
			}
		}
		if ("EQUALS".equalsIgnoreCase(filter.getType())) {
			return buildPredicate(builder, rootQuery, filter.getColumnName(), filter.getValue());
		}
		if ("STARTSWITH".equalsIgnoreCase(filter.getType())) {
			String value = filter.getValue();
			String replacedValue = value.replaceAll("[^\\w\\s]", "").toLowerCase();
			Expression<String> lowerCase = builder.lower(rootQuery.get(filter.getColumnName()));
			return builder.like(lowerCase, replacedValue + "%");
		}
		if ("BETWEEN".equalsIgnoreCase(filter.getType())) {
			return setBetweenValue(builder, rootQuery, filter);
		}
		return null;
	}

	public <T> void sortQuery(CriteriaBuilder builder, Root<T> rootQuery, CriteriaQuery<T> criteriaQuery,
			List<SearchSort> sortFilter) {
		if (sortFilter != null && !sortFilter.isEmpty()) {
			List<Order> orders = sortFilter.stream().map(i -> {
				if ("ASC".equalsIgnoreCase(i.getSortType()))
					return builder.asc(rootQuery.get(i.getSortField()));
				if ("DESC".equalsIgnoreCase(i.getSortType()))
					return builder.desc(rootQuery.get(i.getSortField()));
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toList());
			criteriaQuery.orderBy(orders);
		}
	}

	public void paginationQuery(Query query, Pagination page) {
		query.setFirstResult(page.getPageStart());
		query.setMaxResults(page.getPageFetch());
	}

	public <T> Predicate setLangCode(CriteriaBuilder builder, Root<T> rootQuery, String langCode) {
		if (langCode != null && !langCode.isEmpty()) {
			Path<Object> langCodePath = rootQuery.get("langCode");
			if (langCodePath != null) {
				return builder.equal(langCodePath, langCode);
			}
		}
		return null;
	}

	private <T> Predicate setBetweenValue(CriteriaBuilder builder, Root<T> rootQuery, SearchFilter filter) {
		String columnName = filter.getColumnName();
		Path<Object> path = rootQuery.get(columnName);
		if (path != null) {
			Class<? extends Object> type = path.getJavaType();
			String fieldType = type.getTypeName();
			String toValue = filter.getToValue();
			String fromValue = filter.getFromValue();
			if (LocalDateTime.class.getName().equals(fieldType)) {
				return builder.between(rootQuery.get(columnName), DateUtils.parseToLocalDateTime(fromValue),
						DateUtils.convertUTCToLocalDateTime(toValue));
			}
			if (Long.class.getName().equals(fieldType)) {
				return builder.between(rootQuery.get(columnName), Long.parseLong(fromValue), Long.parseLong(toValue));
			}
			if (Integer.class.getName().equals(fieldType)) {
				return builder.between(rootQuery.get(columnName), Integer.parseInt(fromValue),
						Integer.parseInt(toValue));
			}
			if (Float.class.getName().equals(fieldType)) {
				return builder.between(rootQuery.get(columnName), Float.parseFloat(fromValue),
						Float.parseFloat(toValue));
			}
			if (Double.class.getName().equals(fieldType)) {
				return builder.between(rootQuery.get(columnName), Double.parseDouble(fromValue),
						Double.parseDouble(toValue));
			}
			if (String.class.getName().equals(fieldType)) {
				return builder.between(rootQuery.get(columnName), fromValue, toValue);
			}
		} else {
			throw new MasterDataServiceException("", "column name is invalid:" + filter.getColumnName());
		}
		return null;
	}

	private <T> Object parseDataType(Root<T> root, String column, String value) {
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
		}
		return value;
	}

	private <T> Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String column, String value) {
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

}

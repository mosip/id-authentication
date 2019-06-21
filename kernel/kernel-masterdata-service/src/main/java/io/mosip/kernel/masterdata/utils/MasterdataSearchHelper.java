package io.mosip.kernel.masterdata.utils;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.entity.BaseEntity;

@Repository
public class MasterdataSearchHelper {

	@PersistenceContext
	EntityManager entityManager;

	@Transactional(readOnly = true)
	public <T extends BaseEntity> List<T> searchMasterdata(Class<T> entity, SearchDto searchDto) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entity);
		// root Query
		Root<T> rootQuery = criteriaQuery.from(entity);
		// appling filters
		filterQuery(criteriaBuilder, rootQuery, criteriaQuery, searchDto.getFilters());
		// appling sorting
		sortQuery(criteriaBuilder, rootQuery, criteriaQuery, searchDto.getSort());
		// creating executable query from criteria query
		TypedQuery<T> executableQuery = entityManager.createQuery(criteriaQuery);
		// adding pagination
		paginationQuery(executableQuery, searchDto.getPagination());
		// executing query and returning data
		return executableQuery.getResultList();
	}

	public <T extends BaseEntity> CriteriaQuery<T> filterQuery(CriteriaBuilder builder, Root<T> rootQuery,
			CriteriaQuery<T> criteriaQuery, List<SearchFilter> filters) {

		List<Predicate> predicates = filters.parallelStream().map(i -> {
			if ("IN".equalsIgnoreCase(i.getType())) {
				return rootQuery.get(i.getColumnName()).in(i.getValue());
			}
			if ("EQUALS".equalsIgnoreCase(i.getType())) {
				return builder.equal(rootQuery.get(i.getColumnName()), i.getValue());
			}
			if ("STARTSWITH".equalsIgnoreCase(i.getType())) {
				String value = i.getValue();
				String replacedValue = value.replaceAll("[^\\w\\s]", "").toLowerCase();
				Expression<String> lowerCase = builder.lower(rootQuery.get(i.getColumnName()));
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
			if ("BETWEEN".equalsIgnoreCase(i.getType())) {
				return builder.between(rootQuery.get(i.getColumnName()),
						DateUtils.convertUTCToLocalDateTime(i.getFromValue()),
						DateUtils.convertUTCToLocalDateTime(i.getToValue()));
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		if (!predicates.isEmpty()) {
			Predicate[] predicateArray = new Predicate[predicates.size()];
			criteriaQuery.where(builder.and(predicates.toArray(predicateArray)));
		}
		return criteriaQuery;
	}

	public <T extends BaseEntity> CriteriaQuery<T> sortQuery(CriteriaBuilder builder, Root<T> rootQuery,
			CriteriaQuery<T> criteriaQuery, List<SearchSort> sortFilter) {
		List<Order> orders = sortFilter.stream().map(i -> {
			if ("ASC".equalsIgnoreCase(i.getSortType()))
				return builder.asc(rootQuery.get(i.getSortField()));
			if ("DESC".equalsIgnoreCase(i.getSortType()))
				return builder.desc(rootQuery.get(i.getSortField()));
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return criteriaQuery.orderBy(orders);

	}

	public Query paginationQuery(Query query, Pagination page) {
		query.setFirstResult(page.getPageStart());
		query.setMaxResults(page.getPageFetch());
		return query;
	}

}

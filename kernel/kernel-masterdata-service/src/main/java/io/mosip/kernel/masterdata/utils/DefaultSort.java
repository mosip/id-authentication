package io.mosip.kernel.masterdata.utils;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchSort;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

@Aspect
public class DefaultSort {

	@Before("searchWithinPointCut() && searchArgsPointCut()")
	public void defalultSortField(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof SearchDto) {
				SearchDto dto = (SearchDto) arg;
				if (dto.getSort().isEmpty()) {
					SearchSort sort1 = new SearchSort("updatedDateTime", "desc");
					SearchSort sort2 = new SearchSort("createdDateTime", "desc");
					dto.setSort(Arrays.asList(sort1, sort2));
				}
				if (dto.getPagination() == null) {
					Pagination page = new Pagination(0, 10);
					dto.setPagination(page);

				}
			}
		}

	}

	@Pointcut("args(io.mosip.kernel.masterdata.dto.request.SearchDto)")
	public void searchArgsPointCut() {
	}

	@Pointcut("within(io.mosip.kernel.masterdata.service.impl.*)")
	public void searchWithinPointCut() {
	}
}

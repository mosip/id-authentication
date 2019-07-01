package io.mosip.kernel.masterdata.test.utils;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MasterdataSearchHelperTest {

	@Autowired
	private MasterdataSearchHelper searchHelper;

	private SearchFilter noColumnFilter;
	private SearchFilter noBetweenValueFilter;
	private SearchFilter noValuefilter;
	private SearchFilter filter;
	private SearchFilter betweenfilter;
	private SearchSort noColumnSort;
	private SearchSort sort;
	private Pagination page;
	private Pagination invalidPage;
	private SearchFilter startWithFilter;
	private SearchFilter wildCardFilter1;
	private SearchFilter wildCardFilter2;
	private SearchFilter noFilterType;
	private SearchFilter searchwithDateFilter;

	@Before
	public void setup() {
		noColumnFilter = new SearchFilter();
		noColumnFilter.setType("equals");
		noColumnFilter.setValue("REG");

		noBetweenValueFilter = new SearchFilter();
		noBetweenValueFilter.setColumnName("createdDateTime");
		noBetweenValueFilter.setType("between");

		noValuefilter = new SearchFilter();
		noValuefilter.setColumnName("name");
		noValuefilter.setType("equals");

		betweenfilter = new SearchFilter();
		betweenfilter.setColumnName("createdDateTime");
		betweenfilter.setType("between");
		betweenfilter.setFromValue("2019-01-01T01:01:01.000Z");
		betweenfilter.setToValue("2019-01-07T01:01:01.000Z");

		filter = new SearchFilter();
		filter.setColumnName("name");
		filter.setType("contains");
		filter.setValue("*mosip*");

		startWithFilter = new SearchFilter();
		startWithFilter.setColumnName("name");
		startWithFilter.setType("startswith");
		startWithFilter.setValue("mosip*");

		wildCardFilter1 = new SearchFilter();
		wildCardFilter1.setColumnName("name");
		wildCardFilter1.setType("contains");
		wildCardFilter1.setValue("mosip");

		wildCardFilter2 = new SearchFilter();
		wildCardFilter2.setColumnName("name");
		wildCardFilter2.setType("contains");
		wildCardFilter2.setValue("mosip*");

		noColumnSort = new SearchSort();
		noColumnSort.setSortType("desc");

		sort = new SearchSort();
		sort.setSortField("updatedDateTime");
		sort.setSortType("asc");

		page = new Pagination();
		page.setPageStart(1);
		page.setPageFetch(100);

		invalidPage = new Pagination();
		invalidPage.setPageFetch(1);
		invalidPage.setPageStart(0);

		noFilterType = new SearchFilter();
		noFilterType.setColumnName("name");

		searchwithDateFilter = new SearchFilter();
		searchwithDateFilter.setType("equals");
		searchwithDateFilter.setValue("2019-01-01T01:01:01.000Z");
		searchwithDateFilter.setColumnName("createdDateTime");
	}

	@Test
	public void searchMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(filter), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, null);
	}

	@Test
	public void searchWithOptionalFilterMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(filter), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchConstainsFilter1Masterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(wildCardFilter1), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchConstainsFilter2Masterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(wildCardFilter2), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchStartsWithFilterMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(wildCardFilter2), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchSortDescFilterMasterdata() {
		sort.setSortType("desc");
		SearchDto searchDto = new SearchDto(Arrays.asList(filter), Arrays.asList(sort), page, "eng");

		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchInvalidPaginationMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(filter), Arrays.asList(sort), invalidPage, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test(expected = RequestException.class)
	public void searchInvalidBetweenFilterMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(noBetweenValueFilter), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test(expected = RequestException.class)
	public void searchNoColumnFilterMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(noColumnFilter), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchNoValueFilterMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(noValuefilter), Arrays.asList(sort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test(expected = RequestException.class)
	public void searchNoSortColumnMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(filter), Arrays.asList(noColumnSort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test(expected = RequestException.class)
	public void searchNoColumnMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(filter), Arrays.asList(noColumnSort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test(expected = RequestException.class)
	public void searchNoFilterTypeMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(noFilterType), Arrays.asList(noColumnSort), page, "eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, Arrays.asList(betweenfilter));
	}

	@Test
	public void searchWithDateMasterdata() {
		SearchDto searchDto = new SearchDto(Arrays.asList(searchwithDateFilter), Arrays.asList(sort), page,
				"eng");
		searchHelper.searchMasterdata(RegistrationCenter.class, searchDto, null);
	}


}

package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.utils.SortUtils;

@RunWith(JUnit4.class)
public class SortUtilsTest {

	private SortUtils sortUtils;
	private List<TestPojo> pojos;

	@Before
	public void setup() {
		sortUtils = new SortUtils();
		pojos = new ArrayList<>();
		TestPojo pojo1 = new TestPojo("mosip", 1, 45000.00);
		pojo1.setActive(true);
		pojo1.setJoinDate(LocalDate.parse("2007-01-26"));
		pojo1.setLastUpdated(LocalDateTime.parse("2007-12-03T10:15:30"));
		pojo1.setSalary(24000f);
		pojo1.setLevel((short) 5);
		pojo1.setMobileNo(987654321);
		TestPojo pojo2 = new TestPojo("mosip", 2, 35000.00);
		pojo2.setActive(false);
		pojo2.setJoinDate(LocalDate.parse("2008-01-12"));
		pojo2.setLastUpdated(LocalDateTime.parse("2010-12-03T10:15:30"));
		pojo2.setSalary(12000f);
		pojo2.setLevel((short) 3);
		pojo2.setMobileNo(777654321);
		pojos.add(pojo1);
		pojos.add(pojo2);
	}

	@Test
	public void successSortTest() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("id", "asc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(1, list.get(0).getId());
	}

	@Test
	public void successSortDescTest() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("amount", "asc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(2, list.get(0).getId());
	}

	@Test(expected = RequestException.class)
	public void invalidSortFieldTest() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("abcd", "asc"));
		sortUtils.sort(pojos, sort);
	}

	@Test
	public void multiFieldSortTest() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("id", "asc"));
		sort.add(new SearchSort("amount", "asc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(1, list.get(0).getId());
	}

	@Test(expected = RequestException.class)
	public void invalidSortTypeTest() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("amount", "abcd"));
		sortUtils.sort(pojos, sort);
	}

	@Test
	public void descSortTypeTest() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("id", "desc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(2, list.get(0).getId());
	}
	
	@Test
	public void sortByLocalDateDataType() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("joinDate", "desc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(2, list.get(0).getId());
	}
	
	@Test
	public void sortByLocalDateTimeDateType() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("lastUpdated", "desc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(2, list.get(0).getId());
	}
	
	@Test
	public void sortByShortDateType() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("level", "desc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(1, list.get(0).getId());
	}
	
	@Test
	public void sortByBooleanType() {
		List<SearchSort> sort = new ArrayList<>();
		sort.add(new SearchSort("active", "desc"));
		List<TestPojo> list = sortUtils.sort(pojos, sort);
		assertEquals(1, list.get(0).getId());
	}
}

package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.utils.PageUtils;

@RunWith(JUnit4.class)
public class PageUtilsTest {

	private PageUtils pageUtils;
	private List<TestPojo> pojos;

	@Before
	public void setup() {
		pageUtils = new PageUtils();
		pageUtils = new PageUtils();
		pojos = new ArrayList<>();
		pojos.add(new TestPojo("mosip1", 1, 25000));
		pojos.add(new TestPojo("mosip2", 2, 54000));
		pojos.add(new TestPojo("mosip3", 3, 14000));
		pojos.add(new TestPojo("mosip", 4, 44000));
		pojos.add(new TestPojo("mosip", 5, 34000));
		pojos.add(new TestPojo("mosip", 6, 51000));
	}

	@Test
	public void pageResponseSuccess() {
		Page<String> page = new PageImpl<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
				PageRequest.of(0, 10), 31);
		PageResponseDto<Object> response = PageUtils.pageResponse(page);
		assertEquals(1l, response.getFromRecord());
		assertEquals(10l, response.getToRecord());
		assertEquals(31l, response.getTotalRecord());
	}

	@Test
	public void pageResponseFailure() {
		PageResponseDto<Object> response = PageUtils.pageResponse(null);
		assertNotNull(response);
	}

	@Test
	public void getPageSuccess() {
		Pagination pagination = new Pagination(0, 2);
		List<TestPojo> list = pageUtils.getPage(pojos, pagination);
		assertEquals(list.get(0).getId(), 1);
		assertEquals(list.get(1).getId(), 2);
		assertEquals(pagination.getPageFetch(), list.size());

	}

	@Test
	public void getPageSuccess2() {
		Pagination pagination = new Pagination(0, 7);
		List<TestPojo> list = pageUtils.getPage(pojos, pagination);
		assertEquals(6, list.size());

	}

	@Test
	public void getPageNull() {
		Pagination pagination = new Pagination(0, 7);
		List<TestPojo> list = pageUtils.getPage(null, pagination);
		assertEquals(0, list.size());

	}

	@Test
	public void sortPageSuccess() {
		PageResponseDto<TestPojo> page = pageUtils.sortPage(pojos, Arrays.asList(new SearchSort("id", "asc")),
				new Pagination(0, 10));
		assertEquals(1, page.getFromRecord());
		assertEquals(6, page.getToRecord());
		assertEquals(6, page.getTotalRecord());
	}

	@Test(expected = RequestException.class)
	public void sortPagePageValueNull() {
		pageUtils.sortPage(pojos, Arrays.asList(new SearchSort("id", "asc")), null);

	}

	@Test(expected = RequestException.class)
	public void invalidPageValue() {
		pageUtils.sortPage(pojos, Arrays.asList(new SearchSort("id", "asc")), new Pagination(-1, 0));

	}

}

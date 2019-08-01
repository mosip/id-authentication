package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.utils.PageUtils;

@RunWith(JUnit4.class)
public class PageUtilsTest {
	
	private PageUtils pageUtils;
	
	@Before
	public void setup() {
		pageUtils=new PageUtils();
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
}

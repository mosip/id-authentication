package io.mosip.kernel.masterdata.utils;

import org.springframework.data.domain.Page;

import io.mosip.kernel.masterdata.dto.response.PageResponseDto;

/**
 * Utility class to calculate the page details
 * 
 * @author Abhishek Kumar
 *
 */
public class PageUtils {

	/**
	 * Method to create page metadata
	 * 
	 * @param page
	 *            request to be
	 * @return {@link PageResponseDto}
	 */
	public static <T> PageResponseDto<T> page(Page<T> page) {
		PageResponseDto<T> pageResponse = null;
		if (page != null) {
			long totalItem = page.getTotalElements();
			int pageNumber = page.getNumber()+1;
			int pageSize = page.getSize();
			int start = (pageNumber * pageSize) - (pageSize - 1);
			pageResponse = new PageResponseDto<>();
			pageResponse.setFromRecord(start);
			pageResponse.setToRecord(Math.min(start + pageSize - 1l, totalItem));
			pageResponse.setTotalRecord(totalItem);
		}
		return pageResponse;
	}
}

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

	private PageUtils() {
		
	}
	
	/**
	 * Method to create page metadata
	 * 
	 * @param <D>
	 * 
	 * @param page
	 *            request to be
	 * @return {@link PageResponseDto}
	 */
	public static <T, D> PageResponseDto<D> pageResponse(Page<T> page) {
		PageResponseDto<D> pageResponse = null;
		if (page != null) {
			long totalItem = page.getTotalElements();
			int pageSize = page.getSize();
			int start = (page.getNumber() * pageSize) + 1;
			pageResponse = new PageResponseDto<>();
			pageResponse.setFromRecord(start);
			pageResponse.setToRecord((long) (start-1) + page.getNumberOfElements());
			pageResponse.setTotalRecord(totalItem);
		}
		return pageResponse;
	}
}

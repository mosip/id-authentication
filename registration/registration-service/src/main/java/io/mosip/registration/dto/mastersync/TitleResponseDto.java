package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * Dto class for fetching response from master data
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public class TitleResponseDto {

	private List<TitleDto> title;

	/**
	 * @return the title
	 */
	public List<TitleDto> getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(List<TitleDto> title) {
		this.title = title;
	}
	

}

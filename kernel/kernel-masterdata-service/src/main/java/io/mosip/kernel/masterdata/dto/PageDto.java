package io.mosip.kernel.masterdata.dto;

import java.util.List;

import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {

	private int pageNo;
	private int pageSize;
	private Sort sort;
	private long totalItems;
	private int totalPages;
	private List<T> data;
}

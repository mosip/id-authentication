package io.mosip.kernel.masterdata.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
	private long fromRecord;
	private long toRecord;
	private long totalRecord;
	private List<T> data;
}

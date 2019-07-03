package io.mosip.kernel.masterdata.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterResponseDto {
	private List<ColumnValue> filters;
}

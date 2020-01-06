package io.mosip.kernel.masterdata.dto.request;

import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.mosip.kernel.masterdata.validator.FilterColumn;
import io.mosip.kernel.masterdata.validator.FilterColumnEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterDto {

	@NotBlank
	private String columnName;

	@NotNull
	@FilterColumn(columns = { FilterColumnEnum.ALL, FilterColumnEnum.UNIQUE, FilterColumnEnum.EMPTY })
	private String type = FilterColumnEnum.UNIQUE.toString();

	private String text;
	
	@Builder
    private FilterDto(String columnName, String type, String text) {
        this.columnName = columnName;
        this.type = Optional.ofNullable(type).orElse(this.type);
        this.text = text;
    }
}

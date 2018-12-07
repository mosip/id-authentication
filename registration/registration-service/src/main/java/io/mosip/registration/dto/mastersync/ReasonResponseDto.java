package io.mosip.registration.dto.mastersync;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonResponseDto {
  
	private List<ReasonCategoryDto> reasonCategories;
}

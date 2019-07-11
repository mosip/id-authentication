package io.mosip.admin.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataCardResponseDto {

	private List<MasterDataCardDto> masterdata;
}

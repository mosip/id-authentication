package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha Sinha
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedWordListRequestDto {

	private List<String> blacklistedwords;

}

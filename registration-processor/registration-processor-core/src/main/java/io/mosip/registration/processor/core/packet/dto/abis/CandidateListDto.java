package io.mosip.registration.processor.core.packet.dto.abis;

import java.util.Arrays;
import lombok.Data;

/**
 * The Class CandidateListDto.
 * @author M1048860 Kiran Raj
 */
@Data 
public class CandidateListDto {
	
	/** The count. */
	private String count;
	
	/** The candidates. */
	private CandidatesDto[] candidates;
	
}

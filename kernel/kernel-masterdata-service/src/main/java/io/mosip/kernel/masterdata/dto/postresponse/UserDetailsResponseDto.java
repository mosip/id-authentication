package io.mosip.kernel.masterdata.dto.postresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.UserDetailsDto;
import lombok.Data;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
public class UserDetailsResponseDto {

	private List<UserDetailsDto> userResponseDto;

}

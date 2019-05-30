package io.mosip.registration.processor.core.packet.dto.masterdata;

import lombok.Data;
import java.util.List;

@Data
public class UserDetailsResponseDto {

    private List<UserDetailsDto> userResponseDto;
}

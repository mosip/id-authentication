package io.mosip.registration.processor.core.packet.dto.masterdata;

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.ServerError;
import lombok.Data;

@Data
public class UserResponseDto {
    private String id;
    private String version;
    private String responsetime ;
    private Object metadata;
    private UserDetailsResponseDto response;
    private List<ServerError> errors ;
}

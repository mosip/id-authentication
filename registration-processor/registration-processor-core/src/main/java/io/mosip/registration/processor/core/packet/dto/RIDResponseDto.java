package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;
@Data
public class RIDResponseDto {
    private String id;
    private String version;
    private String responsetime ;
    private Object metadata;
    private RidDto response;
    private List<ServerError> errors ;
}

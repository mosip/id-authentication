package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class ServerError {

    private String errorCode;
    private String message;
}

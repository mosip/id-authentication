package io.mosip.registration.processor.printing.api.dto;

import io.mosip.registration.processor.core.constant.IdType;
import lombok.Data;

@Data
public class RequestDTO {

    private IdType idtype;

    private String idValue;
}

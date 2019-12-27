package io.mosip.resident.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResidentVidResponseDto extends BaseResponseDTO {

    private String vid;
    private String status;
    private String message;
}

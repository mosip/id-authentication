package io.mosip.resident.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class VidResponseDto implements Serializable {

    private String vid;
    private String message;
}

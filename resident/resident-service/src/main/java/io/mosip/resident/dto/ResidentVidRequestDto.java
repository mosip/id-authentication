package io.mosip.resident.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class ResidentVidRequestDto extends BaseRequestDTO {

    private VidRequestDto request;
}

package io.mosip.resident.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class VidRequestDto implements Serializable {

    private String transactionID;
    private String individualId;
    private String individualIdType;
    private String otp;
    private String vidType;

}

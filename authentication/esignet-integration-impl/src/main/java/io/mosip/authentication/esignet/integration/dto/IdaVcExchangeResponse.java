package io.mosip.authentication.esignet.integration.dto;

import lombok.Data;

@Data
public class IdaVcExchangeResponse<T> {

    private T verifiableCredential;
}

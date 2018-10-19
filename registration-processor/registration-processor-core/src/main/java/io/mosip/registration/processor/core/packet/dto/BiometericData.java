package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class BiometericData
{
    private FingerprintData fingerprintData;
    private IrisData irisData;
}

package org.mosip.registration.processor.dto.json.metadata;

import lombok.Data;

@Data
public class BiometericData
{
    private FingerprintData fingerprintData;
    private IrisData irisData;
}

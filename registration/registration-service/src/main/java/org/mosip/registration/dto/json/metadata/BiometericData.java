package org.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class BiometericData
{
    private FingerprintData fingerprintData;
    private IrisData irisData;
}

package io.mosip.kernel.idrepo.provider.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import io.mosip.kernel.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

public class FingerprintProviderTest {

	FingerprintProvider fp = new FingerprintProvider();

	@Test
	public void testConvertFIRtoFMR() {
		BIR rFinger = new BIR.BIRBuilder().withBdb("3".getBytes())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
						.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList(SingleAnySubtypeType.RIGHT.value(),
								SingleAnySubtypeType.INDEX_FINGER.value()))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
						.build())
				.build();
		assertEquals(fp.convertFIRtoFMR(Collections.singletonList(rFinger.toBIRType(rFinger))),
				Collections.singletonList(rFinger.toBIRType(rFinger)));
	}
}

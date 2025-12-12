package io.mosip.authentication.common.service.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

public class BioInfoTest {

    @Test
    public void testGetBir() {

        BioInfo bioInfo = new BioInfo(
                "FINGER",
                SingleType.FINGER,
                new String[]{"RightIndex", "LeftThumb"}
        );

        byte[] sampleBdb = new byte[]{10, 20, 30};

        BIR bir = BioInfo.getBir(sampleBdb, bioInfo);

        assertNotNull(bir);
        assertNotNull(bir.getBdb());
        assertNotNull(bir.getBdbInfo());
        assertArrayEquals(sampleBdb, bir.getBdb());
        BDBInfo bdbInfo = bir.getBdbInfo();
        List<SingleType> types = bdbInfo.getType();
        assertEquals(1, types.size());
        assertEquals(SingleType.FINGER, types.get(0));

        assertEquals(2, bdbInfo.getSubtype().size());
        assertEquals("RightIndex", bdbInfo.getSubtype().get(0));
        assertEquals("LeftThumb", bdbInfo.getSubtype().get(1));

        assertEquals(ProcessedLevelType.RAW, bdbInfo.getLevel());

        assertEquals(PurposeType.VERIFY, bdbInfo.getPurpose());

        RegistryIDType registry = bdbInfo.getFormat();
        assertNotNull(registry);
        assertEquals("257", registry.getOrganization());
        assertEquals("FINGER", registry.getType());
    }
}


package io.mosip.authentication.common.service.util;

import java.util.Arrays;
import java.util.Collections;

import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class BioInfo {
	private static final String BDB_PRPOCESSED_LEVEL = "Raw";

	/** The type. */
	private String type;

	/** The single type. */
	private SingleType singleType;

	/** The sub types. */
	private String[] subTypes;


	public static BIR getBir(byte[] bdb, BioInfo type) {
		BIRBuilder birBuilder = new BIRBuilder();
		RegistryIDType format = new RegistryIDType();
		format.setOrganization(String.valueOf(CbeffConstant.FORMAT_OWNER));
		format.setType(type.getType());
		BDBInfo bdbInfo = new BDBInfo.BDBInfoBuilder().withType(Collections.singletonList(type.getSingleType()))
				.withSubtype(Arrays.asList(type.getSubTypes()))
				.withLevel(ProcessedLevelType.fromValue(BDB_PRPOCESSED_LEVEL)).withFormat(format)
				.withPurpose(PurposeType.VERIFY).build();
		birBuilder.withBdb(bdb);
		birBuilder.withBdbInfo(bdbInfo);
		return birBuilder.build();
	}
}

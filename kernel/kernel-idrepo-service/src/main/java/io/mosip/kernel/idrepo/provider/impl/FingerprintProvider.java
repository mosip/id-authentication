package io.mosip.kernel.idrepo.provider.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.kernel.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.idrepo.spi.MosipFingerprintProvider;

/**
 * The Class FingerprintProvider.
 *
 * @author Manoj SP
 */
@Component
public class FingerprintProvider implements MosipFingerprintProvider<BIRType, BIR> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.idrepo.spi.MosipFingerprintProvider#convertFIRtoFMR(java.util.List)
	 */
	@Override
	public List<BIR> convertFIRtoFMR(List<BIRType> listOfBIR) {
		return listOfBIR.parallelStream()
				.filter(bir -> bir.getBDBInfo().getFormatType().equals(7l) 
						&& bir.getBDBInfo().getFormatOwner().equals(257l))
				.map(bir -> new BIR.BIRBuilder()
				.withBdb(convertToFMR(bir.getBDB()))
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder()
						.withFormatOwner(257l)
						.withFormatType(2l)
						.withQuality(bir.getBDBInfo().getQuality())
						.withType(bir.getBDBInfo().getType())
						.withSubtype(bir.getBDBInfo().getSubtype())
						.withPurpose(PurposeType.IDENTIFY)
						.withLevel(ProcessedLevelType.PROCESSED)
						.withCreationDate(new Date())
						.build())
				.build())
				.collect(Collectors.toList());
	}

	/**
	 * Convert to FMR.
	 *
	 * @param bdb the bdb
	 * @return the byte[]
	 */
	private byte[] convertToFMR(byte[] bdb) {
		return bdb;
	}

}

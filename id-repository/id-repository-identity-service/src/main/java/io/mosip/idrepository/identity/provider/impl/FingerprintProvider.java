package io.mosip.idrepository.identity.provider.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.spi.MosipFingerprintProvider;
import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class FingerprintProvider.
 *
 * @author Manoj SP
 */
@Component
public class FingerprintProvider implements MosipFingerprintProvider<BIR, BIR> {

	@Autowired
	private BioApiImpl bioApiImpl;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idrepo.spi.MosipFingerprintProvider#convertFIRtoFMR(java
	 * .util.List)
	 */
	@Override
	public List<BIR> convertFIRtoFMR(List<BIR> listOfBIR) {
		Map<String, LocalDateTime> latestcreationDate = filterTimestamp(listOfBIR);
		 return listOfBIR.parallelStream()
				.filter(bir -> Objects.nonNull(bir.getBdbInfo()) && bir.getBdbInfo().getFormatType().equals(7l)
						&& bir.getBdbInfo().getFormatOwner().equals(257l)
						&& Objects.nonNull(latestcreationDate.get(bir.getBdbInfo().getSubtype().toString()))
						&& DateUtils.isSameInstant(latestcreationDate.get(bir.getBdbInfo().getSubtype().toString()),
								bir.getBdbInfo().getCreationDate()))
				.map(bir -> new BIR.BIRBuilder().withBdb(convertToFMR(bir))
						.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
						.withBdbInfo(Optional.ofNullable(bir.getBdbInfo())
								.map(bdbInfo -> new BDBInfo.BDBInfoBuilder().withFormatOwner(257l).withFormatType(2l)
										.withQuality(bdbInfo.getQuality()).withType(bdbInfo.getType())
										.withSubtype(bdbInfo.getSubtype()).withPurpose(PurposeType.IDENTIFY)
										.withLevel(ProcessedLevelType.PROCESSED).withCreationDate(LocalDateTime.now())
										.build())
								.orElseGet(() -> null))
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * Filter timestamp.
	 *
	 * @param listOfBIR the list of BIR
	 * @return the map
	 */
	private Map<String, LocalDateTime> filterTimestamp(List<BIR> listOfBIR) {
		Map<String, LocalDateTime> latestcreationDate = new HashMap<>();
		listOfBIR.stream()
				.filter(bir -> Objects.nonNull(bir.getBdbInfo()) && bir.getBdbInfo().getFormatType().equals(7l)
						&& bir.getBdbInfo().getFormatOwner().equals(257l))
				.forEach(bir -> Optional.ofNullable(bir.getBdbInfo()).ifPresent(bdbInfo -> {
					if (latestcreationDate.containsKey(bdbInfo.getSubtype().toString())) {
						latestcreationDate.compute(bdbInfo.getSubtype().toString(),
								(key, date) -> DateUtils.after(bdbInfo.getCreationDate(), date)
										? bdbInfo.getCreationDate()
										: date);
					} else {
						latestcreationDate.put(bdbInfo.getSubtype().toString(), bdbInfo.getCreationDate());
					}
				}));
		return latestcreationDate;
	}

	/**
	 * Convert to FMR.
	 *
	 * @param bdb the bdb
	 * @return the byte[]
	 */
	private byte[] convertToFMR(BIR bir) {
		KeyValuePair[] flags=null;
		BIR birType = bioApiImpl.extractTemplate(bir, flags);
		return birType.getBdb();
	}

}

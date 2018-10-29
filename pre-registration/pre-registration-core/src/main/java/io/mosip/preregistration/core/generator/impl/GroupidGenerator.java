package io.mosip.preregistration.core.generator.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idgenerator.MosipPridGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;
import io.mosip.preregistration.core.cache.GroupidCacheManager;
import io.mosip.preregistration.core.constant.GroupidGeneratorConstants;
import io.mosip.preregistration.core.constant.GroupidGeneratorErrorCodes;
import io.mosip.preregistration.core.entity.Groupid;
import io.mosip.preregistration.core.exceptions.GroupidGenerationException;
import io.mosip.preregistration.core.generator.MosipGroupIdGenerator;
import io.mosip.preregistration.core.repository.GroupidRepository;

/**
 * GroupidGenerator to generate Groupid This class will return a T digit Groupid
 * after the validation from MosipIdFilter
 * 
 * @author M1037717
 * @since 1.0.0
 *
 */
@Component
public class GroupidGenerator implements MosipGroupIdGenerator<String> {
	@Value("${groupId.length}")
	private int groupidLength;
	@Autowired
	private GroupidRepository groupidRepository;
	@Autowired
	private GroupidCacheManager groupidCacheManager;

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	private int generatedIdLength;
	private long lowerBound;
	private long upperBound;

	@PostConstruct
	public void pridGeneratorPostConstruct() {
		generatedIdLength = groupidLength - 1;
		lowerBound = Long.parseLong(GroupidGeneratorConstants.TWO + StringUtils.repeat(GroupidGeneratorConstants.ZERO, generatedIdLength - 1));
		upperBound = Long.parseLong(StringUtils.repeat(GroupidGeneratorConstants.NINE, generatedIdLength));
	}


	@Override
	public String generateGroupId() {
		Groupid groupid = new Groupid();
		boolean unique = false;
		String generatedGroupid = null;
		while (!unique) {
			generatedGroupid = this.generateGroupid();
			if (groupidCacheManager.contains(generatedGroupid)) {
				unique = false;
			} else {
				unique = true;
			}
		}
		long currentTimestamp = System.currentTimeMillis();
		groupid.setId(generatedGroupid);
		groupid.setCreatedAt(currentTimestamp);
		try {
			groupidRepository.save(groupid);
			groupidCacheManager.add(groupid.getId());
			return generatedGroupid;
		} catch (Exception e) {
			throw new GroupidGenerationException(GroupidGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorCode(),
					GroupidGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorMessage());
		}
	}

	private String generateGroupid() {
		String generatedGroupid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		while (!IdFilterUtils.isValidId(generatedGroupid)) {
			generatedGroupid = generateGroupid();
		}
		return generatedGroupid;
	}

	/**
	 * Generates a id and then generate checksum
	 * 
	 * @param generatedIdLength
	 *            The length of id to generate
	 * @param lowerBound
	 *            The lowerbound for generating id
	 * @param upperBound
	 *            The upperbound for generating id
	 * @return the GroupId with checksum
	 */
	private String generateRandomId(int generatedIdLength, long lowerBound, long upperBound) {
		Long generatedID = RANDOM_DATA_GENERATOR.nextSecureLong(lowerBound, upperBound);
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(String.valueOf(generatedID));
		return appendChecksum(generatedIdLength, generatedID, verhoeffDigit);
	}

	/**
	 * Appends a checksum to generated id
	 * 
	 * @param generatedIdLength
	 *            The length of id
	 * @param generatedVId
	 *            The generated id
	 * @param verhoeffDigit
	 *            The checksum to append
	 */
	private String appendChecksum(int generatedIdLength, Long generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(groupidLength);
		return vidSb.insert(0, generatedVId).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}

}

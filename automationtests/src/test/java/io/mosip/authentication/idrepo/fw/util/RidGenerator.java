package io.mosip.authentication.idrepo.fw.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Arjun Chandramohan
 *
 */
public class RidGenerator {

	/**
	 * accept type as valid or invalid
	 * 
	 * @return the RID based on the time stamp for valid case
	 */
	public String generateRID(String type) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		List<String> givenList = Arrays.asList("Java", "BOOLEAN:true", "LONG:376378783", "^%^%**&&*%%");
		Random rand = new Random();

		if (type.equals("valid"))
			return "278476573600025" + LocalDateTime.now().format(format);
		else
			return givenList.get(rand.nextInt(givenList.size()));
	}
}

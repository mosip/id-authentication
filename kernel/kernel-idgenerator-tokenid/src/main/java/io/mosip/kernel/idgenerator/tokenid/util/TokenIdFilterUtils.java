/**
 * 
 */
package io.mosip.kernel.idgenerator.tokenid.util;

import java.util.stream.IntStream;

/**
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public class TokenIdFilterUtils {

	/**
	 * Private constructor for IdFilter
	 */
	private TokenIdFilterUtils() {
	}
   
	/**
	 * Upper bound of number of digits in sequence allowed in id. For example if
	 * limit is 3, then 12 is allowed but 123 is not allowed in id (in both
	 * ascending and descending order)
	 */
	private static final int SEQUENCE_LIMIT = 3;
	
	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "0123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "9876543210";

	
	
	

	/**
	 * Checks if the input id is valid by passing the id through
	 *  SEQUENCE_LIMIT filter,  #REPEATING_LIMIT filter and
	 *  REPEATING_BLOCK_LIMIT filters
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the input id is valid
	 */
	public static boolean isValidId(String id) {

		return !(sequenceFilter(id)) ;
	}

	/**
	 * Checks the input id for SEQUENCE_LIMIT filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private static boolean sequenceFilter(String id) {
		return IntStream.rangeClosed(0, id.length() - SEQUENCE_LIMIT).parallel()
				.mapToObj(index -> id.subSequence(index, index + SEQUENCE_LIMIT))
				.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
	}

	
}

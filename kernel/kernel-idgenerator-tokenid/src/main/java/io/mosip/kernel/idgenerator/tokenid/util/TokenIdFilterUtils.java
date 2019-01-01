/**
 * 
 */
package io.mosip.kernel.idgenerator.tokenid.util;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Srinivasan
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Component
public class TokenIdFilterUtils {

	
	/**
	 * Upper bound of number of digits in sequence allowed in id. For example if
	 * limit is 3, then 12 is allowed but 123 is not allowed in id (in both
	 * ascending and descending order)
	 */
	@Value("${mosip.kernel.tokenid.sequenselimit}")
	//private static final int SEQUENCE_LIMIT =3;
	private int sequenceLimit;
	
	/**
	 * Private constructor for IdFilter
	 */
	/*@PostConstruct
	public void TokenIdFilterUtils() {
	}*/
   
	
	
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
	public  boolean isValidId(String id) {

		return !(sequenceFilter(id)) ;
	}

	/**
	 * Checks the input id for SEQUENCE_LIMIT filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private  boolean sequenceFilter(String id) {
		System.out.println("======sequenceLimit==="+sequenceLimit);
		return IntStream.rangeClosed(0, id.length() - sequenceLimit).parallel()
				.mapToObj(index -> id.subSequence(index, index + sequenceLimit))
				.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
	}

	
}

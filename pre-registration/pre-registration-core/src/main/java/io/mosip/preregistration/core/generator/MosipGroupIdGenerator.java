package io.mosip.preregistration.core.generator;

import org.springframework.stereotype.Service;

/**
 * This is an interface for the generation of GroupId
 * 
 * @author M1037717
 * @since 1.0.0
 *
 */
@Service
public interface MosipGroupIdGenerator<T> {
	/**
	 * Function to generate an groupId
	 * 
	 * @return The generated groupId
	 */
	T generateGroupId();

}

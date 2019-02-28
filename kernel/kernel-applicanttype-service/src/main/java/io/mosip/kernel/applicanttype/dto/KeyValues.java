package io.mosip.kernel.applicanttype.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author Bal Vikash Sharma
 *
 */
@Data
public class KeyValues implements Serializable {
	private static final long serialVersionUID = 877664400274091548L;
	private Map<String, String> request = new HashMap<>();
}

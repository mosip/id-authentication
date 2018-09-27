package org.mosip.auth.service.filter;

import lombok.Data;

/**
 * The TSP info used for TSP authentication
 *
 * @author Loganathan Sekaran
 */
@Data
class TSPInfo {
		private String auaCode;
		private String licenseKey;
		private String digitalSignature;
}
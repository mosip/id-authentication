package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Map;

import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;
import io.mosip.authentication.service.impl.indauth.builder.BioAuthType;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public final class BioMatcherUtil {

	private static final String MANTRA = "mantra";

	private static final String COGENT = "cogent";

	private static MantraFingerprintProvider mantraFingerprintProvider = new MantraFingerprintProvider();

	private static CogentFingerprintProvider cogentFingerprintProvider = new CogentFingerprintProvider();

	public static double doPartialMatch(String reqInfo, String entityInfo, Map<String, Object> props) {
		Object object = props.get(BioInfo.class.getSimpleName());
		if (object instanceof BioInfo) {
			BioInfo bioInfo = (BioInfo) object;
			String make = bioInfo.getDeviceInfo().getMake();
			if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_MIN.getType())) {
				if (make.equalsIgnoreCase(COGENT)) {
					return cogentFingerprintProvider.scoreCalculator(reqInfo, entityInfo);
				} else if (make.equalsIgnoreCase(MANTRA)) {
					return mantraFingerprintProvider.scoreCalculator(reqInfo, entityInfo);
				}
			} else if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_IMG.getType())) {
				if (make.equalsIgnoreCase(COGENT)) {
					return cogentFingerprintProvider.scoreCalculator(reqInfo, entityInfo);
				} else if (make.equalsIgnoreCase(MANTRA)) {
					return mantraFingerprintProvider.scoreCalculator(reqInfo, reqInfo);
				}
			}
		}
		return 0;
	}

}

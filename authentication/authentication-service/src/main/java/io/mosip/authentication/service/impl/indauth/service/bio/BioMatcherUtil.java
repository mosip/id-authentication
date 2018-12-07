package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Base64;
import java.util.Map;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;

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
		byte[] decodedrefInfo = decodeValue(reqInfo);
		byte[] decodeEntityInfo = decodeValue(entityInfo);
		
		if (object instanceof BioInfo) {
			BioInfo bioInfo = (BioInfo) object;
			String make = bioInfo.getDeviceInfo().getMake();
			if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_MIN.getType())) {
				FingerprintTemplate template1 = new FingerprintTemplate().convert(decodedrefInfo);
				FingerprintTemplate template2 = new FingerprintTemplate().convert(decodeEntityInfo);
				if (make.equalsIgnoreCase(COGENT)) {
					return cogentFingerprintProvider.scoreCalculator(template1.serialize(),
							template2.serialize());
				} else if (make.equalsIgnoreCase(MANTRA)) {
					return mantraFingerprintProvider.scoreCalculator(template1.serialize(),
							template2.serialize());
				}
			} else if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_IMG.getType())) {
				if (make.equalsIgnoreCase(COGENT)) {
					return cogentFingerprintProvider.scoreCalculator(decodedrefInfo, decodeEntityInfo);
				} else if (make.equalsIgnoreCase(MANTRA)) {
					return mantraFingerprintProvider.scoreCalculator(decodedrefInfo, decodeEntityInfo);
				}
			}
		}
		return 0;
	}

	private static byte[] decodeValue(String value) {
		return Base64.getDecoder().decode(value);
	}

	private static String getString(byte[] value) {
		return new String(value);
	}

}

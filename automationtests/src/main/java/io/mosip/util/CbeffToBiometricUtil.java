package io.mosip.util;

import java.util.List;

/**
 * The Class CbeffToBiometricUtil.
 * 
 * @author M1048358 Alok
 */
public class CbeffToBiometricUtil {

	/** The cbeffutil. */
	private CbeffUtil cbeffutil;

	/**
	 * Instantiates a new cbeff to biometric util.
	 *
	 * @param cbeffutil the cbeffutil
	 */
	public CbeffToBiometricUtil(CbeffUtil cbeffutil) {
		this.cbeffutil = cbeffutil;
	}

	/**
	 * Gets the photo.
	 *
	 * @param cbeffFileString the cbeff file string
	 * @param type the type
	 * @param subType the sub type
	 * @return the photo
	 * @throws Exception the exception
	 */
	public byte[] getImageBytes(String cbeffFileString, String type, List<String> subType) throws Exception {
		byte[] photoBytes = null;
		if (cbeffFileString != null) {
			byte[] biometricBytes = CryptoUtil.decodeBase64(cbeffFileString);

			List<BIRType> bIRTypeList = cbeffutil.getBIRDataFromXML(biometricBytes);
			photoBytes = getPhotoByTypeAndSubType(bIRTypeList, type, subType);
		}

		return photoBytes;
	}

	/**
	 * Gets the photo by type and sub type.
	 *
	 * @param bIRTypeList the b IR type list
	 * @param type the type
	 * @param subType the sub type
	 * @return the photo by type and sub type
	 */
	private byte[] getPhotoByTypeAndSubType(List<BIRType> bIRTypeList, String type, List<String> subType) {
		byte[] photoBytes = null;
		for (BIRType birType : bIRTypeList) {
			if (birType.getBDBInfo() != null) {
				List<SingleType> singleTypeList = birType.getBDBInfo().getType();
				List<String> subTypeList = birType.getBDBInfo().getSubtype();

				boolean isType = isSingleType(type, singleTypeList);
				boolean isSubType = isSubType(subType, subTypeList);

				if (isType && isSubType) {
					photoBytes = birType.getBDB();
					break;
				}
			}
		}
		return photoBytes;
	}

	/**
	 * Checks if is sub type.
	 *
	 * @param subType the sub type
	 * @param subTypeList the sub type list
	 * @return true, if is sub type
	 */
	private boolean isSubType(List<String> subType, List<String> subTypeList) {
		return subTypeList.equals(subType) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * Checks if is single type.
	 *
	 * @param type the type
	 * @param singleTypeList the single type list
	 * @return true, if is single type
	 */
	private boolean isSingleType(String type, List<SingleType> singleTypeList) {
		boolean isType = false;
		for (SingleType singletype : singleTypeList) {
			if (singletype.value().equalsIgnoreCase(type)) {
				isType = true;
			}
		}
		return isType;
	}

}

package io.mosip.registration.mdmservice.constants;

/**
 * 
 * Holds the MDM constants
 * 
 * @author balamurugan.ramamoorthy
 *
 */
public class MosipBioDeviceConstants {

	public static final String DEVICE_INFO_ENDPOINT = "deviceInfo";
	public static final String DEVICE_INFO_SERVICENAME = "mdm.deviceInfo";
	public static final String CAPTURE_ENDPOINT = "capture";
	public static final String CAPTURE_SERVICENAME = "mdm.capture";
	public static final String DEVICE_DISCOVERY_ENDPOINT = "deviceDiscovery";
	public static final String DEVICE_DISCOVERY_SERVICENAME = "mdm.deviceDiscovery";

	public static final String VALUE_FINGERPRINT = "FINGERPRINT";
	public static final String VALUE_IRIS = "IRIS";
	public static final String VALUE_FACE = "FACE";
	public static final String VALUE_VEIN = "VEIN";

	public static final String VALUE_SINGLE = "SINGLE";
	public static final String VALUE_SLAP_LEFT = "SLAP_LEFT";
	public static final String VALUE_SLAP_RIGHT = "SLAP_RIGHT";
	public static final String VALUE_SLAP_THUMB = "SLAP_THUMB";
	public static final String VALUE_SLAP_LEFT_ONBOARD = "SLAP_LEFT_ONBOARD";
	public static final String VALUE_SLAP_RIGHT_ONBOARD = "SLAP_RIGHT_ONBOARD";
	public static final String VALUE_SLAP_THUMB_ONBOARD = "SLAP_THUMB_ONBOARD";
	public static final String VALUE_TOUCHLESS = "TOUCHLESS";
	public static final String VALUE_DOUBLE = "DOUBLE";

	public static final String ISO_FILE_NAME = "ISOTemplate";
	public static final String ISO_IMAGE_FILE_NAME = "ISOImage";
	public static final String ISO_FILE = "ISOTemplate.iso";
	public static final String DUPLICATE_FINGER = "DuplicateFinger";
	public static final String ISO_IMAGE_FILE = "ISOImage.iso";

	public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
			"/fingerprints/lefthand/leftLittle/", "/fingerprints/lefthand/leftMiddle/",
			"/fingerprints/lefthand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS = new String[] {
			"/fingerprints/righthand/rightIndex/", "/fingerprints/righthand/rightLittle/",
			"/fingerprints/righthand/rightMiddle/", "/fingerprints/righthand/rightRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/righthand/rightIndex/",
			"/fingerprints/righthand/rightLittle/", "/fingerprints/righthand/rightMiddle/",
			"/fingerprints/righthand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/thumb/leftThumb/",
			"/fingerprints/thumb/rightThumb/" };

	public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/UserOnboard/leftHand/leftIndex/", "/UserOnboard/leftHand/leftLittle/",
			"/UserOnboard/leftHand/leftMiddle/", "/UserOnboard/leftHand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/UserOnboard/rightHand/rightIndex/", "/UserOnboard/rightHand/rightLittle/",
			"/UserOnboard/rightHand/rightMiddle/", "/UserOnboard/rightHand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] { "/UserOnboard/thumb/leftThumb/",
			"/UserOnboard/thumb/rightThumb/" };

}

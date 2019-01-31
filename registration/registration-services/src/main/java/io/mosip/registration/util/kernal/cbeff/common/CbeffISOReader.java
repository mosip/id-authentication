/**
 * 
 */
package io.mosip.registration.util.kernal.cbeff.common;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import io.mosip.registration.util.kernal.cbeff.constant.CbeffConstant;
import io.mosip.registration.util.kernal.cbeff.exception.CbeffException;

/**
 * @author Ramadurai Pandian
 *
 */
public class CbeffISOReader {

	public static byte[] readISOImage(String path, String type) throws Exception {
		File testFile = new File(path);
		DataInputStream in = new DataInputStream(new FileInputStream(testFile));
		int formatId = in.readInt();
		if (checkFormatIdentifier(formatId, type)) {
			byte[] result = new byte[(int) testFile.length()];
			FileInputStream fileIn = new FileInputStream(testFile);
			int bytesRead = 0;
			while (bytesRead < result.length) {
				bytesRead += fileIn.read(result, bytesRead, result.length - bytesRead);
			}
			fileIn.close();
			return result;
		} else {
			throw new CbeffException(
					"Format Identifier is wrong for the image,Please upload correct image of type : " + type);
		}
	}

	public static void writeISOImage(String path) {

	}

	private static boolean checkFormatIdentifier(int formatId, String type) {
		switch (type) {
		case "Finger":
			return CbeffConstant.FINGER_FORMAT_IDENTIFIER == formatId;
		case "Iris":
			return CbeffConstant.IRIS_FORMAT_IDENTIFIER == formatId;
		case "Face":
			return CbeffConstant.FACE_FORMAT_IDENTIFIER == formatId;
		}
		return false;
	}

}

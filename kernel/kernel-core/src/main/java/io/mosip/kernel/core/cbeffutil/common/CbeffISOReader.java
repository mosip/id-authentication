/**
 * 
 */
package io.mosip.kernel.core.cbeffutil.common;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.exception.CbeffException;



/**
 * @author Ramadurai Pandian
 * 
 * Class to read the ISO Image and Identify the format identifier
 *
 */
public class CbeffISOReader {
	
	
	/**
	 * Method used for reading ISO Image
	 * 
	 * @param path of the ISO image
	 * 
	 * @param type of ISO image
	 *        
	 * @return return byte array of image data
	 * 
	 * @exception Exception exception
	 * 
	 */
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

	/**
	 * Method used for validating Format Identifiers based on type
	 * 
	 * @param format id
	 * 
	 * @param type of image
	 *        
	 * @return boolean value if identifier matches with id
	 * 
	 */
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

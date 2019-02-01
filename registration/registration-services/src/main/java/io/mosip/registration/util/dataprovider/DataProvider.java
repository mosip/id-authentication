package io.mosip.registration.util.dataprovider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

public class DataProvider {

	private DataProvider() {

	}

	public static byte[] getImageBytes(String filePath) throws RegBaseCheckedException {
		filePath = "/dataprovider".concat(filePath);

		try {
			BufferedImage bufferedImage = ImageIO.read(DataProvider.class.getResourceAsStream(filePath));
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DATA_PROVIDER_UTIL,
					"Unable to read the Image bytes", ioException);
		}
	}

	public static void setApplicantDocumentDTO(ApplicantDocumentDTO applicantDocumentDTO, boolean isExceptionPhoto) throws RegBaseCheckedException {
		applicantDocumentDTO.setPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotographName("ApplicantPhoto.jpg");
		applicantDocumentDTO.setQualityScore(89.0);
		applicantDocumentDTO.setNumRetry(1);
		applicantDocumentDTO.setHasExceptionPhoto(isExceptionPhoto);
		if (isExceptionPhoto) {
			applicantDocumentDTO.setExceptionPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
			applicantDocumentDTO.setExceptionPhotoName("ExceptionPhoto.jpg");
		}
	}

}

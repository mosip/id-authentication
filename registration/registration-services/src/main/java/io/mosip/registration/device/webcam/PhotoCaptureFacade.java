package io.mosip.registration.device.webcam;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.sarxos.webcam.Webcam;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.device.webcam.impl.LogitechPhotoProvider;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Himaja Dhanyamraju
 */
@Component
public class PhotoCaptureFacade extends LogitechPhotoProvider {

	@Autowired
	private MosipWebcamProvider webcamProvider;
	
	private List<MosipWebcamProvider> webCamProviders;
	
	public MosipWebcamProvider getPhotoProviderFactory(String make) {
		for (MosipWebcamProvider mosipWebcamProvider : webCamProviders) {
			if (mosipWebcamProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				webcamProvider = mosipWebcamProvider;
			}
		}
		return webcamProvider;
	}

	@Autowired
	public void setWebCamProviders(List<MosipWebcamProvider> mosipWebcamProvider) {
		this.webCamProviders = mosipWebcamProvider;
	}

	public Webcam connect(int width, int height) {
		return webcamProvider.connect(width, height);
	}

	public BufferedImage captureImage(Webcam webcam) {
		return webcamProvider.captureImage(webcam);
	}

	public void close(Webcam webcam) {
		webcamProvider.close(webcam);
	}

	/**
	 * Stubs the default photo if camera is not available
	 * 
	 * @param applicantDocumentDTO
	 *            the object where photo will be stored
	 * @param isExceptionPhoto
	 *            specifies if exception photo is required
	 * @throws RegBaseCheckedException
	 */
	public void captureStubApplicantPhoto(ApplicantDocumentDTO applicantDocumentDTO, boolean isExceptionPhoto)
			throws RegBaseCheckedException {
		applicantDocumentDTO.setPhoto(getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotographName("ApplicantPhoto.jpg");
		applicantDocumentDTO.setQualityScore(89.0);
		applicantDocumentDTO.setNumRetry(1);
		applicantDocumentDTO.setHasExceptionPhoto(isExceptionPhoto);
		if (isExceptionPhoto) {
			applicantDocumentDTO.setExceptionPhoto(getImageBytes("/applicantPhoto.jpg"));
			applicantDocumentDTO.setExceptionPhotoName("ExceptionPhoto.jpg");
		}
	}

	private byte[] getImageBytes(String filePath) throws RegBaseCheckedException {
		filePath = "/dataprovider".concat(filePath);

		try {
			BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream(filePath));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DATA_PROVIDER_UTIL,
					"Unable to read the Image bytes", ioException);
		}
	}

}

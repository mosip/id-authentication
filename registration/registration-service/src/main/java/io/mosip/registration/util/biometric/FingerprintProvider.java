package io.mosip.registration.util.biometric;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.gson.JsonSyntaxException;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public abstract class FingerprintProvider implements MosipFingerprintProvider {

	protected String minutia = "";
	protected byte isoTemplate[] = null;
	protected String errorMessage = null;
	protected WritableImage fingerPrintImage = null;
	protected byte fingerDataInByte[] =null;
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FingerprintProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * 
	 * MosipFingerprintProvider#scoreCalculator(byte[], byte[])
	 */
	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#scoreCalculator(byte[], byte[])
	 */
	@Override
	public double scoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		double score = 0;
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().convert(isoImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().convert(isoImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			score = matcher.index(template1).match(template2);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.debug("REGISTRATION - FINGERPRINTPROVIDER - SCORECALCULATOR", APPLICATION_NAME, APPLICATION_ID,
					"Calculating Finger print score for ISO Template");
		}
		return score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#scoreCalculator(java.lang.String, java.lang.String)
	 * 
	 */
	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#scoreCalculator(java.lang.String, java.lang.String)
	 */
	@Override
	public double scoreCalculator(String fingerImage1, String fingerImage2) {
		double score = 0.0;
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().deserialize(fingerImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().deserialize(fingerImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			score = matcher.index(template1).match(template2);
		} catch (IllegalArgumentException | JsonSyntaxException exception) {
			LOGGER.debug("REGISTRATION - FINGERPRINTPROVIDER - SCORECALCULATOR", APPLICATION_NAME, APPLICATION_ID,
					"Calculating Finger print score for Minutia");
		}
		return score;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#captureFingerprint(int, int, java.lang.String)
	 */
	@Override
	public abstract int captureFingerprint(int qualityScore, int captureTimeOut, String outputType);
	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#uninitFingerPrintDevice()
	 */
	@Override
	public abstract void uninitFingerPrintDevice();

	protected void prepareMinutia(byte rawImage[]) {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(rawImage);
		this.minutia = fingerprintTemplate.serialize();
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#getMinutia()
	 */
	@Override
	public String getMinutia() {
		return minutia;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#getIsoTemplate()
	 */
	@Override
	public byte[] getIsoTemplate() {
		return isoTemplate;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	/* (non-Javadoc)
	 * To display image in the screen.
	 * @see io.mosip.registration.util.biometric.MosipFingerprintProvider#getFingerPrintImage()
	 */
	@Override
	public WritableImage getFingerPrintImage() throws IOException{
		WritableImage writableImage = null;
		
		if (null != fingerDataInByte) {
			BufferedImage l_objBufferImg = null;
			try {
				l_objBufferImg = ImageIO.read(new ByteArrayInputStream(fingerDataInByte));
			} catch (IOException ex) {
				throw ex;
			}

			if (l_objBufferImg != null) {
				writableImage = new WritableImage(l_objBufferImg.getWidth(), l_objBufferImg.getHeight());
				PixelWriter pw = writableImage.getPixelWriter();
				for (int x = 0; x < l_objBufferImg.getWidth(); x++) {
					for (int y = 0; y < l_objBufferImg.getHeight(); y++) {
						pw.setArgb(x, y, l_objBufferImg.getRGB(x, y));
					}
				}
			}
		}
		return writableImage;
	}

	
}

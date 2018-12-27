package io.mosip.kernel.core.qrcodegenerator.spi;

import java.io.IOException;

import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;


/**
 * Interface for QR-Code-Generation
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 * 
 * @param <T>
 *            the type of QR-Version
 */
public interface QrCodeGenerator<T> {

	/**
	 * Method to generate QR Code
	 * 
	 * @param data
	 *            data to encode in the QR code
	 * @param version
	 *            {@link QrVersion} class for QR Code version
	 * @return array of byte containing QR Code in PNG format
	 * @throws QrcodeGenerationException
	 *             exceptions which may occur when encoding a QRcode using the
	 *             Writer framework.
	 * @throws IOException
	 *             exceptions which may occur when write to the byte stream fail
	 */
	byte[] generateQrCode(String data, T version) throws QrcodeGenerationException, IOException;

}
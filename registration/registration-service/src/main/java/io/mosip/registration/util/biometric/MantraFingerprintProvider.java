package io.mosip.registration.util.biometric;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.machinezoo.sourceafis.FingerprintTemplate;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class MantraFingerprintProvider extends FingerprintProviderNew implements MFS100Event {

	/** The fp device. */
	private MFS100 fpDevice = new MFS100(this);

	private String fingerPrintType = "";

	public void captureFingerprint(int qualityScore, int captureTimeOut, String outputType) {
		fingerPrintType = outputType;
		if (fpDevice.Init() == 0 && fpDevice.IsConnected()) {
			minutia = "";
			errorMessage = "";
			fpDevice.StartCapture(qualityScore, captureTimeOut, false);
		}
	}
	
	public void uninitFingerPrintDevice() {
		fpDevice.StopCapture();
		fpDevice.Uninit();
	}

	@Override
	public void OnCaptureCompleted(boolean status, int erroeCode, String errorMsg, FingerData fingerData) {
		errorMessage = errorMsg;
		if (fingerPrintType.equals("minutia")) {
			FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(fingerData.ISOTemplate());
			minutia = fingerprintTemplate.serialize();
		}
		OnPreview(fingerData);
	}

	@Override
	public void OnPreview(FingerData fingerData) {
		if (null != fingerData.FingerImage()) {
			BufferedImage l_objBufferImg = null;
			try {
				l_objBufferImg = ImageIO.read(new ByteArrayInputStream(fingerData.FingerImage()));
			} catch (IOException ex) {
				System.out.println("Image failed to load.");
			}

			WritableImage l_objWritableImg = null;
			if (l_objBufferImg != null) {
				l_objWritableImg = new WritableImage(l_objBufferImg.getWidth(), l_objBufferImg.getHeight());
				PixelWriter pw = l_objWritableImg.getPixelWriter();
				for (int x = 0; x < l_objBufferImg.getWidth(); x++) {
					for (int y = 0; y < l_objBufferImg.getHeight(); y++) {
						pw.setArgb(x, y, l_objBufferImg.getRGB(x, y));
					}
				}
			}
			fingerPrintImage = l_objWritableImg;
		}

	}

}

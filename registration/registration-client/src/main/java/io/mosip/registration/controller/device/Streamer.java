package io.mosip.registration.controller.device;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.SocketTimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Component
public class Streamer {

	private InputStream urlStream;

	private boolean isRunning = true;

	private final String CONTENT_LENGTH = "Content-Length:";
	
	@Autowired
	private MosipBioDeviceManager mosipBioDeviceManager;

	private Thread t = null;

	public void startStream(String bioType, ImageView streamImage, ImageView scanImage) {

		t = new Thread(new Runnable() {

			public void run() {

				urlStream = mosipBioDeviceManager.stream(bioType);
				isRunning = true;
				while (isRunning) {
					try {
						byte[] imageBytes = retrieveNextImage();
						ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
						Image img = new Image(imageStream);
						streamImage.setImage(img);
						scanImage.setImage(img);
					} catch (SocketTimeoutException ste) {
						stop();

					} catch (IOException e) {
						System.err.println("failed stream read: " + e);
						stop();
					}
				}

				// close streams
				try {
					urlStream.close();
				} catch (IOException ioe) {
					System.err.println("Failed to close the stream: " + ioe);
				}
			}

		}, "STREAMER_THREAD");

		t.start();

	}
	
	  /**
     * Using the urlStream get the next JPEG image as a byte[]
     *
     * @return byte[] of the JPEG
     * @throws IOException
     */
    private byte[] retrieveNextImage() throws IOException {
        int currByte = -1;

        boolean captureContentLength = false;
        StringWriter contentLengthStringWriter = new StringWriter(128);
        StringWriter headerWriter = new StringWriter(128);

        int contentLength = 0;

        while ((currByte = urlStream.read()) > -1) {
            if (captureContentLength) {
                if (currByte == 10 || currByte == 13) {
                    contentLength = Integer.parseInt(contentLengthStringWriter.toString().replace(" ", ""));
                    break;
                }
                contentLengthStringWriter.write(currByte);

            } else {
                headerWriter.write(currByte);
                String tempString = headerWriter.toString();
                int indexOf = tempString.indexOf(CONTENT_LENGTH);
                if (indexOf > 0) {
                    captureContentLength = true;
                }
            }
        }

        // 255 indicates the start of the jpeg image
        while (urlStream.read() != 255) {
        	
        }

        // rest is the buffer
        byte[] imageBytes = new byte[contentLength + 1];
        // since we ate the original 255 , shove it back in
        imageBytes[0] = (byte) 255;
        int offset = 1;
        int numRead = 0;
        while (offset < imageBytes.length
                && (numRead = urlStream.read(imageBytes, offset, imageBytes.length - offset)) >= 0) {
            offset += numRead;
        }

        return imageBytes;
    }

    /**
     * Stop the loop, and allow it to clean up
     */
    public synchronized void stop() {
    	if(t!=null) {
        isRunning = false;
        t.stop();
        try {
			urlStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    }


}

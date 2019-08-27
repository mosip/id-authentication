package io.mosip.authentication.demo.service;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;

import javafx.scene.image.ImageView;

/**
 * The Class MjpegTestRunner.
 * 
 * @author Sanjay Murali
 */
@SuppressWarnings("restriction")
public class MjpegTestRunner implements Runnable {
    private static final String CONTENT_LENGTH = "Content-Length:";
    private static final String CONTENT_TYPE = "Content-type: image/jpeg";
    private final URL url;
    private ImageView viewer;
    private InputStream urlStream;
    private boolean isRunning = true; //TODO should be false by default

    public MjpegTestRunner(ImageView viewer, URL url) throws IOException {
        this.viewer = viewer;
        this.url = url;
        start(); //TODO remove from here
    }

    private void start() throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		String request = "{\"env\":\"Staging\",\"mosipProcess\":\"Registration\",\"version\":\"1.0\",\"timeout\":10000,\"captureTime\":\"0001-01-01T00:00:00\",\"bio\":[{\"type\":\"FIR\",\"count\":1,\"exception\":[],\"requestedScore\":40,\"deviceId\":\"1c7f1e29-db9b-4afc-98fc-91d6d40bc8e2\",\"deviceSubId\":1,\"previousHash\":\"\"}],\"customOpts\":[{\"Name\":\"name1\",\"Value\":\"value1\"}]}";
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(request);
		wr.flush();
		wr.close();
		con.setReadTimeout(5000);
		con.connect();
        urlStream = con.getInputStream();
    }

    /**
     * Stop the loop, and allow it to clean up
     */
    public synchronized void stop() {
        isRunning = false;
    }

    /**
     * Keeps running while process() returns true
     * <p>
     * Each loop asks for the next JPEG image and then sends it to our JPanel to draw
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (isRunning) {
            try {
                byte[] imageBytes = retrieveNextImage();
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);

                this.viewer.setImage(new javafx.scene.image.Image(bais));
                
            } catch (SocketTimeoutException ste) {
                System.err.println("failed stream read: " + ste);
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

    private void addTimestampToFrame(BufferedImage frame) {
        Graphics2D g2d = (Graphics2D) frame.getGraphics().create();
        try {
            g2d.setColor(Color.WHITE);
            g2d.drawString(new Date().toString(), 10, frame.getHeight()-50);
        } finally {
            g2d.dispose();
        }
    }

    /**
     * Using the urlStream get the next JPEG image as a byte[]
     *
     * @return byte[] of the JPEG
     * @throws IOException
     */
    private byte[] retrieveNextImage() throws IOException {
        int currByte = -1;

        String header = null;
        // build headers
        // the DCS-930L stops it's headers

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
        while ((urlStream.read()) != 255) {
            // just skip extras
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

}
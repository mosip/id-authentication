package io.mosip.kernel.idrepo.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StreamUtils;

/**
 * The request wrapper used in Auth filter that allows to re-read the request
 * body.
 *
 * @author Loganathan Sekar
 */
class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

	/** The raw data. */
	private byte[] rawData;

	/** The request. */
	private HttpServletRequest request;

	/** The servlet stream. */
	private ResettableServletInputStream servletStream;

	/**
	 * Instantiates a new resettable stream http servlet request.
	 *
	 * @param request
	 *            the request
	 */
	public ResettableStreamHttpServletRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
		this.servletStream = new ResettableServletInputStream();
	}

	/**
	 * Reset input stream.
	 */
	public void resetInputStream() {
		servletStream.stream = new ResettableServletInputStream(
				new ByteArrayInputStream(rawData));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (rawData == null) {
			rawData =
					StreamUtils.copyToByteArray(this.request.getInputStream());
			servletStream.stream = new ByteArrayInputStream(rawData);
		}
		return servletStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		if (rawData == null) {
			rawData =
					StreamUtils.copyToByteArray(this.request.getInputStream());
			servletStream.stream = new ByteArrayInputStream(rawData);
		}
		return new BufferedReader(new InputStreamReader(servletStream));
	}

	public void replaceData(byte[] newData) {
		rawData = newData;
		servletStream.stream = new ByteArrayInputStream(rawData);
	}

	/**
	 * The Class ResettableServletInputStream.
	 */
	private class ResettableServletInputStream extends ServletInputStream {

		/** The stream. */
		private InputStream stream;

		/** The eof reached. */
		private boolean eofReached;

		/** The closed. */
		private boolean closed;

		/**
		 * Instantiates a new resettable servlet input stream.
		 *
		 * @param stream
		 *            the stream
		 */
		public ResettableServletInputStream(InputStream stream) {
			this.stream = stream;
		}

		/**
		 * Instantiates a new resettable servlet input stream.
		 */
		public ResettableServletInputStream() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			int val = stream.read();
			if (val == -1) {
				eofReached = true;
			}
			return val;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletInputStream#isFinished()
		 */
		@Override
		public boolean isFinished() {
			return eofReached;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletInputStream#isReady()
		 */
		@Override
		public boolean isReady() {
			return !eofReached && !closed;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#close()
		 */
		@Override
		public void close() throws IOException {
			super.close();
			closed = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.
		 * ReadListener)
		 */
		@Override
		public void setReadListener(ReadListener listener) {
			// TODO Auto-generated method stub

		}
	}

}

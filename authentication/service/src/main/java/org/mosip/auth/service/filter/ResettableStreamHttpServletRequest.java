package org.mosip.auth.service.filter;

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
class ResettableStreamHttpServletRequest extends
			HttpServletRequestWrapper {

		private byte[] rawData;
		private HttpServletRequest request;
		private ResettableServletInputStream servletStream;

		public ResettableStreamHttpServletRequest(HttpServletRequest request) {
			super(request);
			this.request = request;
			this.servletStream = new ResettableServletInputStream();
		}


		public void resetInputStream() {
			servletStream.stream = new ResettableServletInputStream(new ByteArrayInputStream(rawData));
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			if (rawData == null) {
				rawData = StreamUtils.copyToByteArray(this.request.getInputStream());
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
			return servletStream;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			if (rawData == null) {
				rawData = StreamUtils.copyToByteArray(this.request.getInputStream());
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
			return new BufferedReader(new InputStreamReader(servletStream));
		}
		

		private class ResettableServletInputStream extends ServletInputStream {

			private InputStream stream;
			private boolean eofReached;
			private boolean closed;

			public ResettableServletInputStream(InputStream stream) {
				this.stream = stream;
			}

			public ResettableServletInputStream() {
			}

			@Override
			public int read() throws IOException {
				int val = stream.read();
				if(val == -1) {
					eofReached = true;
				}
				return val;
			}

			@Override
			public boolean isFinished() {
				return eofReached;
			}

			@Override
			public boolean isReady() {
				return !eofReached && !closed;
			}
			
			@Override
			public void close() throws IOException {
				super.close();
				closed = true;
			}

			@Override
			public void setReadListener(ReadListener listener) {
				// TODO Auto-generated method stub
				
			}
		}
	}

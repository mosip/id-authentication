package io.mosip.authentication.common.service.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * The response wrapper used in Auth filter to capture the response body in the
 * filter.
 *
 * @author Loganathan Sekar
 */
class CharResponseWrapper extends HttpServletResponseWrapper {

	/** The output. */
	private ByteArrayOutputStream output;

	/** The closed. */
	private boolean closed;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return output.toString();
	}

	/**
	 * Instantiates a new char response wrapper.
	 *
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public CharResponseWrapper(HttpServletResponse response)
			throws IOException {
		super(response);
		this.output = new ByteArrayOutputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletResponseWrapper#getWriter()
	 */
	public PrintWriter getWriter() {
		return new PrintWriter(new OutputStreamWriter(output));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jakarta.servlet.ServletResponseWrapper#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				output.write(b);
			}

			@Override
			public void setWriteListener(WriteListener listener) {
			    //override method
			}

			@Override
			public boolean isReady() {
				return !closed;
			}

			@Override
			public void close() throws IOException {
				super.close();
				closed = true;
			}
		};
	}

}
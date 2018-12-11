package io.mosip.kernel.idrepo.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

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

	/** The writer. */
	private PrintWriter writer;

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
		this.writer = response.getWriter();
		this.output = new ByteArrayOutputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponseWrapper#getWriter()
	 */
	public PrintWriter getWriter() {
		return new PrintWriter(new OutputStreamWriter(output));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
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
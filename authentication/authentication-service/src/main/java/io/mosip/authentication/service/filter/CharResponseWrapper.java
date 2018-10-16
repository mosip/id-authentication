package io.mosip.authentication.service.filter;

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
	private ByteArrayOutputStream output;
	private boolean closed;
	private PrintWriter writer;
	private HttpServletResponse response;

	public String toString() {
		return output.toString();
	}

	public CharResponseWrapper(HttpServletResponse response) throws IOException {
		super(response);
		this.response = response;
		writer = response.getWriter();
		output = new ByteArrayOutputStream();
	}
	
	public void clear() throws IOException {
		output = new ByteArrayOutputStream();
		writer = super.getWriter();
		writer.flush();
	}

	public PrintWriter getWriter() {
		return new PrintWriter(new OutputStreamWriter(output));
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				output.write(b);
				writer.write(b);
			}

			@Override
			public void setWriteListener(WriteListener listener) {

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
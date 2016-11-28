package com.hsm.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.catalina.util.StringManager;

public class SocketInputStream extends InputStream {
	private static final byte CR = (byte) '\r';
	private static final byte LF = (byte) '\n';
	private static final byte SP = (byte) ' ';
	private static final byte HT = (byte) '\t';
	private static final byte COLON = (byte) ':';
	private static final int LC_OFFSET = 'A' - 'a';

	protected byte buf[];
	protected int count;
	protected int pos;
	protected InputStream is;

	public SocketInputStream(InputStream is, int bufferSize) {
		this.is = is;
		buf = new byte[bufferSize];
	}

	protected static StringManager sm = StringManager
			.getManager(Constants.Package);

	public void readRequestLine(HttpRequestLine requestLine) throws IOException {
		if (requestLine.methodEnd != 0)
			requestLine.recycle();
		int chr = 0;
		do {
			try {
				chr = read();
			} catch (IOException e) {
				chr = -1;
			}
		} while ((chr == CR) || (chr == LF));
		if (chr == -1)
			throw new EOFException(sm.getString("requestStream.readline.error"));
		pos--;

		// Read the method

		int maxRead = requestLine.method.length;
		int readStart = pos;
		int readCount = 0;
		boolean space = false;
		while (!space) {
			if (readCount >= maxRead) {
				if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.method, 0, newBuffer, 0,
							maxRead);
					requestLine.method = newBuffer;
					maxRead = requestLine.method.length;
				} else {
					/*
					 * throw new IOException
					 * (sm.getString("requestStream.readline.toolong"));
					 */
				}
			}
			if (pos >= count) {
				int val = read();
				if (val == -1) {
					throw new IOException(
							sm.getString("requestStream.readline.error"));
				}
				pos = 0;
				readStart = 0;
			}
			if (buf[pos] == SP) {
				space = true;
			}
			requestLine.method[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}
		requestLine.methodEnd = readCount - 1;

		// Read uri
		maxRead = requestLine.uri.length;
		readStart = pos;
		readCount = 0;
		space = false;
		boolean eol = false;
		while (!space) {
			if (readCount >= maxRead) {
				if (2 * maxRead <= requestLine.MAX_URL_SIZE) {
					char[] newUri = new char[2 * maxRead];
					System.arraycopy(requestLine.uri, 0, newUri, 0, maxRead);
					requestLine.uri = newUri;
					maxRead = requestLine.uri.length;
				} else {
					throw new IOException(
							sm.getString("requestStream.readline.toolong"));
				}
			}
			if (pos >= count) {
				int val = read();
				if (val == -1) {
					throw new IOException(
							sm.getString("requestStream.readline.error"));
				}
				pos = 0;
				readStart = 0;
			}
			if (buf[pos] == SP) {
				space = true;
			} else if ((buf[pos] == CR) || (buf[pos] == LF)) {
				// HTTP/0.9 style request
				eol = true;
				space = true;
			}
			requestLine.uri[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}
		requestLine.uriEnd = readCount - 1;

		// Read protocol
		maxRead = requestLine.protocol.length;
		readStart = pos;
		readCount = 0;
		while (!eol) {
			if (readCount >= maxRead) {
				if (2 * maxRead <= requestLine.MAX_PROTOCOL_SIZE) {
					char[] newProtpcol = new char[2 * maxRead];
					System.arraycopy(requestLine.protocol, 0, newProtpcol, 0,
							maxRead);
					requestLine.uri = newProtpcol;
					maxRead = requestLine.protocol.length;
				} else {
					throw new IOException(
							sm.getString("requestStream.readline.toolong"));
				}
			}
			if (pos >= count) {
				int val = read();
				if (val == -1) {
					throw new IOException(
							sm.getString("requestStream.readline.error"));
				}
				pos = 0;
				readStart = 0;
			}
			if (buf[pos] == CR) {
				
			} else if (buf[pos] == LF) {
				eol = true;
			} else {
				requestLine.protocol[readCount] = (char) buf[pos];
				readCount++;
			}
			pos++;
		}
		requestLine.protocolEnd = readCount;

	}

	public void readHeader(HttpHeader header) throws IOException {
		if (header.nameEnd != 0)
			header.recycle();
		char chr = (char) read();
		if ((chr == CR) || chr == LF) {
			if (chr == CR)
				read();
			header.nameEnd = 0;
			header.valueEnd = 0;
		} else {
			pos--;
		}

		// Read the header.name
		int maxRead = header.name.length;
		int readStart = pos;
		int readCount = 0;

		boolean colon = false;

		while (!colon) {
			if (readCount >= maxRead) {
				if (2 * maxRead <= header.MAX_NAME_SIZE) {
					char[] newChar = new char[2 * maxRead];
					System.arraycopy(header.name, 0, newChar, 0, maxRead);
					header.name = newChar;
					maxRead = header.name.length;
				} else {
					throw new IOException(
							sm.getString("requestStream.readline.toolong"));
				}
				if (pos >= count) {
					int val = read();
					if (val == -1) {
						throw new IOException(
								sm.getString("requestStream.readline.error"));
					}
					pos = 0;
					readStart = 0;
				}
				if (buf[pos] == COLON) {
					colon = true;
				}
				char val = (char) buf[pos];
				if (val >= 'A' && val <= 'Z') {
					header.name[readCount] = (char) (val - LC_OFFSET);
				} else {
					header.name[readCount] = val;
				}
				pos++;
				readCount++;
			}
		}
		header.nameEnd = readCount - 1;

		// Read value
		maxRead = header.value.length;
		readStart = pos;
		readCount = 0;
		int crPos = -2;
		boolean eol = false;
		boolean validation = true;
		while (validation) {
			boolean space = true;
			while (space) {
				if (pos >= count) {
					int val = read();
					if (val == -1) {
						throw new IOException(
								sm.getString("requestStream.readline.error"));
					}
					pos = 0;
					readStart = 0;
				}
				if ((buf[pos] == SP) || buf[pos] == HT) {
					pos++;
				} else {
					space = false;
				}
			}
			while (!eol) {
				if (readCount >= maxRead) {
					if (2 * maxRead <= header.MAX_VALUE_SIZE) {
						char[] newChar = new char[2 * maxRead];
						System.arraycopy(header.value, 0, newChar, 0, maxRead);
						header.value = newChar;
						maxRead = header.value.length;
					} else {
						throw new IOException(
								sm.getString("requestStream.readline.toolong"));
					}
				}
				if (pos >= count) {
					int val = read();
					if (val == -1)
						throw new IOException(
								sm.getString("requestStream.readline.error"));
					pos = 0;
					readCount = 0;
				}
				if (buf[pos] == CR) {

				} else if (buf[pos] == LF) {
					eol = true;
				} else {
					int ch = buf[pos] & 0xff;
					header.value[readCount] = (char) ch;
					readCount++;
				}
				pos++;
			}
			int nextChar = read();
			if ((nextChar != SP) && (nextChar != HT)) {
				pos--;
				validation = false;
			} else {
				eol = false;
				if (readCount >= maxRead) {
					if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
						char[] newBuffer = new char[2 * maxRead];
						System.arraycopy(header.value, 0, newBuffer, 0, maxRead);
						header.value = newBuffer;
						maxRead = header.value.length;
					} else {
						throw new IOException(
								sm.getString("requestStream.readline.toolong"));
					}
				}
				header.value[readCount] = ' ';
				readCount++;
			}
		}
		header.valueEnd = readCount;
	}

	public int available() throws IOException {
		return (count - pos) + is.available();
	}

	public void close() throws IOException {
		if (is == null)
			return;
		is.close();
		is = null;
		buf = null;
	}

	protected void fill() throws IOException {
		pos = 0;
		count = 0;
		int nRead = is.read(buf, 0, buf.length);
		if (nRead > 0)
			count = nRead;
	}

	@Override
	public int read() throws IOException {
		if (pos >= count) {
			fill();
			if (pos >= count)
				return -1;
		}
		return buf[pos++] & 0xff;
	}

}

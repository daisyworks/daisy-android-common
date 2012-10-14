/*
    This file is part of the DaisyWorks Android Library.

    The DaisyWorks Android Library is free software: you can redistribute
    it and/or modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation, either version 3
    of the License, or (at your option) any later version.

    The DaisyWorks Android Library is distributed in the hope that it
    will be useful, but WITHOUT ANY WARRANTY; without even the implied
    warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with the DaisyWorks Android Library.
    If not, see <http://www.gnu.org/licenses/>.

    Copyright 2011 DaisyWorks, Inc
 */
package com.daisyworks.android.bluetooth;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class AsyncReaderThread extends Thread implements AsyncReader {
	private static final String LOG_TAG = "DaisyAsyncReaderThread";

	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final Reader reader;
	private final char[] readBuffer = new char[1024];
	private final BlockingQueue<String> input = new LinkedBlockingQueue<String>();

	private IOException exception;

	private boolean completed = false;

	public AsyncReaderThread(final Reader reader) {
		this.reader = reader;
	}

	protected String readNext() throws IOException {
		StringBuilder buf = null;

		while (true) {
			final int read = reader.read(readBuffer);
			if (read == -1) {
				if (BTCommThread.DEBUG_BLUETOOTH)
					Log.i(LOG_TAG, "read returned -1");
				return buf == null ? null : buf.toString();
			}

			if (buf == null) {
				buf = new StringBuilder(read);
			}

			buf.append(readBuffer, 0, read);

			if (readBuffer[read - 1] == '\n') {
				if (BTCommThread.DEBUG_BLUETOOTH)
					Log.i(LOG_TAG, "read ended in newline");
				return buf.toString();
			}
		}
	}

	@Override
	public void run() {
		if (BTCommThread.DEBUG_BLUETOOTH)
			Log.d(LOG_TAG, "AsyncReaderThread thread started");

		while (!shutdown.get()) {
			try {
				final String next = readNext();
				input.add(next);
			} catch (final IOException ioe) {
				if (!shutdown.get()) {
					if (BTCommThread.DEBUG_BLUETOOTH)
						Log.e(LOG_TAG, "Error reading input", ioe);
					shutdown.set(true);
					exception = ioe;
				}
			}
		}
		
		if (BTCommThread.DEBUG_BLUETOOTH)
			Log.d(LOG_TAG, "AsyncReaderThread thread stopped");

		synchronized (this) {
			completed = true;
			notifyAll();
		}
	}

	public void shutdown() {
		shutdown.set(true);
		this.interrupt();
	}

	public synchronized void waitForShutdown() {
		while (!completed) {
			try {
				wait();
			} catch (final InterruptedException ie) {
				return;
			}
		}
	}

	@Override
	public String readLine() throws IOException {
		try {
			final String next = input.take();
			if (next == null && exception != null) {
				throw exception;
			}
			return next;
		} catch (final InterruptedException ie) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	@Override
	public String readLine(final long maxWait) throws IOException {
		try {
			final String next = input.poll(maxWait, TimeUnit.MILLISECONDS);
			if (next == null && exception != null) {
				throw exception;
			}
			if (BTCommThread.DEBUG_BLUETOOTH)
				Log.i(LOG_TAG, "BlueTooth read: " + next);
			return next;
		} catch (final InterruptedException ie) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
}
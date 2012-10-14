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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BTCommThread extends Thread {
	public static final boolean DEBUG_BLUETOOTH = false;

	public static final int BLUETOOTH_START_CONNECT = 1;
	public static final int BLUETOOTH_CONNECTED = 2;
	public static final int BLUETOOTH_CONNECTION_ERROR = 3;
	public static final int BLUETOOTH_CONNECTION_CLOSED = 4;

	public static final String LOG_TAG = "BTCommThread";

	private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final LinkedList<BluetoothAction> actionQueue = new LinkedList<BluetoothAction>();

	protected Handler handler;

	protected String deviceAddress;

	protected Object socketLock = new Object();
	protected BluetoothSocket socket = null;
	protected AsyncReaderThread readerThread = null;
	protected OutputStream out = null;
	protected BluetoothAdapter btAdapter;
	protected BluetoothAction[] onConnectActions;

	protected AtomicLong timeout = new AtomicLong();

	public BTCommThread(final Handler handler, final String deviceAddress, final long timeout,
			final BluetoothAction[] onConnectActions) {
		this.handler = handler;
		this.deviceAddress = deviceAddress;
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		this.timeout.set(timeout);
		this.onConnectActions = onConnectActions;
	}

	public void setDeviceAddress(final String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public void enqueueAction(final BluetoothAction action) {
		if (shutdown.get()) {
			return;
		}

		synchronized (actionQueue) {
			actionQueue.add(action);
			actionQueue.notifyAll();
		}
	}

	public void enqueueActionRemovingRedundantEntries(final BluetoothAction action, final ActionComparator comparator) {
		if (shutdown.get()) {
			return;
		}

		synchronized (actionQueue) {
			while (!actionQueue.isEmpty() && (comparator.isRedundant(action, actionQueue.getLast()))) {
				actionQueue.removeLast();
			}
			actionQueue.add(action);
			actionQueue.notifyAll();
		}
	}

	public boolean isConnected() {
		synchronized (socketLock) {
			return socket != null;
		}
	}

	public void ensureConnected() {
		synchronized (socketLock) {
			if (socket == null) {
				enqueueAction(new EnsureConnectedBluetoothAction());
			} else {
				sendMessage(BLUETOOTH_CONNECTED);
			}
		}
	}

	void performDeviceConnect() {
		if (btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();
		closeSocket();

		sendMessage(BLUETOOTH_START_CONNECT);

		try {
			synchronized (socketLock) {
				if (BTCommThread.DEBUG_BLUETOOTH)
					Log.d(LOG_TAG, "Bluetooth device " + deviceAddress);
				final BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
				socket.connect();
				readerThread = new AsyncReaderThread(new InputStreamReader(socket.getInputStream()));
				readerThread.start();
				out = socket.getOutputStream();
			}

			sendMessage(BLUETOOTH_CONNECTED);

			if (onConnectActions != null) {
				for (final BluetoothAction action : onConnectActions) {
					action.performAction(handler, readerThread, out);
				}
			}
		} catch (final Exception e) {
			closeSocket();
			socket = null;
			if (BTCommThread.DEBUG_BLUETOOTH)
				Log.e(LOG_TAG, "Bluetooth " + deviceAddress + " Connection failed", e);
			sendMessage(BLUETOOTH_CONNECTION_ERROR);
		}
	}

	@Override
	public void run() {
		long lastActionPerformed = System.currentTimeMillis();
		try {
			if (BTCommThread.DEBUG_BLUETOOTH)
				Log.d(LOG_TAG, "BTCommThread thread started " + deviceAddress);

			while (!shutdown.get()) {
				BluetoothAction nextAction = null;
				synchronized (actionQueue) {
					if (!actionQueue.isEmpty()) {
						nextAction = actionQueue.removeFirst();
					} else {
						try {
							if (timeout.get() > 0) {
								actionQueue.wait(timeout.get());
							} else {
								actionQueue.wait();
							}
						} catch (final InterruptedException ie) {
							Thread.interrupted();
							shutdown.set(true);
						}
					}
				}

				synchronized (BTCommThread.class) {
					if (nextAction != null) {
						if (BTCommThread.DEBUG_BLUETOOTH)
							Log.i(LOG_TAG, "Performing action " + deviceAddress + ": " + nextAction.getClass());

						if (!isConnected()) {
							performDeviceConnect();
						}
						if (isConnected()) {
							try {
								nextAction.performAction(handler, readerThread, out);
							} catch (final IOException ioe) {
								if (BTCommThread.DEBUG_BLUETOOTH)
									Log.e(LOG_TAG,
											"Failed performing action " + deviceAddress + ": " + nextAction.getClass());
								closeSocket();
							}
						}
						lastActionPerformed = System.currentTimeMillis();
					} else if (timeout.get() > 0 && (lastActionPerformed + timeout.get() < System.currentTimeMillis())) {
						closeSocket();
					}
				}
			}
		} finally {
			if (BTCommThread.DEBUG_BLUETOOTH)
				Log.d(LOG_TAG, "BTCommThread thread shutdown " + deviceAddress);

			closeSocket();
		}
	}

	private void sendMessage(final int message) {
		synchronized (shutdown) {
			if (handler != null) {
				handler.obtainMessage(message).sendToTarget();
			}
		}
	}

	public void newActivity(final Handler newHandler, final long newTimeout) {
		synchronized (shutdown) {
			this.handler = newHandler;
		}
		updateTimeout(newTimeout);
	}

	public void updateTimeout(final long newTimeout) {
		this.timeout.set(newTimeout);
		synchronized (actionQueue) {
			actionQueue.notifyAll();
		}
	}

	public void shutdown() {
		shutdown.set(true);
		synchronized (actionQueue) {
			actionQueue.notifyAll();
		}
	}

	public boolean isShutdown() {
		return shutdown.get();
	}

	protected void closeSocket() {
		try {
			synchronized (socketLock) {
				if (readerThread != null) {
					readerThread.shutdown();
				}
				readerThread = null;
				out = null;
				if (socket != null) {
					if (DEBUG_BLUETOOTH)
						Log.i(LOG_TAG, "Closing bluetooth socket " + deviceAddress);

					socket.close();
				} else {
					if (DEBUG_BLUETOOTH)
						Log.d(LOG_TAG, "Bluetooth socket already closed " + deviceAddress);
				}
			}
			sendMessage(BLUETOOTH_CONNECTION_CLOSED);
		} catch (Exception e) {
			if (DEBUG_BLUETOOTH)
				Log.e(LOG_TAG, "Error closing bluetooth socket " + deviceAddress, e);
		} finally {
			socket = null;
		}
	}
}
package de.dralle.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.dralle.network.NetworkHelper;

public class UDPByteReceiver {
	private ArrayList<ByteDataReceivedCallback> bdrCallbacks;

	public int addCallback(ByteDataReceivedCallback callback) {
		bdrCallbacks.add(callback);
		return bdrCallbacks.size();
	}

	public boolean removeCallback(ByteDataReceivedCallback callback) {
		return bdrCallbacks.remove(callback);
	}

	/**
	 * Port to be used. Can be set using the parameterezized constructor.
	 */
	private int port;
	private volatile DatagramSocket server = null;
	private volatile boolean receiving = false;
	/**
	 * Timeout to be used by the receiving thread. Default is 5000ms
	 */
	private volatile int timeout = 5000;

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the receiving
	 */
	public boolean isReceiving() {
		return receiving;
	}

	public UDPByteReceiver() {
		bdrCallbacks = new ArrayList<>();
	}

	public UDPByteReceiver(int port) {
		this();
		if (port > 0) {
			this.port = port;
		}

	}

	public void notifyCallbacksOfNewData(String address, int port, byte[] data) {
		for (ByteDataReceivedCallback byteDataReceivedCallback : bdrCallbacks) {
			byteDataReceivedCallback.onByteDataReceived(address, port, data);
		}
	}

	public void notifyCallbacksOfActivation(int activePort) {
		for (ByteDataReceivedCallback byteDataReceivedCallback : bdrCallbacks) {
			byteDataReceivedCallback.onActivate(activePort);
		}
	}

	public void notifyCallbacksOfDeactivation(int port) {
		for (ByteDataReceivedCallback byteDataReceivedCallback : bdrCallbacks) {
			byteDataReceivedCallback.onDeactivate(port);
		}
	}

	public void notifyCallbacksOfReceiverStart(int activePort) {
		for (ByteDataReceivedCallback byteDataReceivedCallback : bdrCallbacks) {
			byteDataReceivedCallback.onReceivingStart(activePort);
		}
	}

	public void notifyCallbacksOfReceiverStop(int port) {
		for (ByteDataReceivedCallback byteDataReceivedCallback : bdrCallbacks) {
			byteDataReceivedCallback.onReceivingStop(port);
		}
	}

	/**
	 * Tries to create socket. Won´´t start the receiving thread
	 * 
	 * @return true if succesful
	 * @throws SocketException
	 *             Underlying Socketêxception if can´t create socket. Can happen
	 *             if port is already in use
	 */
	public boolean activate() throws SocketException {
		server = new DatagramSocket(port);
		notifyCallbacksOfActivation(port);
		return server != null;

	}

	/**
	 * Try to start the message receiver thread for this server
	 * 
	 * @return value indicates success
	 */
	public boolean startReceiving() {
		if (server != null) {
			Thread receive = new Thread(new Runnable() {

				@Override
				public void run() {
					receiverLoop();

				}

			});
			receive.start();
			return true;
		} else {
			return false;
		}

	}

	/**
	 * internal loop for receiver
	 */
	protected void receiverLoop() {
		if (server != null) {// check if server is available
			receiving = true;
			int packetSize = NetworkHelper.getInstance().getHighestMTU();
			try {
				server.setSoTimeout(timeout); // set timeout
			} catch (SocketException e) {
				receiving = false;
			}
			// enable broadcasting (not needed on the server, but good way to indicate that
			// broadcast receiving is on)
			try {
				if (!server.getBroadcast()) {
					server.setBroadcast(true);
				}
			} catch (SocketException e) {
				receiving = false;
			}
			if (receiving) {
				notifyCallbacksOfReceiverStart(port);
			}
			while (receiving) {
				boolean timeout = false;

				byte[] receiveBuffer = new byte[packetSize];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				try {
					server.receive(receivePacket);
				} catch (SocketTimeoutException ste) {
					timeout = true;
				} catch (IOException e) {
					receiving = false;
				}
				if (!timeout) {
					try {

						byte[] data = new byte[receivePacket.getLength()];
						for (int i = 0; i < receivePacket.getLength(); i++) {
							data[i] = receivePacket.getData()[receivePacket.getOffset() + i];
						}
						notifyCallbacksOfNewData(receivePacket.getAddress().getHostAddress(), receivePacket.getPort(),
								data);
					} catch (Exception e) {
						receiving = false;
					}

				}
			}
			notifyCallbacksOfReceiverStop(port);

		}

	}

	/**
	 * Stop the message receiver thread for this server
	 * 
	 * @return value indicates previous status
	 */
	public boolean stopReceiving() {
		boolean prev = receiving;
		receiving = false;
		return prev;

	}

	/**
	 * Deactivate this server
	 * 
	 * @return value indicates previous status
	 */
	public boolean deactivate() {
		stopReceiving();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {

		}
		server.close();
		notifyCallbacksOfDeactivation(port);
		boolean prev = server != null;
		server = null;
		return prev;

	}
}

package de.dralle.network.udp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import de.dralle.network.NetworkHelper;

/**
 * 
 * @author nils
 *
 */
public class UDPByteSender {
	/**
	 * List of all bound callbacks
	 */
	private ArrayList<ByteDataSentCallback> bdsCallbacks;
	/**
	 * Add a callback
	 * @param callback
	 */
	public void addByteDataSentCallback(ByteDataSentCallback callback) {
		bdsCallbacks.add(callback);
	}
	public boolean removeByteDataSentCallback(ByteDataSentCallback callback) {
		return bdsCallbacks.remove(callback);
	}
	private void notifyCallbacksOfSentFailure(String address, int port, byte[] data, Exception e) {
		for (ByteDataSentCallback b : bdsCallbacks) {
			b.byteDataSendFailed(address, port, data, e);
		}
	}
	private void notifyCallbacksOfSent(String address, int port, byte[] data) {
		for (ByteDataSentCallback b : bdsCallbacks) {
			b.byteDataSent(address, port, data);
		}
	}
	/**
	 * Default send timeout 500ms
	 */
	private int timeout = 500;

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

	public UDPByteSender() {
		bdsCallbacks=new ArrayList<>();
	}

	public boolean send(String host, int port, byte[] data) {
		InetAddress address = NetworkHelper.getInstance().resolveHostname(host);
		if (address != null) {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e) {
				notifyCallbacksOfSentFailure(address.getHostAddress(), port, data, e);
			}
			if (socket != null) {
				boolean setTimeoutFailed=false;
				try {
					socket.setSoTimeout(timeout);
				} catch (SocketException e) {
					setTimeoutFailed=true;
					notifyCallbacksOfSentFailure(address.getHostAddress(), port, data, e);
				}
				if(!setTimeoutFailed) {
						DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
					boolean send = false;
					try {
						socket.send(packet);
						send = true;
						notifyCallbacksOfSent(address.getHostAddress(), port, data);
					} catch (IOException e) {
						send = false;
						notifyCallbacksOfSentFailure(address.getHostAddress(), port, data, e);
					}
					socket.close();
					return send;
				}else {
					return false;
				}
				
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	public boolean sendLoopback(int port, byte[] data) {
		return send(NetworkHelper.getInstance().getLoopback().getHostAddress(), port, data);
	}

	public boolean sendLocalhost(int port, byte[] data) {
		return send(NetworkHelper.getInstance().getLocalhost().getHostAddress(), port, data);
	}

	public void sendBroadcast(int port, byte[] data) {
		List<InetAddress> broadcastAddresses=NetworkHelper.getInstance().getBroadcastAddresses();
		for (InetAddress inetAddress : broadcastAddresses) {
			send(inetAddress.getHostAddress(),port,data);
		}
	}
}

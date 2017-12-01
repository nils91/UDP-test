package de.dralle.network.udp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import de.dralle.network.NetworkHelper;

/**
 * 
 * @author nils
 *
 */
public class UDPByteSender {
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

	}

	public boolean send(String host, int port, byte[] data) {
		InetAddress address = NetworkHelper.getInstance().resolveHostname(host);
		if (address != null) {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e) {

			}
			if (socket != null) {
				try {
					socket.setSoTimeout(timeout);
				} catch (SocketException e) {

				}
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				boolean send = false;
				try {
					socket.send(packet);
					send = true;
				} catch (IOException e) {
					send = false;
				}
				socket.close();
				return send;
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

package de.dralle.network.udp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import de.dralle.network.NetworkHelper;

public class UDPByteReceiver {
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
	 * @param port the port to set
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
	 * @param timeout the timeout to set
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
		
	}

	public UDPByteReceiver(int port) {
		this();
		if (port > 0) {
			this.port = port;
		}

	}

	/**
	 * Tries to create socket. Won´´t start the receiving thread
	 * 
	 * @return true if succesful
	 * @throws SocketException
	 *             Underlying Socketêxception if can´t create socket. Can happen if port is already in use
	 */
	public boolean activate() throws SocketException {
		server = new DatagramSocket(port);
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
			int packetSize=NetworkHelper.getInstance().getHighestMTU();
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
			while (receiving) {
				boolean timeout = false;

				byte[] receiveBuffer = new byte[packetSize];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				try {
					server.receive(receivePacket);
				} catch (SocketTimeoutException ste) {
					System.out.println("Timeout and no data received");
					timeout = true;
				} catch (IOException e) {
					receiving = false;
				}
				if (!timeout) {
					System.out.println("Data received. "+receivePacket.getLength()+ " bytes");
					System.out.println(receivePacket.getSocketAddress());

				}
			}

		}

	}
	/**
	 * Stop the message receiver thread for this server
	 * 
	 * @return value indicates previous status
	 */
	public boolean stopReceiving() {
		boolean prev=receiving;
		receiving=false;
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
		boolean prev=server!=null;
		server.close();
		server=null;
		return prev;

	}
}

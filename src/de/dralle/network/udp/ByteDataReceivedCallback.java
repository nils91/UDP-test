/**
 * 
 */
package de.dralle.network.udp;

/**
 * @author Nils
 *
 */
public interface ByteDataReceivedCallback {
	public void onByteDataReceived(String address, int port, byte[] data);
	public void onActivate(int activePort);
	public void onDeactivate(int port);
	public void onReceivingStart(int receivingPort);
	public void onReceivingStop(int port);
}

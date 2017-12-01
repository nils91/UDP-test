/**
 * 
 */
package de.dralle.network.udp;

/**
 * @author Nils
 *
 */
public interface ByteDataSentCallback {
	public void onByteDataSent(String address, int port, byte[] data);
	public void onByteDataSendFailed(String address, int port, byte[] data, Exception e);
}

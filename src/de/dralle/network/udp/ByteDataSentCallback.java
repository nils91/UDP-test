/**
 * 
 */
package de.dralle.network.udp;

/**
 * @author Nils
 *
 */
public interface ByteDataSentCallback {
	public void byteDataSent(String address, int port, byte[] data);
	public void byteDataSendFailed(String address, int port, byte[] data, Exception e);
}

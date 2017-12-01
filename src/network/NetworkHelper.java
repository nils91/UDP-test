/**
 * 
 */
package network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * @author nils
 *
 */
public class NetworkHelper {
	public static NetworkHelper instance;
	/**
	 * @return the instance
	 */
	public static NetworkHelper getInstance() {
		if(instance==null) {
			instance=new NetworkHelper();
		}
		return instance;
	}
	private NetworkHelper() {
		
	}
	public Enumeration<NetworkInterface> getNetworkInterfaces() {
		Enumeration<NetworkInterface> en=Collections.emptyEnumeration();
		try {
			en = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			
		}
	    return en;
	}
	public List<NetworkInterface> getNetworkInterfacesAsList(){
		Enumeration<NetworkInterface> en=getNetworkInterfaces();
		List<NetworkInterface> enList=new ArrayList();
		while(en.hasMoreElements()) {
			enList.add(en.nextElement());
			
		}
		return enList;
	}
	public int countNetworkInterfaces() {
		
		return getNetworkInterfacesAsList().size();
	}
	public List<String> getInterfaceNames(){
		List<NetworkInterface> enList=getNetworkInterfacesAsList();
		List<String> enNames=new ArrayList<>();
		for (NetworkInterface en : enList) {
			enNames.add(en.getDisplayName());
		}
		return enNames;
	}
	public InetAddress getLoopback() {
		InetAddress address=null;
			address=InetAddress.getLoopbackAddress();
		return address;
	}
	public InetAddress getLocalhost() {
		InetAddress address=null;
		try {
			address=InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			
		}
		return address;
	}
	public String getHostname() {
		try {
			return getLocalhost().getHostName();
		}catch(Exception e) {
			return null;
		}
		
	}
	public List<InetAddress> getAllAddresses(){
		List<NetworkInterface> enList=getNetworkInterfacesAsList();
		List<InetAddress> enAdresses=new ArrayList<>();
		for (NetworkInterface en : enList) {
			Enumeration<InetAddress> addresses = en.getInetAddresses();
			while(addresses.hasMoreElements()) {
				enAdresses.add(addresses.nextElement());
			}
		}
		return enAdresses;
	}
	public List<InetAddress> getBroadcastAddresses(){
		List<NetworkInterface> enList=getNetworkInterfacesAsList();
		List<InetAddress> enAdresses=new ArrayList<>();
		for (NetworkInterface en : enList) {
			List<InterfaceAddress> addresses = en.getInterfaceAddresses();
			for (InterfaceAddress interfaceAddress : addresses) {
				InetAddress bc=interfaceAddress.getBroadcast();
				if(bc!=null) {
					enAdresses.add(interfaceAddress.getBroadcast());
				}
				
			}
		}
		return enAdresses;
	}
	public int getHighestMTU() {
		int mtu=-1;
		List<NetworkInterface> enList=getNetworkInterfacesAsList();
		
		for (NetworkInterface en : enList) {
			try {
				if(en.getMTU()>mtu) {
					mtu=en.getMTU();
				}
			} catch (SocketException e) {
				
			}
		}
		return mtu;
	}
	public int getHighestNonLoopbackMTU() {
		int mtu=-1;
		List<NetworkInterface> enList=getNetworkInterfacesAsList();
		
		for (NetworkInterface en : enList) {
			try {
				if(!en.isLoopback()&&en.getMTU()>mtu) {
					mtu=en.getMTU();
				}
			} catch (SocketException e) {
				
			}
		}
		return mtu;
	}
	public InetAddress resolveHostname(String host) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			
		}
		return address;
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import network.NetworkHelper;

public class Main {
	static UDPByteReceiver server;
	public static void main(String[] args) {
		server=new UDPByteReceiver();
		System.out.println("UDP Test");
		System.out.println("---------------");
		while (true) {
			printMainMenu();
		}

	}

	private static void printMainMenu() {
		String[] items = new String[] { "1: Read Hostname", "2: Read Loopback", "3: Read Localhost address",
				"4: List Network interfaces", "5: Get highest MTU", "6: List this hosts ip addresses",
				"7: List broadcast ip addresses", "8: Set server port", "9: Get server port", "10: Activate Server",
				"11: Start receiving server thread", "12: Stop receiving server thread", "13: Deactivate server",
				"14: Send data", "15: Send data to localhost", "16: Send data to the loopback address",
				"17: Broadcast data" };
		for (String string : items) {
			System.out.println(string);
		}
		System.out.println("--------");
		System.out.print(" >> ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		try {
			input = br.readLine();
		} catch (IOException e) {

		}
		switch (input) {
		case "1":
			System.out.println("Hostname: " + NetworkHelper.getInstance().getHostname());
			break;
		case "2":
			System.out.println("Loopback address: " + NetworkHelper.getInstance().getLoopback().getHostAddress());
			break;
		case "3":
			System.out.println("Localhost address: " + NetworkHelper.getInstance().getLocalhost().getHostAddress());
			break;
		case "4":
			System.out.println("Network interfaces: " + NetworkHelper.getInstance().countNetworkInterfaces());
			for (String string : NetworkHelper.getInstance().getInterfaceNames()) {
				System.out.println(string);

			}
			break;
		case "5":
			System.out.println("Highest MTU: " + NetworkHelper.getInstance().getHighestMTU());
			System.out.println(
					"Highest MTU (excluding loopback): " + NetworkHelper.getInstance().getHighestNonLoopbackMTU());

			break;
		case "6":
			System.out.println("IP addresses");
			for (InetAddress add : NetworkHelper.getInstance().getAllAddresses()) {
				System.out.println(add.getHostAddress());
			}
			break;
		case "7":
			System.out.println("Broadcast IP addresses");
			for (InetAddress add : NetworkHelper.getInstance().getBroadcastAddresses()) {
				System.out.println(add.getHostAddress());
			}
			break;
		case "8":
			System.out.print("Enter port > ");
			input = "";
			try {
				input = br.readLine();
				server.setPort(Integer.parseInt(input));
			} catch (Exception e) {
				System.out.println("Failed: "+e.getMessage());
			}
			
			break;
		case "9":
			System.out.println("Server port: "+server.getPort());
			break;
		case "10":
			try {
				if(server.activate()) {
					System.out.println("Server activated");
				}else {
					System.out.println("Server could not be activated.");
				}
				
			} catch (SocketException e) {
				System.out.println("Server could not be activated: "+e.getMessage());
			}
			break;
		case "11":
			if(server.startReceiving()) {
				System.out.println("Message receiving server thread started.");
			}else {
				System.out.println("Message receiving server thread could not be started.");
			}
			break;
		case "12":
			server.stopReceiving();
			System.out.println("Stopped.");
			break;
		case "13":
			server.deactivate();
			System.out.println("Deactivated.");
			break;
		case "14":
			System.out.print("Enter data > ");
			input = "";
			try {
				input = br.readLine();
				
			} catch (Exception e) {
				System.out.println("Failed: "+e.getMessage());
			}
			if(input!="") {
				System.out.print("Enter host > ");
				String host = "";
				InetAddress hostAddress=null;
				try {
					host = br.readLine();
					hostAddress=NetworkHelper.getInstance().resolveHostname(host);
				} catch (Exception e) {
					System.out.println("Failed: "+e.getMessage());
				}
				if(hostAddress!=null) {
					System.out.print("Enter port > ");
					String portString = "";
					int port=0;
					try {
						portString = br.readLine();
						port=Integer.parseInt(portString);
					} catch (Exception e) {
						System.out.println("Failed: "+e.getMessage());
					}
					if(port>0) {
						UDPByteSender client=new UDPByteSender();
						if(client.send(hostAddress.getHostAddress(), port, input.getBytes())) {
							System.out.println("Message "+input+" sent to "+hostAddress.getHostAddress()+" UDP port "+port);
						}else {
							System.out.println("Message "+input+" could not be sent to "+hostAddress.getHostAddress()+" UDP port "+port);
							
						}
					}
				}
			}
			break;
		case "15":

			System.out.print("Enter data > ");
			input = "";
			try {
				input = br.readLine();
				
			} catch (Exception e) {
				System.out.println("Failed: "+e.getMessage());
			}
			if(input!="") {
					System.out.print("Enter port > ");
					String portString = "";
					int port=0;
					try {
						portString = br.readLine();
						port=Integer.parseInt(portString);
					} catch (Exception e) {
						System.out.println("Failed: "+e.getMessage());
					}
					if(port>0) {
						UDPByteSender client=new UDPByteSender();
						if(client.sendLocalhost(port, input.getBytes())) {
							System.out.println("Message "+input+" sent to UDP port "+port+" on localhost");
						}else {
							System.out.println("Message "+input+" could not be sent");
							
						}
					}
				
			}
			break;
		case "16":
			System.out.print("Enter data > ");
			input = "";
			try {
				input = br.readLine();
				
			} catch (Exception e) {
				System.out.println("Failed: "+e.getMessage());
			}
			if(input!="") {
					System.out.print("Enter port > ");
					String portString = "";
					int port=0;
					try {
						portString = br.readLine();
						port=Integer.parseInt(portString);
					} catch (Exception e) {
						System.out.println("Failed: "+e.getMessage());
					}
					if(port>0) {
						UDPByteSender client=new UDPByteSender();
						if(client.sendLoopback(port, input.getBytes())) {
							System.out.println("Message "+input+" sent to UDP port "+port+" on loopback");
						}else {
							System.out.println("Message "+input+" could not be sent");
							
						}
					}
				
			}
			break;
		case "17":
			System.out.print("Enter data > ");
			input = "";
			try {
				input = br.readLine();
				
			} catch (Exception e) {
				System.out.println("Failed: "+e.getMessage());
			}
			if(input!="") {
					System.out.print("Enter port > ");
					String portString = "";
					int port=0;
					try {
						portString = br.readLine();
						port=Integer.parseInt(portString);
					} catch (Exception e) {
						System.out.println("Failed: "+e.getMessage());
					}
					if(port>0) {
						UDPByteSender client=new UDPByteSender();
						client.sendBroadcast(port, input.getBytes());
					}
				
			}
			break;
		default:
			System.out.println("Item " + input + " not found.");
			break;
		}
		System.out.println("--------");

	}

	public static void oldmain() {
		NetworkHelper nh = NetworkHelper.getInstance();
		System.out.println("Hostname: " + nh.getHostname());
		System.out.println("Loopback address: " + nh.getLoopback().getHostAddress());
		System.out.println("Localhost address: " + nh.getLocalhost().getHostAddress());
		System.out.println("Network interfaces: " + nh.countNetworkInterfaces());
		for (String string : nh.getInterfaceNames()) {
			System.out.println(string);

		}
		System.out.println("Highest MTU: " + nh.getHighestMTU());
		System.out.println("Highest MTU (excluding loopback): " + nh.getHighestNonLoopbackMTU());
		System.out.println("IP addresses");
		for (InetAddress add : nh.getAllAddresses()) {
			System.out.println(add.getHostAddress());
		}
		System.out.println("Broadcast IP addresses");
		for (InetAddress add : nh.getBroadcastAddresses()) {
			System.out.println(add.getHostAddress());
		}
		int port = 3112;
		UDPByteReceiver server = new UDPByteReceiver();
		server.setPort(port);
		boolean exception = false;
		do {
			try {
				server.activate();
				exception = false;
			} catch (SocketException e) {
				port++;
				exception = true;
			}
		} while (exception);
		System.out.println("Server activated on port " + port);
		server.startReceiving();
		System.out.println("Message receiver thread started");
		UDPByteSender client = new UDPByteSender();
		byte[] data = new byte[] { 1, 2, 3, 4 };
		client.sendLocalhost(port, data);
		System.out.println("Data sent to localhost");
		client.sendLoopback(port, data);
		System.out.println("Data sent to loopback address");
		client.sendBroadcast(port, data);
		System.out.println("Data broadcasted");

	}

}

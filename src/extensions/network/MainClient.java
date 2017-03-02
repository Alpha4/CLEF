package extensions.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import framework.plugin.IMessage;
import framework.plugin.INetwork;

public class MainClient {

	/*
	 * FOR TESTING PURPOSES ONLY 
	 */
	
	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub

		INetwork net = Network.getInstance();

		IMessage m = new Message();
		m.setAuthor("Auré");
		m.setPlainText("super tealjbfiajfakpofjaioùhzfiugaiuhpst");

		IMessage m2 = new Message();
		m2.setAuthor("Machin");
		m2.setPlainText("super test");

		net.send(m, InetAddress.getByName("localhost"));
		net.send(m2, InetAddress.getByName("localhost"));

	}

}

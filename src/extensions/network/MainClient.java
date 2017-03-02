package extensions.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import framework.plugin.INetwork;

public class MainClient {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		
		INetwork net = Network.getInstance();
		
		Message m = new Message();
		m.setAuthor("Auré");
		m.setPlainText("super tealjbfiajfakpofjaioùhzfiugaiuhpst");
		
		Message m2 = new Message();
		m2.setAuthor("Machin");
		m2.setPlainText("super test");
		
		
		net.send(m, InetAddress.getByName("localhost"));
		net.send(m2, InetAddress.getByName("localhost"));
		
	}

}

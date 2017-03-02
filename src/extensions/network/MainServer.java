package extensions.network;

import java.io.IOException;
import java.net.UnknownHostException;

import framework.plugin.IMessage;
import framework.plugin.INetwork;

public class MainServer {

	public static void main(String[] args) {
		INetwork net = Network.getInstance();

		while(true) {
			IMessage m = null;
			try {
				m = net.receive();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(m.getAuthor()+"/"+m.getPlainText());
		}
	}
}

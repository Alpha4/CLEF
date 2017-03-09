package extensions.networkClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import extensions.ihm.IGUI;
import extensions.ihm.SimpleGUI;
import extensions.message.Message;
import extensions.network.Network;
import framework.plugin.IMessage;
import framework.plugin.INetwork;
import framework.plugin.INetworkClient;

public class NetworkClient implements INetworkClient {
	
	// Appeler le Framework.getExtension("INetwork")
	private INetwork network = Network.getInstance();
	private String clientName;
	private InetAddress server;
	private IMessage m = null;
	// Appeler le Framework.getExtension("IGUI")
	//private IGUI gui = new SimpleGUI(); 

	public void send(String message) {
		// Appeler le Framework.getExtension("IMessage")
		IMessage m = new Message();
		m.setAddress(server);
		m.setAuthor(clientName);
		m.setPlainText(message);
		network.send(m, server);
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public InetAddress getServer() {
		return server;
	}

	public void setServer(String server) {
		try {
			this.server = InetAddress.getByName(server);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void receive() {
		try {
			m = network.receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		Thread t = new Thread() {
			public void run () {
				while(true) {
					receive();
					//gui.receiveMessage(m);
					System.out.println(m.getPlainText());
				}
			}
		};
		t.start();
		
		setServer("localhost");
		setClientName("Toto");
		send("Salut! Comment ça va?");
	}
}

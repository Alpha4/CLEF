package extensions.network;

import java.io.IOException;
import java.net.InetAddress;

import framework.plugin.IMessage;
import framework.plugin.INetwork;
import framework.plugin.INetworkClient;

public class NetworkClient implements INetworkClient {

	private INetwork network = Network.getInstance();
	private String clientName;
	private InetAddress server;
	private IMessage m = null;

	public void send(String message) {
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

	public void setServer(InetAddress server) {
		this.server = server;
	}

	public void receive() {
		try {
			m = network.receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getAuthor() {
		return m.getAuthor();
	}

	public String getMessage() {
		return m.getPlainText();
	}
}

package extensions.networkServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import extensions.network.Network;
import framework.plugin.IMessage;
import framework.plugin.INetwork;
import framework.plugin.INetworkServer;

public class NetworkServer implements INetworkServer {

	private List<Client> clients;
	
	// Appeler le Framework.getExtension("INetwork")
	private INetwork network = Network.getInstance();
	
	public NetworkServer() {
		clients = new ArrayList<Client>();
	}

	private void messageReceived(IMessage message) {
		Client c = new Client(message.getPort(), message.getAddress());
		
		if (!clients.contains(c))
			clients.add(c);
		
		for (Client client : clients) {
			System.out.println(client.getAddress());
			// "Can't assign requested address" on the next line, don't know why
			network.send(message, client.getAddress(),client.getPort());
		}
	}

	public void run() {
		System.out.println("Network Server lancé sur le port : "+((Network) network).getReceivePort());
		while (true) {
			IMessage message = null;
			try {
				message = network.receive();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageReceived(message);
		}
	}
}

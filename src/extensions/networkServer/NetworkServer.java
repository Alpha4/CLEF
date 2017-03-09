package extensions.networkServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import extensions.network.Network;
import framework.plugin.IMessage;
import framework.plugin.INetwork;
import framework.plugin.INetworkServer;

public class NetworkServer implements INetworkServer {

	private List<InetAddress> clients;
	// Appeler le Framework.getExtension("INetwork")
	private INetwork network = Network.getInstance();

	private void messageReceived(IMessage message) {
		if (!clients.contains(message.getAddress()))
			clients.add(message.getAddress());

		for (InetAddress client : clients) {
			network.send(message, client);
		}
	}

	public void run() {
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

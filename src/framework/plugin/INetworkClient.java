package framework.plugin;

import java.net.InetAddress;

public interface INetworkClient {

	void send(String message);

	String getClientName();

	void setClientName(String clientName);

	InetAddress getServer();

	void setServer(InetAddress server);

	void run();
}
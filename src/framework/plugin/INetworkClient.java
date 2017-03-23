package framework.plugin;

import java.net.InetAddress;

import framework.IExtension;

public interface INetworkClient extends IExtension {

	void send(String message);

	String getClientName();

	void setClientName(String clientName);

	void setServer(String server);

	void run();
}
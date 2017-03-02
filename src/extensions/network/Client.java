package extensions.network;

import java.net.InetAddress;

import framework.plugin.IClient;

public class Client implements IClient {
	private String name;
	private InetAddress address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
}

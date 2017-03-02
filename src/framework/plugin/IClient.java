package framework.plugin;

import java.net.InetAddress;

public interface IClient {

	String getName();

	void setName(String name);

	InetAddress getAddress();

	void setAddress(InetAddress address);

}
package framework.plugin;

import java.io.IOException;
import java.net.InetAddress;

public interface INetwork {

	IMessage receive() throws IOException;

	void send(IMessage m, InetAddress address);

}
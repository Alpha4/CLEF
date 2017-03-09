package framework.plugin;

import java.io.IOException;
import java.net.InetAddress;

import framework.IExtension;

public interface INetwork extends IExtension {

	IMessage receive() throws IOException;

	void send(IMessage m, InetAddress address);

}
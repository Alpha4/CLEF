package framework.plugin;

import java.net.InetAddress;

import framework.IExtension;

public interface IMessage extends IExtension{

	String getAuthor();

	void setAuthor(String author);

	String getPlainText();

	void setPlainText(String plainText);

	InetAddress getAddress();

	void setAddress(InetAddress address);
	
	int getPort();

	void setPort(int port);

}
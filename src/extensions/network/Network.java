package extensions.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import framework.plugin.IMessage;
import framework.plugin.INetwork;

public class Network implements INetwork {

	private static INetwork onlyInstance = new Network();
	private DatagramSocket sendingSocket;
	private DatagramSocket receivingSocket;
	private final int port = 3682; // FIX ME : magic port
	private static final int size = 1024; // FIX ME : magic size
	static byte[] buffer = new byte[size];

	private Network() {
		try {
			sendingSocket = new DatagramSocket();
			receivingSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static INetwork getInstance() {
		// TODO Auto-generated method stub
		return onlyInstance;
	}

	public IMessage receive() throws IOException {
		DatagramPacket in = new DatagramPacket(buffer, buffer.length);
		receivingSocket.receive(in);
		IMessage m = new Message();
		String message = new String(in.getData());
		String[] strings = message.split("/");
		m.setAuthor(strings[0]);
		m.setPlainText(strings[1]);
		m.setAddress(in.getAddress());
		buffer = new byte[size];

		return m;
	}

	public void send(IMessage m, InetAddress address) {
		String message = m.getAuthor() + "/" + m.getPlainText();
		DatagramPacket out = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
		try {
			sendingSocket.send(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

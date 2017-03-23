package extensions.networkClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import framework.plugin.INetworkClient;

public class NetworkClient implements INetworkClient {
	
	public static final int PORT = 1337;
	
	private Socket socket;
	private Scanner input;
	private PrintWriter output;
	
	public NetworkClient() throws IOException {
		socket = new Socket("localhost",PORT);
		input = new Scanner(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(),true);
	}

	public void send(String message) {
		output.println(message);
	}
	
	public String read() throws IOException {
		return input.nextLine();
	}

	@Override
	public String getClientName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClientName(String clientName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InetAddress getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServer(String server) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		
		send("SALUT!");
		
		while(true) {
			
		}
		
	}
	
}

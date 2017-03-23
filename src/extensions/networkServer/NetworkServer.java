package extensions.networkServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import framework.plugin.INetworkServer;

public class NetworkServer implements INetworkServer {
	
	public static final int PORT = 1337;
	
	private ServerSocket serverSocket;
	private List<ClientThread> clientThreads = new ArrayList<ClientThread>();
	
	public void connect(Socket socket) {
		try {
			clientThreads.add(new ClientThread(socket));
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void disconnect(ClientThread client) {
		Iterator<ClientThread> itr = clientThreads.iterator();
		while(itr.hasNext()) {
			if (itr.next().equals(client))
				itr.remove();
			break;
		}
	}
	
	public void broadcast(ClientThread activeClient, String message) {
		for(ClientThread client: clientThreads) {
			if (!client.equals(activeClient)) {
				client.sendMessage(message);
			}
		}
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server is listening on "+PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				connect(socket);
			}
		} catch (IOException e) {
			
		} finally {
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private class ClientThread extends Thread {
		
		private Socket clientSocket;
		private BufferedReader input;
		private PrintWriter output;

		public ClientThread(Socket socket) throws IOException {
			this.clientSocket = socket;
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new PrintWriter(clientSocket.getOutputStream(),true);
			start();
		}
		
		public void readMessage(String message) {
			broadcast(this, message);
		}

		public void sendMessage(String message) {
			output.println(message);
		}
		
		public void close() {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
				}
				disconnect(this);
			} catch(IOException e) {
				System.out.println(e.getMessage());
			}
		}
		
		public void run() {
			String message;
			try {
				while(true) {
					message = input.readLine();
					System.out.println(message);
					if (message == null) {
						System.out.println("close");
						close();
						break;
					}
					readMessage(message);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} finally {
				close();
			}
		}
		
	}
	
}

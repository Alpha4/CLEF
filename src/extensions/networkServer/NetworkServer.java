package extensions.networkServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.List;

import framework.Framework;
import interfaces.INetworkServer;

import java.util.ArrayList;
import java.util.Iterator;

public class NetworkServer implements INetworkServer {
	
	private boolean isStarted = false;
	private int port;
	
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
	
	public void setPort(int port){
		this.port = port;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void stopServer() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			Framework.event("network.server.disconnected", null);
			isStarted = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() {
		Thread thread = new Thread() {
			public void run () {

				try {
					serverSocket = new ServerSocket(getPort());
					System.out.println("Server is listening on "+getPort());
					isStarted = true;
					Framework.event("network.server.connected", null);
					while (!serverSocket.isClosed()) {
						Socket socket = serverSocket.accept();
						connect(socket);
					}
				} catch (BindException e) {
					Framework.event("network.server.failed", "Failed to create server. Check IP and port.");
				} catch (SocketException e) {
					// Small fix because of the exception thrown at line "serverSocket.accept()" when socket is closed
				} catch (IOException e) {
					Framework.event("network.server.failed", "Unknown server error ("+e.getClass()+": "+e.getMessage()+")");
				} finally {
					try {
						if (serverSocket != null)
							serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		thread.start();
	}
	
	public boolean isStarted() {
		return isStarted;
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
		
		@Override
		public void run() {
			String message;
			try {
				while(true) {
					message = input.readLine();
					if (message == null) {
						close();
						break;
					}
					//System.out.println(message);
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

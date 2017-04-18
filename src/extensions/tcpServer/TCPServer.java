package extensions.tcpServer;

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

/**
 * Implémentation TCP d'un serveur de chat
 */
public class TCPServer implements INetworkServer {
	
	private boolean isStarted = false;
	private int port;
	
	private ServerSocket serverSocket;
	private List<ClientThread> clientThreads = new ArrayList<ClientThread>();
	
	// Public functions
	
	@Override
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
	
	@Override
	public void stopServer() {
		try {
			broadcast(null, "close");
			if (serverSocket != null)
				serverSocket.close();
			Framework.event("network.server.disconnected", null);
			isStarted = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		stopServer();
	}

	@Override
	public void setPort(int port){
		this.port = port;
	}
	
	@Override
	public boolean isStarted() {
		return isStarted;
	}
	
	// Private functions
	
	/**
	 * Récupère le port du serveur
	 * 
	 * @return port du serveur
	 */
	private int getPort(){
		return this.port;
	}
	
	/**
	 * Créer un nouveau thread client
	 * 
	 * @param socket socket du client
	 */
	private void connect(Socket socket) {
		try {
			clientThreads.add(new ClientThread(socket));
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Déconnecte un client
	 * 
	 * @param client le client à déconnecter
	 */
	private void disconnect(ClientThread client) {
		Iterator<ClientThread> itr = clientThreads.iterator();
		while(itr.hasNext()) {
			if (itr.next().equals(client))
				itr.remove();
			break;
		}
	}
	
	/**
	 * Broadcast un message à tous les clients
	 * 
	 * <p>
	 * 
	 * Pour éviter les doublons, il est possible de donner en paramètre
	 * le client qui vient d'envoyer le message
	 * 
	 * @param activeClient 	client qui ne doit pas recevoir le message
	 * @param message		le message à envoyer
	 */
	private void broadcast(ClientThread activeClient, String message) {
		for (int i = 0; i < clientThreads.size(); i++) {
			ClientThread client = clientThreads.get(i);
			if (activeClient == null || !client.equals(activeClient)) {
				client.sendMessage(message);
			}
		}
	}
	
	/**
	 * Thread d'un client
	 */
	private class ClientThread extends Thread {
		
		private Socket clientSocket;
		private BufferedReader input;
		private PrintWriter output;

		/**
		 * Constructeur par défaut
		 * 
		 * @param socket		Socket du client
		 * @throws IOException
		 */
		public ClientThread(Socket socket) throws IOException {
			this.clientSocket = socket;
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new PrintWriter(clientSocket.getOutputStream(),true);
			start();
		}
		
		/**
		 * Action effectuée quand un message est reçu
		 * 
		 * @param message le message reçu
		 */
		public void readMessage(String message) {
			broadcast(this, message);
		}

		/**
		 * Permet d'envoyer un message au client
		 * 
		 * @param message le message à envoyer
		 */
		public void sendMessage(String message) {
			output.println(message);
		}
		
		/**
		 * Déconnecte le client
		 */
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

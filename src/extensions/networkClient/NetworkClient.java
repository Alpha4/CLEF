package extensions.networkClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import framework.Framework;
import interfaces.INetworkClient;

public class NetworkClient implements INetworkClient {
	
	private boolean isConnected;
	private String pseudo;
	private String address;
	private int port;
	
	private Socket socket;
	private Scanner input;
	private PrintWriter output;
	
	private Thread thread;
	
	// Public methods

	public void start() {
		pseudo = "Pseudo";
		address = "localhost";
		port = 1337;
	}
	
	public void connect() {
		try {
			socket = new Socket(address,getPort());
			input = new Scanner(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(),true);
			
			thread = new Thread() {
				public void run () {
					if (input != null) {
						while(input.hasNextLine()) {
							String message = input.nextLine();
							Framework.event("message.received",message);
						}
					}
				}
			};
			thread.start();
			isConnected = true;
			Framework.event("network.client.connected", null);
			
		} catch (ConnectException e) {
			Framework.event("network.client.failed", "Connection refused. Check IP and port.");
		} catch (IOException e) {
			Framework.event("network.client.failed", "Unknown client error ("+e.getClass()+": "+e.getMessage()+")");
		}
		
	}
	
	public void send(String message) {
		output.println(message);
	}
	
	public void disconnect() {
		try {
			if (socket != null) {
				socket.close();
				input.close();
				output.close();
			}
			thread.interrupt();
			isConnected = false;
			Framework.event("network.client.disconnected", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		disconnect();
	}
	
	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getPort(){
		return port;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
}

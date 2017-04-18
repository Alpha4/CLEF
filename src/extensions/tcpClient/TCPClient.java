package extensions.tcpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import framework.Framework;
import interfaces.INetworkClient;

/**
 * Implémentation TCP d'un client de chat
 */
public class TCPClient implements INetworkClient {
	
	private boolean isConnected;
	private String pseudo;
	private String address;
	private int port;
	
	private Socket socket;
	private Scanner input;
	private PrintWriter output;
	
	private Thread thread;

	@Override
	public void start() {
		pseudo = "Pseudo";
		address = "localhost";
		port = 1337;
	}
	
	@Override
	public void connect() {
		try {
			socket = new Socket(address,getPort());
			input = new Scanner(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(),true);
			
			thread = new Thread() {
				public void run () {
					if (input != null) {
						try {
							while(input.hasNextLine()) {
								String message = input.nextLine();
								if (message.equals("close")) {
									disconnect();
								} else {
									Framework.event("message.received",message);
								}
							}
						} catch (IllegalStateException e) {
							// Small fix because of the exception thrown at line "input.hasNextLine()" when socket is closed
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
	
	@Override
	public void send(String message) {
		output.println(message);
		Framework.event("message.sent",message);
	}
	
	@Override
	public void disconnect() {
		try {
			if (socket != null) {
				socket.close();
				input.close();
				output.close();
			}
			if (thread != null) {
				thread.interrupt();
			}
			if (isConnected) {
				isConnected = false;
				Framework.event("network.client.disconnected", null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		disconnect();
	}
	
	@Override
	public String getPseudo() {
		return pseudo;
	}

	@Override
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	@Override
	public String getAddress() {
		return address;
	}
	
	@Override
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public int getPort(){
		return port;
	}
	
	@Override
	public void setPort(int port){
		this.port = port;
	}
	
	@Override
	public boolean isConnected() {
		return isConnected;
	}
	
}

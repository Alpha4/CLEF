package extensions.ihmconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import framework.Framework;
import framework.plugin.IGUI;
import framework.plugin.INetworkClient;

public class ConsoleGUI implements IGUI{


	// Connection info
	private String hostIP;
	private int connectionStatus;
	private int port;
	private boolean isHost;
	private String pseudo;
	
	

	private INetworkClient inetwork;
	
	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public int getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public INetworkClient getInetwork() {
		return inetwork;
	}

	public void setInetwork(INetworkClient inetwork) {
		this.inetwork = inetwork;
	}

	public ConsoleGUI() {
		inetwork = (INetworkClient) Framework.getExtension(INetworkClient.class);
		hostIP = "localhost";
		port = 1337;
		pseudo = "Client";

	}
	
	@Override
	public void run() {
		initGUI();
		Framework.handleEvent("message.received", this);
		
	}

	@Override
	public void receiveMessage(String m) {
		//comme les messages sont en pseudo : message
		System.out.println(m);
		
	}
	
	

	@Override
	public void initGUI() {
		inetwork = (INetworkClient) Framework.getExtension(INetworkClient.class);
		inetwork.setPort(port);
		inetwork.setServer(hostIP);
		//on reçoit pseudo : message
		System.out.println("Bienvenue sur CLEF, une application extensible");
		System.out.println("Affichage en console du chat");
		
		
		
	}

	@Override
	public void handleEvent(String name, Object event) {
		if (name == "message.received") {
			this.receiveMessage((String)event);
		}
	}

}
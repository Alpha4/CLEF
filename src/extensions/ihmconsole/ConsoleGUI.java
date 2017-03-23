package extensions.ihmconsole;

import framework.plugin.IGUI;
import framework.plugin.IMessage;
import framework.plugin.INetworkClient;

public class ConsoleGUI implements IGUI{


	// Connection info
	private String hostIP;
	private int connectionStatus;
	private int port;
	private boolean isHost;
	private String pseudo;
	
	

	private INetworkClient inetwork;
	
	
	
	@Override
	public void run() {
		initGUI();
		
	}

	@Override
	public void receiveMessage(IMessage m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveRoomList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedToServResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnectedFromServer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initGUI() {
		System.out.println("Bienvenue sur CLEF, une application extensible");
		
		
	}

}

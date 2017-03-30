package extensions.ihmconsole;

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
	
	@Override
	public void run() {
		initGUI();
		
	}

	@Override
	public void receiveMessage(String m) {
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
		inetwork = (INetworkClient) Framework.getExtension(INetworkClient.class);
		
		System.out.println("Bienvenue sur CLEF, une application extensible");
		
	}

}

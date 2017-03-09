package extensions.ihm;

import framework.plugin.IMessage;

public interface IGUI {
	
		
	void receiveMessage(IMessage m);
	public void receiveRoomList();
	public void connectedToServResponse();
	public void disconnectedFromServer();
	public void initGUI();
	
	

}
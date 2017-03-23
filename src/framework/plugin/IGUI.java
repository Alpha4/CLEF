package framework.plugin;

import framework.IExtension;

public interface IGUI extends IExtension{
	
		
	void receiveMessage(IMessage m);
	public void receiveRoomList();
	public void connectedToServResponse();
	public void disconnectedFromServer();
	public void initGUI();
	
	

}
package framework.plugin;

import framework.IExtension;

public interface IGUI extends IExtension{
	
		
	void receiveMessage(String m);
	public void receiveRoomList();
	public void connectedToServResponse();
	public void disconnectedFromServer();
	public void initGUI();
	
	

}
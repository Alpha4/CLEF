package framework.plugin;

import framework.IExtension;

public interface IGUI extends IExtension{
	
	/*
	 * Reçoit le message
	 * @param m : le message
	 */
	void receiveMessage(String m);
	
	/*
	 * Reçoit la list des rooms
	 */
	public void receiveRoomList();
	
	/*
	 * Connexion au serveur
	 */
	public void connectedToServResponse();
	
	/*
	 * Deconnexion du serveur
	 */
	public void disconnectedFromServer();
	
	/*
	 * Initialisation
	 */
	public void initGUI();
	
	

}

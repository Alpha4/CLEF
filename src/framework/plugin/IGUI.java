package framework.plugin;

import framework.IExtension;

public interface IGUI extends IExtension{
	
	/*
	 * Reçoit le message
	 * @param m : le message
	 */
	void receiveMessage(String m);
	
	
	/*
	 * Initialisation
	 */
	public void initGUI();
	
	

}

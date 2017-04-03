package framework.plugin;

import framework.IExtension;

/**
 * Interface définissant l'interface graphique
 */
public interface IGUI extends IExtension{
	
	/**
	 * Reçoit le message
	 * @param m : le message
	 */
	void receiveMessage(String m);
	
	
	/**
	 * Initialisation
	 */
    void initGUI();
	
	

}

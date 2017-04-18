package interfaces;

import framework.IExtension;

/**
 *Interface du serveur
 */
public interface INetworkServer extends IExtension {

	/**
	 * définir le port du serveur
	 * @param port le port du serveur
	 */
	void setPort(int port);
	
	/**
	 * Démarre le serveur
	 */
	void startServer();
	
	/**
	 * Arrête le serveur
	 */
	void stopServer();
	
	/**
	 * Permet de savoir si le serveur est lancé
	 * 
	 * @return Vrai si le serveur est lancé, faux sinon
	 */
	boolean isStarted();

}
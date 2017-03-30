package framework.plugin;

import framework.IExtension;
/**
*Interface du serveur
*/
public interface INetworkServer extends IExtension {
/**
*methode appelée lors du lancement du serveur
*/
	void run();
/**
 * définir le port du serveur
 * @param port
 */
	void setPort(int port);
/**
 * méthode qui ferme le server socket
 */
	public void stopServer();

}
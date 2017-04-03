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
 * @param port le port du serveur
 */
	void setPort(int port);
/**
 * méthode qui ferme le server socket
 */
void stopServer();

}
package framework.plugin;

import java.net.InetAddress;

import framework.IExtension;

/**
*interface pour extension chat coté client
*/
public interface INetworkClient extends IExtension {
/**
*envoyer message au serveur
*/
	void send(String message);
/**
*retourne nom du client
*@return String : nom du client
*/
	String getClientName();
/**
*definir le nom du client
*@param String : nom du client
*/
	void setClientName(String clientName);
/**
*definir l'adresse du serveur
*@param String : adresse serveur
*/
	void setServer(String server);
/**
*méthode appelée pour lors du lancement du client
*/
	void run();
	
/**
 * définir le port d'écoute du client
 * @param port
 */
	void setPort(int port);
/**
 * méthode qui ferme le socket du Client
 */
	public void stopClient();
/**
 * Méthode retournant le port du Client.
 * @return
 */
	int getPort();
}
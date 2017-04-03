package framework.plugin;

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
*@param clientName nom du client
*/
	void setClientName(String clientName);
/**
*definir l'adresse du serveur
*@param server adresse serveur
*/
	void setServer(String server);
/**
*méthode appelée pour lors du lancement du client
*/
	void run();
	
/**
 * définir le port d'écoute du client
 * @param port le port d'écoute
 */
	void setPort(int port);
/**
 * méthode qui ferme le socket du Client
 */
void stopClient();
/**
 * Méthode retournant le port du Client.
 * @return le port du client
 */
	int getPort();
}
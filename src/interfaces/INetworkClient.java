package interfaces;

import framework.IExtension;

/**
*interface pour extension chat coté client
*/
public interface INetworkClient extends IExtension {
	
	/**
	 * Se connecte au serveur
	 */
	void connect();
	
	/**
	 * Se déconnecte du serveur
	 */
	void disconnect();
	
	/**
	* Permet d'envoyer un message
	*/
	void send(String message);
	
	/* Accesseurs et mutateurs */
	
	String getPseudo();
	void setPseudo(String pseudo);
	
	String getAddress();
	void setAddress(String address);

	int getPort();
	void setPort(int port);
	
	boolean isConnected();

}
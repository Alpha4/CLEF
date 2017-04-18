package interfaces;

import framework.IExtension;

/**
 * Interface pour client d'un réseau
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
	* 
	* @param message Message à envoyer
	*/
	void send(String message);
	
	/* Accesseurs et mutateurs */
	
	/**
	 * Accesseur de pseudo
	 * 
	 * @return pseudo
	 */
	String getPseudo();
	
	/**
	 * Mutateur de pseudo
	 * 
	 * @param pseudo pseudo
	 */
	void setPseudo(String pseudo);
	
	/**
	 * Accesseur d'address
	 * 
	 * @return address
	 */
	String getAddress();
	
	/**
	 * Mutateur d'address
	 * 
	 * @param address address
	 */
	void setAddress(String address);

	/**
	 * Accesseur de port
	 * 
	 * @return port
	 */
	int getPort();
	
	/**
	 * Mutateur de port
	 * 
	 * @param port port
	 */
	void setPort(int port);
	
	/**
	 * Indique si le client est connecté
	 * 
	 * @return Vrai si le client est connecté à un serveur, faux sinon
	 */
	boolean isConnected();

}
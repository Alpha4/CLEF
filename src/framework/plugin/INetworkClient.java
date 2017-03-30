package framework.plugin;

import java.net.InetAddress;

import framework.IExtension;

/**
*@class INetworkClient
*@brief interface pour extension chat coté client
*
*/
public interface INetworkClient extends IExtension {
/**
*@brief envoyer message au serveur
*/
	void send(String message);
/**
*@brief retourne nom du client
*@return String : nom du client
*/
	String getClientName();
/**
*@brief definir le nom du client
*@param String : nom du client
*/
	void setClientName(String clientName);
/**
*@brief definir l'adresse du serveur
*@param String : adresse serveur
*/
	void setServer(String server);
/**
*@brief méthode appelée pour lors du lancement du client
*/
	void run();
}
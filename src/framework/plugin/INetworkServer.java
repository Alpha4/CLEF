package framework.plugin;

import framework.IExtension;
/**
*@class Interface du serveur
*@brief envoyer message au serveur
*/
public interface INetworkServer extends IExtension {
/**
*@brief methode appelée lors du lancement du serveur
*/
	void run();

	void setPort(int port);
	public void stopThread();

}
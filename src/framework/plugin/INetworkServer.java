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

}
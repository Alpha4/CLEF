package framework.plugin;

import java.util.Map;

import framework.IExtension;
/**
*Interface du monitoring
*/
public interface IMonitoring extends IExtension {

	Map<Class<?>,String> getExtensionsStatus();
	void loadExtension(Class<?> cl);
	void killExtension(Class<?> cl);
}

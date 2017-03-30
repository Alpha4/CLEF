package framework.plugin;

import java.util.Map;

import framework.IExtension;
/**
*Interface du monitoring
*/
public interface IMonitoring extends IExtension {

	public Map<Class<?>,String> getExtensionsStatus();
	public void loadExtension(Class<?> cl);
	public void killExtension(Class<?> cl);
}

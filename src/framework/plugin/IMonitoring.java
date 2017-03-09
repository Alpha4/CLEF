package framework.plugin;

import java.util.Map;

import framework.IExtension;

public interface IMonitoring extends IExtension {

	public Map<Class<?>,String> getExtensionsStatus();
}

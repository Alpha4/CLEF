package extensions.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import framework.IExtension;
import interfaces.IMonitoring;
import framework.IExtensionActions;
import framework.Framework;

public class Monitoring implements IMonitoring {
	
	private Map<Class<?>,Map<Class<?>,IExtension>> extensions;

	
	public void start() {
		extensions = Framework.extensions;
	}
	
	public Map<Class<?>,String> getExtensionsStatus() {
		Map<Class<?>,String> res = new HashMap<Class<?>,String>();
		for(Map<Class<?>,IExtension> extensions : this.extensions.values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()){
				res.put(extension.getKey(), ((IExtensionActions)extension.getValue()).getStatus());
			}
		}
		return res;
	}
	
	public void loadExtension(Class<?> cl) {
		for(Map<Class<?>,IExtension> extensions : this.extensions.values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()){
				if (extension.getKey().equals(cl)) {
					((IExtensionActions)extension.getValue()).load();
				}
			}
		}
	}

	public void killExtension(Class<?> cl) {
		for(Map<Class<?>,IExtension> extensions : this.extensions.values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()){
				if (extension.getKey().equals(cl)) {
					((IExtensionActions)extension.getValue()).kill();
				}
			}
		}
	}
}

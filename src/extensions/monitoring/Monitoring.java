package extensions.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import framework.IExtension;
import framework.ExtensionActions;
import framework.Framework;
import framework.plugin.IMonitoring;

public class Monitoring implements IMonitoring {
	
	private static Map<Class<?>,Map<Class<?>,IExtension>> extensions;

	public void run() {
		extensions = Framework.extensions;
	}
	
	public Map<Class<?>,String> getExtensionsStatus() {
		Map<Class<?>,String> res = new HashMap<Class<?>,String>();
		for(Map<Class<?>,IExtension> extensions : this.extensions.values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()){
				res.put(extension.getKey(), ((ExtensionActions)extension.getValue()).getStatus());
			}
		}
		return res;
	}
	
	public void load(IExtension extension) {
		((ExtensionActions)extension).load();
	}

	public void kill(IExtension extension) {
		((ExtensionActions)extension).kill();
	}
}

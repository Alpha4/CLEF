package extensions.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import framework.ExtensionContainer;
import framework.Framework;
import framework.plugin.IMonitoring;

public class Monitoring implements IMonitoring {
	
	private static Map<Class<?>,Map<Class<?>,ExtensionContainer>> extensions;

	public void run() {
		extensions = Framework.extensions;
	}
	
	public Map<Class<?>,String> getExtensionsStatus() {
		Map<Class<?>,String> res = new HashMap<Class<?>,String>();
		for(Map<Class<?>,ExtensionContainer> containers : extensions.values()) {
			for(Entry<Class<?>,ExtensionContainer> container : containers.entrySet()){
				res.put(container.getKey(), container.getValue().getStatus());
			}
		}
		return res;
	}

}

package framework;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.*;

public class Framework {

	public static Map<Class<?>,Map<Class<?>,ExtensionContainer>> extensions;
	private static List<ExtensionContainer> autorunExtensions;

	public static void main(String[] args) throws Exception {
		
		Framework.autorunExtensions = new ArrayList<ExtensionContainer>();
		Framework.extensions = new HashMap<Class<?>, Map<Class<?>, ExtensionContainer>>();

		/* 1- Load config */
		Config config = Framework.loadConfig(null);
		
		/* 2- Load dependencies */
		Framework.loadDependencies(config);
		
		/* 3- Execute autorun extensions */
		Framework.executeAutorunExtensions();
		
	}
	
	private static void loadDependencies(Config frameworkConfig) {
		
		for(String classpath : frameworkConfig.getExtensions()) {
			Config config = Framework.loadConfig(classpath);
			
			try {
				
				// Get plugin interface class
				Class<?> plugInterface = Class.forName("framework.plugin."+config.getType());
				
				// Get plugin class
				Class<?> plugClass = Class.forName(classpath);
				
				// Create ExtensionContainer
				ExtensionContainer container = new ExtensionContainer(plugClass, config);
				
				if (config.isAutorun()) {
					autorunExtensions.add(container);
				}
				
				if (extensions.get(plugInterface) == null) {
					extensions.put(plugInterface, new HashMap<Class<?>,ExtensionContainer>());
				}
				
				extensions.get(plugInterface).put(plugClass, container);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void executeAutorunExtensions() {
		
		for(ExtensionContainer container : Framework.autorunExtensions) {
			container.getExtension().run();
		}
		
	}
	
	public static IExtension getExtension(Class<?> cl) {
		return Framework.getExtension(cl,0);
	}
	
	public static IExtension getExtension(Class<?> cl, int i) {
		return Framework.get(cl).get(i).getExtension();
	}
	
	public static List<ExtensionContainer> get(Class<?> cl) {
		List<ExtensionContainer> containers = new ArrayList<ExtensionContainer>();
		
		for(Entry<Class<?>, ExtensionContainer> container : extensions.get(cl).entrySet()) {
			containers.add(container.getValue());
		}
		
		return containers;
	}
	
	public static IExtension get(Class<?> cl, Class<?> cl2) {
		
		for(Entry<Class<?>, ExtensionContainer> container : extensions.get(cl).entrySet()) {
			if (container.getValue().getExtensionClass() == cl2) {
				return container.getValue().getExtension();
			}
		}
		
		return null;
	}
	
	private static Config loadConfig(String classpath) {
		
		String path;
		if (classpath == null) {
			path = "config.json";
		} else {
			path = classpath.replace(".", "/");
			path = "src/" + path.substring(0, path.lastIndexOf('/')+1) + "config.json";
		}
		
		Gson gson = new Gson();
		Config config = null;
		
		try {
			config = gson.fromJson(new FileReader(path), Config.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return config;
	}

}

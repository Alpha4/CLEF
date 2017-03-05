package framework;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.*;

public class Framework {

	private static Map<Class<?>,Map<Class<?>,ExtensionContainer>> extensions;
	private static List<ExtensionContainer> autorunExtensions;

	public static void main(String[] args) throws Exception {
		
		Framework.autorunExtensions = new ArrayList<ExtensionContainer>();
		Framework.extensions = new HashMap<Class<?>, Map<Class<?>, ExtensionContainer>>();

		/* 1- Load config */
		Config config = Framework.loadConfig();
		
		/* 2- Load dependencies */
		Framework.loadDependencies(config);
		
		/* 3- Execute autorun extensions */
		Framework.executeAutorunExtensions();
		
	}
	
	public static void loadDependencies(Config frameworkConfig) {
		
		List<Config> dependenciesConfigs = Framework.resolveDependencies(frameworkConfig);
		
		for(Config conf : dependenciesConfigs) {
			if (conf.getType() != null) {
				try {
					// Get plugin interface class
					Class<?> plugInterface = Class.forName("framework.plugin.I"+conf.getType());
					
					// Create ExtensionContainer
					ExtensionContainer container = new ExtensionContainer(conf);
					
					if (IAutorunExtension.class.isAssignableFrom(plugInterface)) {
						// Add the container to the autorun extensions
						Framework.autorunExtensions.add(container);
					}
				
					// Add the container to the extensions
					if (Framework.extensions.get(plugInterface) == null) {
						Framework.extensions.put(plugInterface, new HashMap<Class<?>, ExtensionContainer>());
					}
					Framework.extensions.get(plugInterface).put(Class.forName(conf.getClasspath()), container);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static List<Config> resolveDependencies(Config config) {
		
		List<Config> dependencies = new ArrayList<Config>();
		
		dependencies.add(config);
		Framework.resolveDependencies(config, dependencies);
		
		return dependencies;
	}
	
	public static List<Config> resolveDependencies(Config config, List<Config> dependencies) {
		
		for(String classpath : config.getDependencies()) {
			Config conf = Framework.loadConfig(classpath);
			
			boolean found = false;
			for (Config c : dependencies) {
				if (c.getClasspath() != null && c.getClasspath().equals(conf.getClasspath())) {
					found = true;
				}
			}
			
			if (!found) {
				dependencies.add(conf);
				Framework.resolveDependencies(conf, dependencies);
			}
		}
		
		return dependencies;
	}
	
	private static void executeAutorunExtensions() {
		
		for(ExtensionContainer container : Framework.autorunExtensions) {
			((IAutorunExtension) container.getExtension()).run();
		}
		
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
			if (container.getValue().getMeta().getClasspath().equals(cl2.getName())) {
				return container.getValue().getExtension();
			}
		}
		
		return null;
	}

	public static Config loadConfig() {
		return loadConfig("");
	}
	
	public static Config loadConfig(String classpath) {
		
		String path;
		if (classpath == "") {
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

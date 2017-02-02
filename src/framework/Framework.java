package framework;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.*;

public class Framework {
	
	// TODO: change to List of extension containers
	private static Map<Class<?>,Map<Class<?>,ExtensionContainer>> extensions;

	public static void main(String[] args) throws Exception {

		/* 1- Load config */
		Config base = Framework.loadConfig();
		
		/* 2- Load app config */
		Config app = Framework.loadConfig(base.getBase());
		
		/* 3- Load extensions */
		Framework.extensions = Framework.loadExtensions(app);
		
		// TODO: Faire les autoruns
		/* 4- Execute app extension */
		List<Config> configs = Framework.get(Class.forName("framework.plugin.I"+app.getType()));
		((IAutorunExtension) Framework.get(Class.forName("framework.plugin.I"+app.getType()), configs.get(0))).run();
		
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
	
	public static Map<Class<?>, Map<Class<?>, ExtensionContainer>> loadExtensions(Config app) {
		
		Map<Class<?>, Map<Class<?>, ExtensionContainer>> extensions = new HashMap<Class<?>, Map<Class<?>, ExtensionContainer>>();
		
		// Load app
		
		try {
			Class<?> plugin = Class.forName("framework.plugin.I"+app.getType());
			extensions.put(plugin, new HashMap<Class<?>, ExtensionContainer>());
			extensions.get(plugin).put(Class.forName(app.getClasspath()), new ExtensionContainer(app));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Load app's dependencies
		
		List<Config> configs = Framework.resolveDependencies(app);
		
		for(Config config : configs) {
			try {
				Class<?> plugin = Class.forName("framework.plugin.I"+config.getType());
				if (extensions.get(plugin) == null) {
					extensions.put(plugin, new HashMap<Class<?>, ExtensionContainer>());
				}
				extensions.get(plugin).put(Class.forName(config.getClasspath()), new ExtensionContainer(config));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println(extensions);
		
		return extensions;
	}
	
	public static List<Config> resolveDependencies(Config config) {
		List<Config> dependencies = new ArrayList<Config>();
		
		dependencies.add(config);
		
		for(String classpath : config.getDependencies()) {
			Config conf = Framework.loadConfig(classpath);
			dependencies.addAll(Framework.resolveDependencies(conf));
		}
		
		return dependencies;
	}
	
	public static List<Config> get(Class<?> cl) {
		List<Config> configs = new ArrayList<Config>();
		
		for(Entry<Class<?>, ExtensionContainer> container : extensions.get(cl).entrySet()) {
			configs.add(container.getValue().getMeta());
		}
		
		return configs;
	}
	
	public static IExtension get(Class<?> cl, Config config) {
		
		
		for(Entry<Class<?>, ExtensionContainer> container : extensions.get(cl).entrySet()) {
			if (container.getValue().getMeta().getClasspath().equals(config.getClasspath())) {
				return container.getValue().getExtension();
			}
		}
		
		return null;
	}

}

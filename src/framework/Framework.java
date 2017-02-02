package framework;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

public class Framework {
	
	private static Map<Class<?>,ExtensionContainer> extensions;

	public static void main(String[] args) throws Exception {

		/* 1- Load config */
		Config base = Framework.loadConfig();
		
		/* 2- Load app config */
		Config app = Framework.loadConfig(base.getBase());
		
		/* 3- Load extensions */
		Framework.extensions = Framework.loadExtensions(app);
		
		/* 4- Execute app extension */
		((IAppExtension) Framework.get(Class.forName(app.getClasspath()))).run();
		
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
	
	public static Map<Class<?>, ExtensionContainer> loadExtensions(Config app) {
		
		Map<Class<?>, ExtensionContainer> extensions = new HashMap<Class<?>, ExtensionContainer>();
		
		// Instantiate app
		
		try {
			extensions.put(Class.forName(app.getClasspath()), new ExtensionContainer(app));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Instantiate app's dependencies
		
		List<Config> configs = Framework.resolveDependencies(app);
		
		for(Config config : configs) {
			try {
				extensions.put(Class.forName(config.getClasspath()), new ExtensionContainer(config));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
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
	
	public static IExtension get(Class<?> cl) {
		ExtensionContainer container = Framework.extensions.get(cl);
		if (container == null)
			System.out.println("Extension not found: "+cl);
		return container.getExtension();
	}

}

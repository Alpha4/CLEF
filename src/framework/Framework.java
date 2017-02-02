package framework;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

public class Framework {

	public static void main(String[] args) {

		/* 1- Load config */
		Config base = Framework.loadConfig();
		
		/* 2- Load app config */
		Config app = Framework.loadConfig(base.getBase());
		
		/* 3- Load extensions */
		Map<String,IExtension> extensions = Framework.loadExtensions(app);
		
		/* 4- Execute app extension */
		extensions.get("extensions.app.App").run();
		
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
	
	public static Map<String, IExtension> loadExtensions(Config app) {
		
		Map<String, IExtension> extensions = new HashMap<String, IExtension>();
		
		// Instantiate app
		
		try {
			Class<?> cl = Class.forName(app.getClasspath());
			IExtension ex = (IExtension) cl.newInstance();
			extensions.put(app.getClasspath(),ex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Instantiate app's dependencies
		
		List<String> classpaths = Framework.resolveDependencies(app);
		
		for(String classpath : classpaths) {
			try {
				Class<?> cl = Class.forName(classpath);
				IExtension ex = (IExtension) cl.newInstance();
				extensions.put(classpath, ex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return extensions;
	}
	
	public static List<String> resolveDependencies(Config config) {
		List<String> dependencies = new ArrayList<String>();
		
		dependencies.addAll(config.getDependencies());
		
		for(String classpath : dependencies) {
			Config conf = Framework.loadConfig(classpath);
			dependencies.addAll(Framework.resolveDependencies(conf));
		}
		
		return dependencies;
	}

}

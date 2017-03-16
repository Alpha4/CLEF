package framework;

import java.io.FileReader;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.*;

public class Framework {

	public static Map<Class<?>,Map<Class<?>,IExtension>> extensions;
	private static List<IExtension> autorunExtensions;

	public static void main(String[] args) throws Exception {
		
		Framework.autorunExtensions = new ArrayList<IExtension>();
		Framework.extensions = new HashMap<Class<?>, Map<Class<?>, IExtension>>();

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
				
				// Create Extension
				IExtension extension = Framework.createExtension(plugClass, config);
				
				if (config.isAutorun()) {
					autorunExtensions.add(extension);
				}
				
				if (extensions.get(plugInterface) == null) {
					extensions.put(plugInterface, new HashMap<Class<?>,IExtension>());
				}
				
				extensions.get(plugInterface).put(plugClass, extension);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void executeAutorunExtensions() {
		
		for(IExtension extension : Framework.autorunExtensions) {
			extension.run();
		}
		
	}
	
	public static IExtension getExtension(Class<?> cl){
		return Framework.get(cl).get(0);
	}
	
	public static List<IExtension> get(Class<?> cl) {
		List<IExtension> extensions = new ArrayList<IExtension>();
		
		for(Entry<Class<?>, IExtension> extension : Framework.extensions.get(cl).entrySet()) {
			extensions.add(extension.getValue());
		}
		
		return extensions;
	}
	
	public static IExtension get(Class<?> cl, Class<?> cl2) {
		
		for(Entry<Class<?>, IExtension> extension : Framework.extensions.get(cl).entrySet()) {
			if (extension.getValue().getClass() == cl2) {
				return extension.getValue();
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
	
	private static IExtension createExtension(Class<?> cl, Config conf) {
		
		Class<?>[] interfaces = cl.getInterfaces();
		interfaces = Arrays.copyOf(interfaces, interfaces.length+1);
		interfaces[interfaces.length-1] = ExtensionActions.class;
		IExtension ext = (IExtension) Proxy.newProxyInstance(cl.getClassLoader(), interfaces, new ExtensionContainer(cl, conf));
		
		return ext;
	}

}

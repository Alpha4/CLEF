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
	
	private static Map<String,List<IExtension>> eventHandlers;
	
	private static Config config;
	
	// Public functions

	public static void main(String[] args) throws Exception {
		
		Framework.autorunExtensions = new ArrayList<IExtension>();
		Framework.extensions = new HashMap<Class<?>, Map<Class<?>, IExtension>>();
		
		Framework.eventHandlers = new HashMap<String, List<IExtension>>();

		/* 1- Load config */
		config = Framework.loadConfig(null);
		
		/* 2- Load dependencies */
		Framework.loadDependencies(config);
		
		/* 3- Execute autorun extensions */
		Framework.executeAutorunExtensions();
		
	}
	
	public static void exit() {
		
		// Essaye d'arrêter gracieusement chacune des extensions
		for(IExtension extension : Framework.autorunExtensions) {
			((IExtensionActions)extension).kill();
		}
		
		// Ferme le programme
		System.exit(0);
		
	}
	
	/**
	 * Récupère la première extension disponible de l'interface demandée en paramètre
	 * @param cl, l'interface demandée
	 * @return IExtension, une extension
	 */
	public static IExtension getExtension(Class<?> cl){
		return Framework.get(cl).get(0);
	}
	
	/**
	 * Récupère la liste des extensions disponible de l'interface demandée en paramètre
	 * @param cl, l'interface demandée
	 * @return List<IExtension>, la liste des extensions
	 */
	public static List<IExtension> get(Class<?> cl) {
		List<IExtension> extensions = new ArrayList<IExtension>();
		
		if (Framework.extensions.get(cl) == null)
			throw new RuntimeException("No \""+cl.getName()+"\" extensions are configured in this application. Check your config.json!");
		
		for(Entry<Class<?>, IExtension> extension : Framework.extensions
				.get(cl)
				.entrySet()) {
			extensions.add(extension.getValue());
		}
		
		return extensions;
	}
	
	/**
	 * Récupère l'extension suivant l'interface et la classe de l'extension donnée en paramètre
	 * @param cl, l'interface demandée
	 * @param cl2, la classe de l'extension
	 * @return l'extension voulue
	 */
	public static IExtension get(Class<?> cl, Class<?> cl2) {
		
		for(Entry<Class<?>, IExtension> extension : Framework.extensions.get(cl).entrySet()) {
			if (extension.getValue().getClass() == cl2) {
				return extension.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Déclare un événement qui vient d'avoir lieu
	 * @param name, le nom de l'événement
	 * @param event, un objet associé à l'événement
	 */
	public static void event(String name, Object event) {
		if (Framework.eventHandlers.containsKey(name)) {
			List<IExtension> handlers = Framework.eventHandlers.get(name);
			for (IExtension handler : handlers) {
				handler.handleEvent(name, event);
			}
		}
	}
	
	/**
	 * Demande à être notifié à chaque fois qu'un événement est déclaré
	 * @param name, le nom de l'événement
	 * @param handler, l'extension qui souhaite être notifié
	 */
	public static void subscribeEvent(String name, IExtension handler) {
		
		handler = Framework.proxyOf(handler);
		
		// If first handler to subscribe to this event
		if (!Framework.eventHandlers.containsKey(name)) {
			Framework.eventHandlers.put(name, new ArrayList<IExtension>());
		}
		
		// If not already subscribed
		if (!Framework.eventHandlers.get(name).contains(handler)) {
			Framework.eventHandlers.get(name).add(handler);
		}
	}
	
	public static void unsubscribeEvent(String name, IExtension handler) {
		
		handler = Framework.proxyOf(handler);
		
		Framework.eventHandlers.get(name).remove(handler);
	}
	
	/**
	 * Permet de récupérer l'objet Config de l'application ('config.json')
	 * @return Config de l'application
	 */
	public static Config getConfig() {
		return Framework.config;
	}
	
	// Private functions
	
	/**
	 * Permet de lire la configuration de l'application et récupérer toutes les extensions nécessaires
	 * @param frameworkConfig: Configuration de l'application
	 */
	private static void loadDependencies(Config frameworkConfig) {
		
		for(String classpath : frameworkConfig.getExtensions()) {
			Config config = Framework.loadConfig(classpath);
			
			try {
				
				// Get plugin interface class
				Class<?> plugInterface = Class.forName("interfaces."+config.getType());
				
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
	
	/**
	 * Execute la methode run() de toutes les extensions ayant le paramètre "autorun" à vrai
	 */
	private static void executeAutorunExtensions() {
		
		for(IExtension extension : Framework.autorunExtensions) {
			((IExtensionActions)extension).load();
		}
		
	}
	
	/**
	 * Charge le fichier de config ('config.json') correspondant au classpath de l'extension
	 * Si classpath est null, charge le fichier de config de l'application
	 * @param classpath de l'extension
	 * @return Config, le fichier de config correspondant
	 */
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
	
	/**
	 * Créer le proxy de l'extension suivant la classe et le fichier de config associé
	 * @param cl, le classe de l'extension
	 * @param conf, le fichier de config (config.json) associé
	 * @return IExtension, l'extension derrière un proxy
	 */
	private static IExtension createExtension(Class<?> cl, Config conf) {
		
		Class<?>[] interfaces = cl.getInterfaces();
		interfaces = Arrays.copyOf(interfaces, interfaces.length+1);
		interfaces[interfaces.length-1] = IExtensionActions.class;
		IExtension ext = (IExtension) Proxy.newProxyInstance(cl.getClassLoader(), interfaces, new ExtensionContainer(cl, conf));
		
		return ext;
	}
	
	private static IExtension proxyOf(IExtension ex) {
		for (Entry<Class<?>,Map<Class<?>,IExtension>> extensions : Framework.extensions.entrySet()) {
			for (Entry<Class<?>,IExtension> extension : extensions.getValue().entrySet()) {
				if (((IExtensionActions)extension.getValue()).isProxyOf(ex)) {
					return extension.getValue();
				}
			}
		}
		return ex; // Already was a proxy
	}

}

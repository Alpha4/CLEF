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

/**
 * Framework est la classe principale de la plateforme.
 */

/*
 * Rappel API de Framework:
 *
 * Application:
 * 		- exit()								Quitte l'application
 * 		- getConfig()							Récupère la configuration de l'application
 *
 * Extensions:
 * 		- getExtensions()						Récupère toutes les extensions
 * 		- getExtension(Class<T>)				Récupère une IExtension
 * 		- get(Class<?>)							Récupère une liste d'IExtension
 * 		- get(Class<?>, Class<T>)				Récupère une IExtension
 * 		- getExtensionConfig(IExtension)		Renvoie la configuration de l'extension
 * 		- getExtensionStatus(IExtension)		Renvoie le status d'une extension
 * 		- loadExtension(IExtension)				Charge une extension
 * 		- killExtension(IExtension)				Kill une extension
 * 		- isKillable(IExtension)				Indique si une extension est killable
 *
 * Events:
 * 		- event(String, Object)					Déclare un nouvel événement
 * 		- event(Event)							Déclare un nouvel événement
 * 		- subscribeEvent(String, IExtension)	Souscris à un type d'événement
 * 		- unsubscribeEvent(String, IExtension)	Désouscris à un type d'événement
 */
public class Framework {
	
	private static String extensionsPackage = "extensions";
	private static String interfacesPackage = "interfaces";

	private static Map<Class<?>,Map<Class<?>,IExtension>> extensions;
	private static List<IExtension> autorunExtensions;
	private static Map<String,List<IExtension>> eventHandlers;
	private static Config config;


	/****************************************/
	/*                                      */
	/*             APPLICATION              */
	/*                                      */
	/****************************************/

	/**
	 * Fonction principal de l'exécutable
	 * 
	 * @param args arguments de l'exécutable
	 */
	public static void main(String[] args) {

		Framework.autorunExtensions = new ArrayList<IExtension>();
		Framework.extensions = new HashMap<Class<?>, Map<Class<?>, IExtension>>();

		Framework.eventHandlers = new HashMap<String, List<IExtension>>();

		/* 1- Load config */
		config = Framework.loadConfig(null);

		/* 2- Load dependencies */
		Framework.loadDependencies();

		/* 3- Execute autorun extensions */
		Framework.executeAutorunExtensions();

	}

	/**
	 * Quitte le programme
	 *
	 * <p>
	 *
	 * Essaye d'arrêter gracieusement chacune des extensions avant de
	 * quitter le programme
	 */
	public static void exit() {

		// Essaye d'arrêter gracieusement chacune des extensions
		for(Map<Class<?>,IExtension> extensions : Framework.extensions.values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()){
				((IExtensionActions)extension.getValue()).kill();
			}
		}

		// Ferme le programme
		System.exit(0);

	}

	/**
	 * Récupère la configuration de l'application
	 *
	 * <p>
	 *
	 * Permet de récupérer l'objet Config de l'application ('application.json')
	 *
	 * @return Configuration de l'application
	 */
	public static Config getConfig() {
		return Framework.config;
	}


	/****************************************/
	/*                                      */
	/*              EXTENSIONS              */
	/*                                      */
	/****************************************/

	/**
	 * Récupère toutes les extensions
	 * 
	 * @return les extensions
	 */
	public static Map<Class<?>,Map<Class<?>,IExtension>> getExtensions() {
		return Framework.extensions;
	}
	
	/**
	 * Récupère une extension
	 *
	 * <p>
	 *
	 * Récupère la première extension disponible en fonction de l'interface
	 * donnée en paramètre
	 *
	 * @param <T>					la classe de l'interface demandée
	 * @param extensionInterface	l'interface demandée
	 * @return 						l'extension
	 */
	public static <T extends IExtension> T getExtension(Class<T> extensionInterface){
		return extensionInterface.cast(Framework.get(extensionInterface).get(0));
	}

	/**
	 * Récupère une liste d'extension
	 *
	 * <p>
	 *
	 * Récupère la liste des extensions disponible en fonction de l'interface
	 * donnée en paramètre
	 *
	 * @param <T>					la classe de l'interface demandée
	 * @param extensionInterface 	l'interface demandée
	 * @return 						la liste des extensions
	 */
	public static <T extends IExtension> List<T> get(Class<T> extensionInterface) {
		List<T> extensions = new ArrayList<T>();

		if (Framework.extensions.get(extensionInterface) == null)
			throw new RuntimeException("No \""+extensionInterface.getName()+"\" extensions are"
					+ " configured in this application. Check your config.json!");

		for(Entry<Class<?>, IExtension> extension : Framework.extensions
				.get(extensionInterface)
				.entrySet()) {
			extensions.add(extensionInterface.cast(extension.getValue()));
		}

		return extensions;
	}

	/**
	 * Récupère une extension
	 *
	 * <p>
	 *
	 * Récupère l'extension suivant l'interface et la classe de l'extension
	 * donnée en paramètre
	 *
	 * @param <T>					la classe de l'interface demandée
	 * @param extensionInterface	l'interface demandée
	 * @param extensionClass		la classe de l'extension choisie
	 * @return 						l'extension voulue
	 */
	public static <T extends IExtension> T get(Class<T> extensionInterface, Class<?> extensionClass) {

		for(Entry<Class<?>, IExtension> extension : Framework.extensions.get(extensionInterface).entrySet()) {
			if (extension.getValue().getClass() == extensionClass) {
				return extensionInterface.cast(extension.getValue());
			}
		}

		return null;
	}
	
	/**
	 * Récupère la configuration d'une extension
	 * 
	 * <p>
	 * 
	 * @param extension		l'extension dont la configuration est demandée
	 * @return				la configuration de l'extension
	 */
	public static Config getExtensionConfig(IExtension extension) {
		extension = Framework.proxyOf(extension);
		return ((IExtensionActions)extension).getConfig();
	}

	/**
	 * Récupère le status d'une extension
	 *
	 * <p>
	 *
	 * @see framework.Status
	 *
	 * @param extension 	l'extension dont le status est demandé
	 * @return 				le status
	 */
	public static String getExtensionStatus(IExtension extension) {
		extension = Framework.proxyOf(extension);
		return ((IExtensionActions)extension).getStatus();
	}

	/**
	 * Charge une extension
	 *
	 * @param extension		l'extension à charger
	 * @return 				Vrai si l'extension a bien été chargée, faux sinon
	 */
	public static boolean loadExtension(IExtension extension) {
		extension = Framework.proxyOf(extension);
		if (!((IExtensionActions)extension).getStatus().equals(Status.LOADED))
			return ((IExtensionActions)extension).load();
		return false;
	}

	/**
	 * Kill une extension
	 *
	 * @param extension		l'extension à killer
	 * @return 				Vrai si l'extension a bien été killed, faux sinon
	 */
	public static boolean killExtension(IExtension extension) {
		extension = Framework.proxyOf(extension);
		if (!((IExtensionActions)extension).getStatus().equals(Status.KILLED))
			return ((IExtensionActions)extension).kill();
		return false;
	}

	/**
	 * Indique si une extension peut être killed
	 *
	 * @param extension		l'extension en question
	 * @return 				Vrai si l'extension peut être killed, faux sinon
	 */
	public static boolean isKillable(IExtension extension) {
		extension = Framework.proxyOf(extension);
		return ((IExtensionActions)extension).isKillable();
	}


	/****************************************/
	/*                                      */
	/*                EVENTS                */
	/*                                      */
	/****************************************/

	/**
	 * Déclare un nouvel événement
	 *
	 * <p>
	 *
	 * Surcharge de {@link #event(Event)}
	 *
	 * @param name		le nom de l'événement
	 * @param payload	un objet associé à l'événement
	 */
	public static void event(String name, Object payload) {

		Event event = new Event(name, payload);

		Framework.event(event);
	}

	/**
	 * Déclare un nouvel événement
	 *
	 * @param event l'événement
	 */
	public static void event(Event event) {

		for (Entry<String,List<IExtension>> entry : Framework.eventHandlers.entrySet()) {
			if (event.is(entry.getKey())) {
				List<IExtension> handlers = Framework.eventHandlers.get(entry.getKey());
				for (IExtension handler : handlers) {
					handler.handleEvent(event);
				}
			}
		}
	}

	/**
	 * Souscris à un type d'événement
	 *
	 * <p>
	 *
	 * Demande à être notifié à chaque fois qu'un événement d'un certain type
	 * est déclaré
	 *
	 * @param name		le nom de l'événement
	 * @param handler	l'extension qui souhaite être notifiée
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

	/**
	 * Désouscris à un type d'événement
	 *
	 * <p>
	 *
	 * Demande à ne plus être notifié à chaque fois qu'un événement
	 * d'un certain type est déclaré
	 *
	 * @param name		le nom de l'événement
	 * @param handler	l'extension qui ne souhaite plus être notifiée
	 */
	public static void unsubscribeEvent(String name, IExtension handler) {

		handler = Framework.proxyOf(handler);

		for (Entry<String,List<IExtension>> entry : Framework.eventHandlers.entrySet()) {
			Event event = new Event(entry.getKey(), null);
			if (event.is(name)) {
				Framework.eventHandlers.get(entry.getKey()).remove(handler);
			}
		}
	}

	/****************************************/
	/*                                      */
	/*          PRIVATE FUNCTIONS           */
	/*                                      */
	/****************************************/

	/**
	 * Pré-charge les dépendances de l'application
	 *
	 * <p>
	 *
	 * Permet de lire la configuration de l'application et récupérer toutes
	 * les extensions nécessaires
	 */
	private static void loadDependencies() {

		for(String name : Framework.getConfig().getExtensions()) {
			Config config = Framework.loadConfig(name);

			try {
				
				// Get extension classpath
				String classpath = extensionsPackage+"."+name+"."+config.getName();

				// Get extension interface class
				Class<?> extensionInterface = Class.forName(interfacesPackage+"."+config.getType());

				// Get extension class
				Class<?> extensionClass = Class.forName(classpath);

				// Create Extension
				IExtension extension = createExtension(extensionInterface, extensionClass, config);

				if (config.isAutorun()) {
					autorunExtensions.add(extension);
				}

				if (extensions.get(extensionInterface) == null) {
					extensions.put(extensionInterface, new HashMap<Class<?>,IExtension>());
				}

				extensions.get(extensionInterface).put(extensionClass, extension);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Démarre les extensions "autorun"
	 *
	 * <p>
	 *
	 * Execute la methode start() de toutes les extensions ayant le paramètre
	 * "autorun" à true
	 */
	private static void executeAutorunExtensions() {

		for(IExtension extension : autorunExtensions) {
			((IExtensionActions)extension).load();
		}

	}

	/**
	 * Charge un fichier de configuration
	 *
	 * <p>
	 *
	 * Charge le fichier de configuration ('config.json') correspondant au classpath
	 * de l'extension
	 * Si classpath est null, charge le fichier de config de l'application
	 *
	 * @param classpath		Classpath de l'extension
	 * @return				le fichier de configuration correspondant
	 */
	private static Config loadConfig(String name) {

		String path;
		if (name == null) {
			path = "application.json";
		} else {
			path = "src/"+extensionsPackage+"/" + name + "/config.json";
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
	 * Créer l'extension
	 *
	 * <p>
	 *
	 * Créer le proxy de l'extension suivant la classe et le fichier de config associé
	 *
	 * @param extensionClass	le classe de l'extension
	 * @param config			la configuration associée
	 * @return l'extension derrière un proxy
	 */
	private static IExtension createExtension(Class<?> extensionInterface, Class<?> extensionClass, Config config) {

		Class<?>[] interfaces = extensionClass.getInterfaces();
		interfaces = Arrays.copyOf(interfaces, interfaces.length+1);
		interfaces[interfaces.length-1] = IExtensionActions.class;
		IExtension ext = (IExtension) Proxy.newProxyInstance(extensionClass.getClassLoader(),
				interfaces, new ExtensionContainer(extensionInterface, extensionClass, config));

		return ext;
	}

	/**
	 * Trouve le proxy correspondant à une extension
	 *
	 * @param extension		l'extension
	 * @return				le proxy de l'extension
	 */
	private static IExtension proxyOf(IExtension extension) {
		for (Entry<Class<?>,Map<Class<?>,IExtension>> extensions : Framework.extensions.entrySet()) {
			for (Entry<Class<?>,IExtension> extensionEntry : extensions.getValue().entrySet()) {
				if (((IExtensionActions)extensionEntry.getValue()).isProxyOf(extension)) {
					return extensionEntry.getValue();
				}
			}
		}
		// If already was a proxy
		return extension;
	}

}

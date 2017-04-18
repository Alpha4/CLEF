package framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Proxy d'une IExtension
 * 
 * <p>
 * 
 * Evénements créés:<br>
 * 		- extension.loaded - Class		Event au chargement d'une extension<br>
 * 		- extension.killed - Class		Event au kill d'une extension<br>
 * 		- extension.error  - Class		Event à l'erreur d'une extension<br>
 */
class ExtensionContainer implements InvocationHandler {

	Config config;
	IExtension extension;
	String status;
	Class<?> extensionInterface;
	Class<?> extensionClass;
	
	/**
	 * Constructeur par défaut
	 * 
	 * @param extensionClass	Classe de l'extension
	 * @param meta				Configuration de l'extension
	 */
	public ExtensionContainer(Class<?> extensionInterface, Class<?> extensionClass, Config config) {
		super();
		this.extensionInterface = extensionInterface;
		this.extensionClass = extensionClass;
		this.config = config;
		this.status = Status.NOT_LOADED;
	}
	
	/**
	 * Renvoie l'instance de l'extension
	 * 
	 * <p>
	 * 
	 * Instancie l'extension si elle ne l'est pas encore
	 * 
	 * @return l'extension
	 */
	public IExtension getExtension() {
		if (extension == null) {
			this.load();
		}
		return extension;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (method.getName().equals("load")) {
			return this.load();
		}
		
		if (method.getName().equals("kill")) {
			return this.kill();
		}
		
		if (method.getName().equals("getExtension")) {
			return this.getExtension();
		}
		
		if (method.getName().equals("getStatus")) {
			return this.status;
		}
		
		if (method.getName().equals("getConfig")) {
			return this.config;
		}
		
		if (method.getName().equals("isKillable")) {
			return this.config.isKillable();
		}
		
		if (method.getName().equals("isProxyOf")) {
			return this.extension != null && this.extension.getClass().getName().equals(args[0].getClass().getName());
		}
		
		if (method.getName().equals("equals")) {
			return this.equals(args[0]);
		}
		
		if (this.status.equals(Status.KILLED) || this.status.equals(Status.ERROR))  {
			this.load(); // Try to reload the extension
		}
		
		Object ret = null;
		try {
			ret = method.invoke(this.getExtension(), args);
		} catch (Exception e) {
			this.error(e);
		}
		
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.extension != null && this.extension.getClass().getName().equals(((IExtensionActions)o).getExtension().getClass().getName());
	}
	
	@Override
	public String toString() {
		return "[Container: "+this.config+"]";
	}
	
	/**
	 * Charge l'extension
	 * 
	 * <p>
	 * 
	 * Créer l'événement "extension.loaded"
	 * 
	 * @return Vrai si l'extension a été chargé, faux sinon
	 */
	private boolean load() {
		try {
			this.status = Status.LOADED;
			this.extension = (IExtension) extensionClass.newInstance();
			this.extension.start();
			Framework.event("extension.loaded", this.extensionClass);
			return true;
		} catch (Exception e) {
			this.error(e);
		}
		return false;
	}
	
	/**
	 * Kill l'extension
	 * 
	 * <p>
	 * 
	 * Créer l'événement "extension.killed"
	 * 
	 * @return Vrai si l'extension a été killed, faux sinon
	 */
	private boolean kill() {
		if (this.extension != null) {
			if (this.config.isKillable()) {
				Framework.unsubscribeEvent("*", this.extension);
				this.extension.stop();
				this.extension = null;
				this.status = Status.KILLED;
				Framework.event("extension.killed", this.extensionClass);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Change le status de l'extension en erreur
	 * 
	 * @param exception l'Exception qui en est la cause
	 */
	private void error(Exception exception) {
		this.status = Status.ERROR;
		Framework.event("extension.error", this.extensionClass);
		exception.printStackTrace();
	}
}

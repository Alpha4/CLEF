package framework;

/**
 * Interface définissant les extensions
 */
public interface IExtension {
	/**
	 * Méthode appelée au lancement de l'extension
	 */
	public void run();
	public void handleEvent(String name, Object event);
}

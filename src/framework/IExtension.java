package framework;

/**
 * Interface définissant les extensions
 */
public interface IExtension {
	
	/**
	 * Méthode appelée au lancement de l'extension
	 */
	void run();
	
	/**
	 * Méthode appelée après qu'un événement soit lancé
	 * @param name, correspondant au nom de l'événement
	 * @param event, correspondant à l'objet associé à l'événement
	 */
	void handleEvent(String name, Object event);
}

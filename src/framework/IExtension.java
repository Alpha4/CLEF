package framework;

/**
 * Interface définissant les extensions
 */
public interface IExtension {
	
	/**
	 * Méthode appelée au lancement de l'extension
	 */
	public void run();
	
	/**
	 * Méthode appelée après qu'un événement soit lancé
	 * @param String name, correspondant au nom de l'événement
	 * @param Object event, correspondant à l'objet associé à l'événement
	 */
	public void handleEvent(String name, Object event);
}

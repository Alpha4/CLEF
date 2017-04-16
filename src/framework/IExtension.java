package framework;

/**
 * Interface définissant les extensions
 */
public interface IExtension {
	
	/**
	 * Démarrage de l'extension (voir documentation: "life cycle des extensions")
	 */
	default void start() {};
	
	/**
	 * Arrêt de l'extension (voir documentation: "life cycle des extensions")
	 */
	default void stop() {};
	
	/**
	 * Méthode appelée après qu'un événement soit lancé
	 * @param name, correspondant au nom de l'événement
	 * @param event, correspondant à l'objet associé à l'événement
	 */
	default void handleEvent(String name, Object event) {};

}

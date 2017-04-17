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
	 * @param event, Event comprenant le nom et le payload
	 */
	default void handleEvent(Event event) {};

}

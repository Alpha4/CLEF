package framework;

/**
 * Interface définissant les extensions
 */
public interface IExtension {
	
	/**
	 * Démarrage de l'extension
	 * 
	 * <p>
	 * 
	 * Si l'extension est "autorun", cette méthode sera appellé au lancement
	 * de l'application<br>
	 * Sinon, cete méthode sera appellé au chargement de l'extension, c'est
	 * à dire la première fois qu'elle sera demandée par une autre extension
	 */
	default void start() {};
	
	/**
	 * Arrêt de l'extension
	 * 
	 * <p>
	 * 
	 * Cette méthode est appelé quand l'extension est "kill", manuellement ou non.
	 */
	default void stop() {};
	
	/**
	 * Gère les événements reçus
	 * 
	 * @param event Event comprenant le nom et le payload
	 */
	default void handleEvent(Event event) {};

}

package extensions.logger;

import framework.Event;
import framework.Framework;
import interfaces.IMonitoring;

/**
 * Affiche les logs dans la console
 * 
 * <p>
 * 
 * Evénements souscris:<br>
 * 		- *		Tous les événements<br>
 */
public class Logger implements IMonitoring {
	
	@Override
	public void start() {
		Framework.subscribeEvent("*", this);
	}
	
	@Override
	public void stop() {
		System.out.println("extension.killed => "+this.getClass());
		System.out.println("End of logs");
	}

	@Override
	public void handleEvent(Event event) {
		
		System.out.println(event);
	}
	
}

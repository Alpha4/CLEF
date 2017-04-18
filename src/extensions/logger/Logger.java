package extensions.logger;

import framework.Event;
import framework.Framework;
import interfaces.IMonitoring;

/**
 * Affiche les logs dans la console
 */
public class Logger implements IMonitoring {
	
	@Override
	public void start() {
		Framework.subscribeEvent("*", this);
	}
	
	@Override
	public void stop() {
		System.out.println(this.getClass()+" : killed");
		System.out.println("End of logs");
	}

	@Override
	public void handleEvent(Event event) {
		
		if (event.is("extension")) {
			String[] cats = event.getName().split("\\.");
			System.out.println(event.getPayload()+" : "+cats[cats.length-1]);
		} else if (event.is("message")) {
			System.out.println("Message: "+(String)event.getPayload());
		}
		
	}
	
}

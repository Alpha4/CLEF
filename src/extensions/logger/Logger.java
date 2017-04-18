package extensions.logger;

import framework.Event;
import framework.Framework;
import interfaces.IMonitoring;

public class Logger implements IMonitoring {
	
	public void start() {
		Framework.subscribeEvent("extension", this);
	}
	
	public void stop() {
		System.out.println(this.getClass()+" : killed");
		System.out.println("End of logs");
	}

	public void handleEvent(Event event) {
		
		if (event.is("extension")) {
			String[] cats = event.getName().split("\\.");
			System.out.println(event.getPayload()+" : "+cats[cats.length-1]);
		}
		
	}
	
}

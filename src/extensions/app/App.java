package extensions.app;

import java.util.List;
import java.util.Map.Entry;

import framework.ExtensionActions;
import framework.Framework;
import framework.IExtension;
import framework.plugin.IAffichage;
import framework.plugin.IApp;
import framework.plugin.IGUI;
import framework.plugin.IMonitoring;

public class App implements IApp {

	public void run() {
		
		// Get monitoring
		IMonitoring monitor = (IMonitoring) Framework.getExtension(IMonitoring.class);
		
		System.out.println(((ExtensionActions)monitor).getDescription());
		
		// Show status
		for(Entry<Class<?>,String> status : monitor.getExtensionsStatus().entrySet()) {
			System.out.println(status.getKey().getName()+": "+status.getValue());
		}
		
		//trying to load GUI
		IGUI gui = (IGUI) Framework.getExtension(IGUI.class);
		
		
		// Print string
		List<IExtension> tests = Framework.get(IAffichage.class);
		IAffichage test = (IAffichage) tests.get(0);
		test.print("salut!");
		
		// Show status again
		for(Entry<Class<?>,String> status : monitor.getExtensionsStatus().entrySet()) {
			System.out.println(status.getKey().getName()+": "+status.getValue());
		}
		
		/*((ExtensionActions)test).kill();
		test.print("Yo!");*/
	}
}

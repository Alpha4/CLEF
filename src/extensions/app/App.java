package extensions.app;

import java.util.List;
import java.util.Map.Entry;

import framework.Framework;
import framework.IExtension;
import framework.ExtensionActions;
import framework.plugin.IApp;
import framework.plugin.IMonitoring;
import framework.plugin.IAffichage;

public class App implements IApp {

	public void run() {
		
		// Get monitoring
		IMonitoring monitor = (IMonitoring) Framework.getExtension(IMonitoring.class);
		
		System.out.println(((ExtensionActions)monitor).getDescription());
		
		// Show status
		for(Entry<Class<?>,String> status : monitor.getExtensionsStatus().entrySet()) {
			System.out.println(status.getKey().getName()+": "+status.getValue());
		}
		
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

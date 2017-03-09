package extensions.app;

import java.util.List;
import java.util.Map.Entry;

import framework.ExtensionContainer;
import framework.Framework;
import framework.plugin.IApp;
import framework.plugin.IMonitoring;
import framework.plugin.IAffichage;

public class App implements IApp {

	public void run() {
		
		// Get monitoring
		IMonitoring monitor = (IMonitoring) Framework.getExtension(IMonitoring.class);
		
		// Show status
		for(Entry<Class<?>,String> status : monitor.getExtensionsStatus().entrySet()) {
			System.out.println(status.getKey().getName()+": "+status.getValue());
		}
		
		// Print string
		String str = "salut!";
		List<ExtensionContainer> tests = Framework.get(IAffichage.class);
		IAffichage test = (IAffichage) tests.get(0).getExtension();
		test.print(str);
		
		// Show status again
		for(Entry<Class<?>,String> status : monitor.getExtensionsStatus().entrySet()) {
			System.out.println(status.getKey().getName()+": "+status.getValue());
		}
	}
}

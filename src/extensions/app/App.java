package extensions.app;

import java.util.List;

import framework.Config;
import framework.Framework;
import framework.IAutorunExtension;
import framework.plugin.IApp;

import framework.plugin.IAffichage;

public class App implements IAutorunExtension, IApp {

	public void run() {
		
		String str = "salut!";
		
		List<Config> tests = Framework.get(IAffichage.class);
		
		IAffichage test = (IAffichage) Framework.get(IAffichage.class, tests.get(0));
		
		test.print(str);
	}
	
}

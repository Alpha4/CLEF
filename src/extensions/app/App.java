package extensions.app;

import java.util.List;

import framework.ExtensionContainer;
import framework.Framework;
import framework.IAutorunExtension;
import framework.plugin.IApp;

import framework.plugin.IAffichage;

public class App implements IAutorunExtension, IApp {

	public void run() {
		
		String str = "salut!";
		
		List<ExtensionContainer> tests = Framework.get(IAffichage.class);
		
		IAffichage test = (IAffichage) tests.get(0).getExtension();
		
		test.print(str);
	}
	
}

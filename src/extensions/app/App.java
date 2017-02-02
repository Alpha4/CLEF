package extensions.app;

import framework.Framework;
import framework.IAppExtension;

import extensions.test.Test;

public class App implements IAppExtension {

	public void run() {
		
		String str = "salut!";
		
		Test test = (Test) Framework.get(Test.class);
		
		test.echo(str);
	}
	
}

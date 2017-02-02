package extensions.test;

import framework.IExtension;

public class Test implements IExtension {
	
	public void echo(String str) {
		System.out.println(str);
	}

}

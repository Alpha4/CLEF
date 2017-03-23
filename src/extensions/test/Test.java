package extensions.test;

import framework.IExtension;
import framework.plugin.IAffichage;

public class Test implements IAffichage{
	
	public void print(String str) {
		System.out.println(str);
	}

}

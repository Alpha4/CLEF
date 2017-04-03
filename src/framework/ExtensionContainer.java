package framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ExtensionContainer implements InvocationHandler {

	Config meta;
	IExtension extension;
	String status;
	Class<?> extensionClass;
	
	public ExtensionContainer(Class<?> extensionClass, Config meta) {
		super();
		this.extensionClass = extensionClass;
		this.meta = meta;
		this.status = Status.NOT_LOADED;
	}
	
	private String getStatus(){
		return this.status;
	}
	
	private IExtension getExtension() {
		if (extension == null) {
			this.load();
		}
		return extension;
	}
	
	public String toString() {
		return "[Container: "+this.meta+"]";
	}
	
	private void load() {
		try {
			this.status = Status.LOADED;
			this.extension = (IExtension) extensionClass.newInstance();
			Framework.event("extension.loaded", this.extensionClass);
		} catch (Exception e) {
			e.printStackTrace();
			this.status = Status.ERROR;
		}
	}
	
	private void kill() {
		if (this.meta.isKillable()) {
			this.extension = null;
			this.status = Status.KILLED;
		} else {
			System.out.println("This extension can't be killed");
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (method.getName().equals("load")) {
			this.load();
			return null;
		}
		
		if (method.getName().equals("kill")) {
			this.kill();
			return null;
		}
		
		if (method.getName().equals("getStatus")) {
			return this.getStatus();
		}
		
		if (method.getName().equals("getDescription")) {
			return this.meta.getDescription();
		}
		
		if (this.status.equals(Status.KILLED) ||
				this.status.equals(Status.ERROR))  {
			throw new KilledExtensionException();
		}
		
		return method.invoke(this.getExtension(), args);
	}
}

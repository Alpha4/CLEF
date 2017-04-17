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
	
	public IExtension getExtension() {
		if (extension == null) {
			this.load();
		}
		return extension;
	}
	
	public String toString() {
		return "[Container: "+this.meta+"]";
	}
	
	private void log(String message) {
		System.out.println(this.extensionClass.getName()+" : "+message);
	}
	
	private void load() {
		try {
			this.status = Status.LOADED;
			this.extension = (IExtension) extensionClass.newInstance();
			this.extension.start();
			Framework.event("extension.loaded", this.extensionClass);
			this.log("loaded");
		} catch (Exception e) {
			this.status = Status.ERROR;
			Framework.event("extension.error", this.extensionClass);
			this.log("error ("+e.getClass()+": "+e.getMessage()+")");
			e.printStackTrace();
		}
	}
	
	private void kill() {
		if (this.extension != null) {
			if (this.meta.isKillable()) {
				Framework.unsubscribeEvent("*", this.extension);
				this.extension.stop();
				this.extension = null;
				this.status = Status.KILLED;
				Framework.event("extension.killed", this.extensionClass);
				this.log("killed");
			} else {
				this.log("This extension can't be killed");
			}
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (method.getName().equals("load")) {
			this.load();
			return null;
		}
		
		if (method.getName().equals("kill")) {
			this.kill();
			return null;
		}
		
		if (method.getName().equals("getExtension")) {
			return this.getExtension();
		}
		
		if (method.getName().equals("getStatus")) {
			return this.getStatus();
		}
		
		if (method.getName().equals("getDescription")) {
			return this.meta.getDescription();
		}
		
		if (method.getName().equals("isProxyOf")) {
			return this.extension != null && this.extension.getClass().getName().equals(args[0].getClass().getName());
		}
		
		if (method.getName().equals("equals")) {
			return this.equals(args[0]);
		}
		
		if (this.status.equals(Status.KILLED) || this.status.equals(Status.ERROR))  {
			this.load(); // Try to reload the extension
		}
		
		return method.invoke(this.getExtension(), args);
	}
	
	public boolean equals(Object o) {
		return this.extension != null && this.extension.getClass().getName().equals(((IExtensionActions)o).getExtension().getClass().getName());
	}
}

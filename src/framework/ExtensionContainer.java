package framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class ExtensionContainer implements InvocationHandler {

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
	
	private boolean load() {
		try {
			this.status = Status.LOADED;
			this.extension = (IExtension) extensionClass.newInstance();
			this.extension.start();
			Framework.event("extension.loaded", this.extensionClass);
			return true;
		} catch (Exception e) {
			this.error(e);
		}
		return false;
	}
	
	private boolean kill() {
		if (this.extension != null) {
			if (this.meta.isKillable()) {
				Framework.unsubscribeEvent("*", this.extension);
				this.extension.stop();
				this.extension = null;
				this.status = Status.KILLED;
				Framework.event("extension.killed", this.extensionClass);
				return true;
			}
		}
		
		return false;
	}
	
	private void error(Exception e) {
		this.status = Status.ERROR;
		Framework.event("extension.error", this.extensionClass);
		e.printStackTrace();
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (method.getName().equals("load")) {
			return this.load();
		}
		
		if (method.getName().equals("kill")) {
			return this.kill();
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
		
		if (method.getName().equals("isKillable")) {
			return this.meta.isKillable();
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
		
		Object ret = null;
		try {
			ret = method.invoke(this.getExtension(), args);
		} catch (Exception e) {
			this.error(e);
		}
		
		return ret;
	}
	
	public boolean equals(Object o) {
		return this.extension != null && this.extension.getClass().getName().equals(((IExtensionActions)o).getExtension().getClass().getName());
	}
}

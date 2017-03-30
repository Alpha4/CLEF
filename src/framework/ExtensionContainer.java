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
		this.extension = null;
		this.status = Status.KILLED;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (method.getName() == "load") {
			this.load();
			return null;
		}
		
		if (method.getName() == "kill") {
			this.kill();
			return null;
		}
		
		if (method.getName() == "getStatus") {
			return this.getStatus();
		}
		
		if (method.getName() == "getDescription") {
			return this.meta.getDescription();
		}
		
		if (this.status == Status.KILLED || 
			this.status == Status.ERROR)  {
			throw new KilledExtensionException();
		}
		
		return method.invoke(this.getExtension(), args);
	}
}

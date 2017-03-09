package framework;

public class ExtensionContainer {

	Config meta;
	IExtension extension;
	String status;
	Class<?> extensionClass;
	
	public ExtensionContainer(Class<?> extensionClass, Config meta) {
		super();
		this.extensionClass = extensionClass;
		this.meta = meta;
		this.status = "Not loaded";
	}
	
	public Config getMeta() {
		return meta;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public void setMeta(Config meta) {
		this.meta = meta;
	}
	
	public IExtension getExtension() {
		if (extension == null) {
			try {
				this.status = "Loaded";
				return (IExtension) extensionClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				this.status = "Error while loading";
			}
		}
		return extension;
	}
	
	public void setExtension(IExtension extension) {
		this.extension = extension;
	}
	
	public void setExtensionClass(Class<?> extensionClass) {
		this.extensionClass = extensionClass;
	}
	
	public Class<?> getExtensionClass() {
		return this.extensionClass;
	}
	
	public String toString() {
		return "[Container: "+this.meta+"]";
	}
	
}

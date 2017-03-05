package framework;

public class ExtensionContainer {

	Config meta;
	IExtension extension;
	
	public ExtensionContainer(Config meta) {
		super();
		this.meta = meta;
	}
	
	public Config getMeta() {
		return meta;
	}
	
	public void setMeta(Config meta) {
		this.meta = meta;
	}
	
	public IExtension getExtension() {
		if (extension == null) {
			try {
				Class<?> cl = Class.forName(meta.getClasspath());
				return (IExtension) cl.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return extension;
	}
	
	public void setExtension(IExtension extension) {
		this.extension = extension;
	}
	
	public String toString() {
		return "[Container: "+this.meta+"]";
	}
	
}

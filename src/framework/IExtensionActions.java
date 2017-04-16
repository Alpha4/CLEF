package framework;

public interface IExtensionActions {
	void load();
	void kill();
	IExtension getExtension();
	String getStatus();
	String getDescription();
	boolean isProxyOf(IExtension extension);
}

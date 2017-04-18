package framework;

import java.util.ArrayList;
import java.util.List;

public class Config {

	String name;
	String description;
	String type;
	boolean autorun = false;
	boolean killable = true;
	List<String> extensions = new ArrayList<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Config [name=" + name + ", description=" + description
				+ ", extensions=" + extensions + ", autorun=" + autorun + "]";
	}
	public List<String> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isAutorun() {
		return this.autorun;
	}
	public void setAutorun(boolean autorun) {
		this.autorun = autorun;
	}
	public boolean isKillable() {
		return this.killable;
	}
	public void setKillable(boolean killable) {
		this.killable = killable;
	}
}

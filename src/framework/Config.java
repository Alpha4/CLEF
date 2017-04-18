package framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration de l'application ou d'une extension
 */
public class Config {

	String name;
	String description;
	String type;
	boolean autorun = false;
	boolean killable = true;
	List<String> extensions = new ArrayList<String>();
	
	/**
	 * Accesseur de name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Mutateur de name
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Accesseur de description
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Mutateur de description
	 * 
	 * @param description description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Accesseur d'extensions
	 * 
	 * @return extensions
	 */
	public List<String> getExtensions() {
		return extensions;
	}
	
	/**
	 * Mutateur d'extensions
	 * 
	 * @param extensions extensions
	 */
	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}
	
	/**
	 * Accesseur de type
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Mutateur de type
	 * 
	 * @param type type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Accesseur d'autorun
	 * 
	 * @return autorun
	 */
	public boolean isAutorun() {
		return this.autorun;
	}
	
	/**
	 * Mutateur d'autorun
	 * 
	 * @param autorun autorun
	 */
	public void setAutorun(boolean autorun) {
		this.autorun = autorun;
	}
	
	/**
	 * Accesseur de killable
	 * 
	 * @return killable
	 */
	public boolean isKillable() {
		return this.killable;
	}
	
	/**
	 * Mutateur de killable
	 * 
	 * @param killable killable
	 */
	public void setKillable(boolean killable) {
		this.killable = killable;
	}

	@Override
	public String toString() {
		return "Config [name=" + name + ", description=" + description
				+ ", extensions=" + extensions + ", autorun=" + autorun + "]";
	}
}

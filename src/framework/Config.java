package framework;

import java.util.ArrayList;
import java.util.List;

public class Config {

	String name;
	String description;
	String base;
	String classpath;
	List<String> dependencies = new ArrayList<String>();
	
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
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	@Override
	public String toString() {
		return "Config [name=" + name + ", description=" + description
				+ ", base=" + base + "]";
	}
	public List<String> getDependencies() {
		return dependencies;
	}
	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}
	public String getClasspath() {
		return classpath;
	}
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
}

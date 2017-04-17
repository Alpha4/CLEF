package framework;

import java.util.Arrays;

public class Event {

	private String name;
	private Object payload;
	
	public Event(String name, Object payload) {
		this.name = name;
		this.payload = payload;
	}
	
	public boolean is(String otherName) {
		if (name.equals(otherName))
			return true;
		
		String[] parts = name.split("\\.");
		String[] otherParts = otherName.split("\\.");
		
		for (int i = 0; i < Math.min(parts.length, otherParts.length); i++) {
			if (!otherParts[i].equals("*") && !parts[i].equals(otherParts[i])) {
				return false;
			}
		}
		
		return otherParts.length <= parts.length;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getPayload() {
		return payload;
	}
	
}

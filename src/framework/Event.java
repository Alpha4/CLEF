package framework;

/**
 * Evénement contenant un nom et un payload
 */
public class Event {

	private String name;
	private Object payload;
	
	/**
	 * Constructeur par défaut
	 * 
	 * @param name		Nom de l'événement
	 * @param payload	Payload associé à l'événement
	 */
	public Event(String name, Object payload) {
		this.name = name;
		this.payload = payload;
	}
	
	/**
	 * Permet de savoir si l'événement correspond au type donné en paramètre
	 * 
	 * <p>
	 * 
	 * Exemples:<br>
	 * <code>
	 * Event event = new Event("network.client.connected", null);<br>
	 * <br>
	 * event.is("network.client.connected"); // True<br>
	 * event.is("network.client.disconnected"); // False<br>
	 * event.is("network.client"); // True<br>
	 * event.is("network.server"); // False<br>
	 * event.is("network"); // True<br>
	 * event.is("network.*"); // True<br>
	 * event.is("network.*.connected"); // True<br>
	 * event.is("network.*.disconnected"); // False<br>
	 * </code>
	 * 
	 * @param type	Type d'événement à comparer
	 * @return		Vrai si l'événement correspond au type, faux sinon
	 */
	public boolean is(String type) {
		if (name.equals(type))
			return true;
		
		String[] parts = name.split("\\.");
		String[] otherParts = type.split("\\.");
		
		for (int i = 0; i < Math.min(parts.length, otherParts.length); i++) {
			if (!otherParts[i].equals("*") && !parts[i].equals(otherParts[i])) {
				return false;
			}
		}
		
		return otherParts.length <= parts.length;
	}
	
	/**
	 * Renvoie le nom de l'événement
	 * 
	 * @return le nom de l'événement
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Renvoie le payload de l'événement
	 * 
	 * @return le payload de l'événement
	 */
	public Object getPayload() {
		return payload;
	}
	
	@Override
	public String toString() {
		return name+" => "+(payload != null ? payload : "");
	}
	
}

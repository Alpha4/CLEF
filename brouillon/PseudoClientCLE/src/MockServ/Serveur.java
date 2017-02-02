package MockServ;


import Client.IServeur;

public class Serveur implements IServeur{


	public String receiveAndSend(String s) {
		
		String message;
		message = "[serv] : " + s;
		
		return message;
	}

	
	
}

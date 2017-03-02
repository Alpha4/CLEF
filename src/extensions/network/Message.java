package extensions.network;

import framework.plugin.IMessage;

public class Message implements IMessage {
	private String author;
	private String plainText;
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getPlainText() {
		return plainText;
	}
	
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	
	
}

package extensions.network;

import framework.plugin.IMessage;

public class Message implements IMessage {
	private String author;
	private String plainText;
	
	@Override
	public String getAuthor() {
		return author;
	}
	@Override
	public void setAuthor(String author) {
		this.author = author;
	}
	@Override
	public String getPlainText() {
		return plainText;
	}
	@Override
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	
	
}

package extensions.network;


public interface IMessage {

	String getAuthor();

	void setAuthor(String author);

	String getPlainText();

	void setPlainText(String plainText);

}
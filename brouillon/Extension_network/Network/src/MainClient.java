import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class MainClient {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		
		INetwork net = Network.getInstance();
		
		Message m = new Message();
		m.setAuthor("Auré");
		m.setPlainText("super test");
		
		net.send(m, InetAddress.getByName("localhost"));
		
	}

}

package extensions.consoleGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import framework.Event;
import framework.Framework;
import interfaces.IGUI;
import interfaces.INetworkClient;
import interfaces.INetworkServer;

/**
 * Interface console pour le chat
 */
public class ConsoleGUI implements IGUI {

	private INetworkClient client;
	private INetworkServer server;
	private String applicationName;
	private JFrame mainFrame;
	private JPanel mainPanel;
	private JTextArea chatText;
	private JTextField chatLine;
	
	private boolean isHost;
	
	private int state;
	private static final int ASKING_TYPE = 1;
	private static final int ASKING_PSEUDO = 2;
	private static final int ASKING_ADDRESS = 3;
	private static final int ASKING_PORT = 4;
	private static final int NOT_CONNECTED = 5;
	private static final int CONNECTED = 6;
	
	// Public functions

	@Override
	public void start() {
		
		// Récupère le nom de l'application pour le titre de la fenêtre
		applicationName = Framework.getConfig().getName();
		
		// Récupère les informations courantes
		client = Framework.getExtension(INetworkClient.class);
		server = Framework.getExtension(INetworkServer.class);
		
		initWindow();
		
		state = NOT_CONNECTED;
		
		isHost = server.isStarted();
		
		if (!client.isConnected()) {
			println("Welcome to "+applicationName+"!\n");
			askType();
		} else {
			state = CONNECTED;
			println("\n-- Connected --\n");
		}
		
		// Subscribe to events
		Framework.subscribeEvent("network", this);
		Framework.subscribeEvent("message", this);
	}
	
	@Override
	public void handleEvent(Event event) {

		if (event.is("network.client.connected")) {
			this.state = CONNECTED;
			println("\n-- Connected --\n");
		} else if (event.is("network.client.disconnected")) {
			this.state = NOT_CONNECTED;
			println("\n-- Disconnected --\n");
			askType();
		} else if (event.is("network.client.failed")) {
			this.state = NOT_CONNECTED;
			println("Error: "+((String)event.getPayload()));
			askType();
		}  else if (event.is("network.server.failed")) {
			this.state = NOT_CONNECTED;
			println("Error: "+((String)event.getPayload()));
		} else if (event.is("message.received")) {
			println((String)event.getPayload());
		} else if (event.is("message.sent")) {
			println((String)event.getPayload());
		}
	}
	
	// Private functions

	/**
	 * Créer la JFrame et le panneau principal
	 */
	private void initWindow() {
		
		mainFrame = new JFrame(applicationName);
		mainFrame.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				Framework.exit();
			}
		});
		
		mainPanel = new JPanel(new BorderLayout());
		
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		chatLine = new JTextField();
		//listener pour envoyer les messages avec entrée
		chatLine.addKeyListener(new KeyboardListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					readLine();
				}
			}
		});
		
		mainPanel.add(new JScrollPane(chatText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		mainPanel.add(chatLine, BorderLayout.SOUTH);
		
		mainFrame.setContentPane(mainPanel);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	/**
	 * Ajoute une ligne
	 * 
	 * @param line ligne à ajouter
	 */
	private void print(String line) {
		chatText.setText(chatText.getText()+line);
		
		mainFrame.repaint();
	}
	
	/**
	 * Ajoute une ligne avec un retour à la ligne
	 * 
	 * @param line ligne à ajouter
	 */
	private void println(String line) {
		print(line+"\n");
	}
	
	/**
	 * Lis la ligne écrite par l'utilisateur
	 *
	 * <p>
	 * 
	 * Effectue différentes actions en fonction
	 */
	private void readLine() {

		String line = chatLine.getText();
		
		if (this.state != NOT_CONNECTED) {
			chatLine.setText("");
		}
		
		if (this.state == ASKING_TYPE) {
			println(line);
			char type = line.charAt(0);
			if (type != 'g' && type != 'G' && type != 'h' && type != 'H') {
				askType();
			} else {
				isHost = type == 'h' || type == 'H';
				askPseudo();
			}
		} else if (this.state == ASKING_PSEUDO) {
			println(line);
			client.setPseudo(line);
			askAddress();
		} else if (this.state == ASKING_ADDRESS) {
			println(line);
			client.setAddress(line);
			askPort();
		} else if (this.state == ASKING_PORT) {
			println(line);
			this.state = NOT_CONNECTED;
			int port = Integer.parseInt(line);
			client.setPort(port);
			if (isHost) {
				server.setPort(port);
				server.startServer();
			}
			client.connect();
		} else if (this.state == CONNECTED) {
			if (line.equals("/exit")) {
				disconnect();
			} else {
				sendMessage(line);
			}
		}
	}
	
	/**
	 * Déconnecte le client ou le serveur
	 */
	private void disconnect() {
		if (isHost) {
			server.stopServer();
		} else {
			client.disconnect();
		}
	}
	
	/**
	 * Demande le type de connexion voulue
	 */
	private void askType() {
		state = ASKING_TYPE;
		print("Type of connection (H: Host / G: Guest): ");
	}
	
	/**
	 * Demande le pseudo voulue
	 */
	private void askPseudo() {
		state = ASKING_PSEUDO;
		print("Pseudo: ");
	}
	
	/**
	 * Demande l'addresse de connection
	 */
	private void askAddress() {
		state = ASKING_ADDRESS;
		print("Address: ");
	}
	
	/**
	 * Demande le port de connection
	 */
	private void askPort() {
		state = ASKING_PORT;
		print("Port: ");
	}
	
	/**
	 * Envoi un message
	 * 
	 * @param message message à envoyer
	 */
	private void sendMessage(String message) {

		if (!message.isEmpty()){
		
			String newMessage = client.getPseudo() + " : " + message;
			chatLine.setText("");			
			client.send(newMessage);
		}
	}
	
	/**
	 * Adapter pour un nouveau KeyboardListener facilement
	 */
	class KeyboardListener implements KeyListener {

		public void keyPressed(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}

		public void keyTyped(KeyEvent e) {}

	}
}
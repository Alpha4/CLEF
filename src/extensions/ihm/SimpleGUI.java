package extensions.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import framework.Framework;
import framework.plugin.IGUI;
import framework.plugin.INetworkClient;
import framework.plugin.INetworkServer;

public class SimpleGUI implements IGUI {
	// Constantes d'informations de connection
	final static int DISCONNECTED = 0;
	final static int BEGIN_CONNECT = 1;
	final static int CONNECTED = 2;

	// Informations et composants de la GUI
	private JFrame mainFrame;
	private JTextArea chatText;
	private JTextField chatLine;
	private JLabel statusBar;
	private JTextField ipField;
	private JTextField portField;
	private JTextField pseudoField;
	private JRadioButton hostOption;
	private JRadioButton guestOption;
	private JButton connectButton;
	private JButton disconnectButton;
	private JButton sendButton;

	// Informations de connection
	private String hostIP;
	private int connectionStatus;
	private int port;
	private boolean isHost;
	private String pseudo;

	// Client et Server
	private INetworkClient inetwork;
	private INetworkServer iserver;


	// Constructeur
	public SimpleGUI(){

		hostIP = "localhost";
		port = 1337;
		setConnectionStatus(DISCONNECTED);
		setHost(true);
		pseudo = "pseudo";

	}

	/**
	 * Generation du panneau de gauche (infos/options) 
	 * @return un JPanel
	 */
	private JPanel initOptionsPane() {

		JPanel pane = null;
		ActionAdapter buttonListener = null;

		// Création du panneau d'options
		JPanel optionsPane = new JPanel(new GridLayout(5, 1));

		// Pour le pseudo
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Pseudo:"));
		pseudoField = new JTextField(15); pseudoField.setText(pseudo);
		pseudoField.setEditable(true);
		pane.add(pseudoField);
		optionsPane.add(pane);   

		// Pour l'adresse IP
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(15); ipField.setText(hostIP);
		ipField.setEditable(true);
		pane.add(ipField);
		optionsPane.add(pane);
		
		// Pour le port
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10); portField.setEditable(true);
		portField.setText((new Integer(port)).toString());
		pane.add(portField);
		optionsPane.add(pane);
		
		// Host/guest options
		ButtonGroup bg = new ButtonGroup();
		hostOption = new JRadioButton("Host", true);
		hostOption.setMnemonic(KeyEvent.VK_H);
		guestOption = new JRadioButton("Guest", false);
		guestOption.setMnemonic(KeyEvent.VK_G);
		bg.add(hostOption);
		bg.add(guestOption);
		pane = new JPanel(new GridLayout(1, 2));
		pane.add(hostOption);
		pane.add(guestOption);
		optionsPane.add(pane); 

		// Connect/disconnect boutons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		
		// Le Listener pour les boutons
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// Quand le bouton "Connect" est pressé
				if (e.getActionCommand().equals("connect")) {
					
					port = Integer.parseInt(portField.getText());
					
					// Si le RadioButton Host est sélectionné, on paramètre le port du serveur.
					if (hostOption.isSelected() == true) {						
						iserver = (INetworkServer) Framework.getExtension(INetworkServer.class);
						iserver.setPort(port);
						iserver.run();						
					}
					
					// Activation du chat / désactivation des options
					chatLine.setEnabled(true);
					sendButton.setEnabled(true);
					disconnectButton.setEnabled(true);
					connectButton.setEnabled(false);
					hostOption.setEnabled(false);
					guestOption.setEnabled(false);
					ipField.setEnabled(false);
					pseudoField.setEnabled(false);
					
					// Paramétrage du Client
					inetwork = (INetworkClient) Framework.getExtension(INetworkClient.class);
					inetwork.setPort(port);
					inetwork.setServer(ipField.getText());
					
					setConnectionStatus(BEGIN_CONNECT);

					statusBar.setText("Online");
					setPseudo(getPseudoField().getText());

					mainFrame.repaint();	
					

				}
				// Quand le bouton "Disconnect" est pressé
				if (e.getActionCommand().equals("disconnect")){
					
					// Stop le server ou le client en focntion du RadioButton coché
					if (hostOption.isSelected() == true) {
						iserver.stopServer();						
					}else{
						inetwork.stopClient();
					}
					// Désactivation du chat / activation des options
					connectButton.setEnabled(true);
					ipField.setEnabled(true);
					hostOption.setEnabled(true);
					guestOption.setEnabled(true);
					pseudoField.setEnabled(true);
					sendButton.setEnabled(false);
					disconnectButton.setEnabled(false);

					setConnectionStatus(DISCONNECTED);
					
					chatLine.setText(""); 
					chatLine.setEnabled(false);

					statusBar.setText("Offline");

					mainFrame.repaint();
				}
				
				// Si le bouton "Send" est pressé
				if (e.getActionCommand().equals("send")){
					sendMessage();
					mainFrame.repaint();
				}
			}
		};
		
		//"Connect" Bouton
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		
		//"Disconnect" Bouton
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setMnemonic(KeyEvent.VK_D);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		
		//"Send" Bouton
		sendButton = new JButton("Send");
		sendButton.setMnemonic(KeyEvent.VK_ENTER);
		sendButton.setActionCommand("send");
		sendButton.addActionListener(buttonListener);
		sendButton.setEnabled(false);
		
		//ajout des boutons au panel
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		buttonPane.add(sendButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}
	
	/**
	 * Retourne le port
	 * @return port 
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Défini le port
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Initialisation du GUI complet
	 */
	public void initGUI() {
		// Création de la barre de status
		statusBar = new JLabel();
		statusBar.setText("Offline");

		// On initialise le panneau des options
		JPanel optionsPane = initOptionsPane();

		// Création du panneau de chat
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);      
		
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));

		//listener pour envoyer les messages avec entrée
		KeyboardListener keyListener = new KeyboardListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		};
		
		// On ajoute le KeyListener à la chatLine
		chatLine.addKeyListener(keyListener);

		// Création de la fenetre principale
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);
		mainFrame = new JFrame(Framework.getConfig().getName());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/**
	 * Méthode appelée par les autres extensions pour informer qu'on a reçu un message
	 */
	public void receiveMessage(String message) {

		String chat = chatText.getText();
		String newChat = chat + "\n" + message;

		chatText.setText(newChat);
		mainFrame.repaint();

	}

	/**
	 * Méthode envoyant un message, reléguant le destinataire à NetworkClient
	 */
	public void sendMessage() {

		String message = chatLine.getText();

		if (!message.isEmpty()){

			String chat = chatText.getText();
			String newChat = chat + "\n" + pseudo + " : " + message;			
			String newMessage = pseudo + " : " + message;
			chatText.setText(newChat);
			chatLine.setText("");			
			inetwork.send(newMessage);
		}

	}

	/**
	 * Retourne la statusBar
	 * @return JLabel statusBar
	 */
	public JLabel getStatusBar() {
		return statusBar;
	}

	/**
	 * Retourne le champ du pseudo
	 * @return JTextField pseudoField
	 */
	public JTextField getPseudoField() {
		return pseudoField;
	}

	/**
	 * Défini l'IP
	 * @param hostIP
	 */
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	/**
	 * Défini le pseudo
	 * @param pseudo
	 */
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	/**
	 * Retourne le status de la connexion
	 * @return
	 */
	public int getConnectionStatus() {
		return connectionStatus;
	}
	
	/**
	 *  Défini le status de la connexion
	 * @param connectionStatus
	 */
	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	/**
	 * Retourne le booléen indiquant si on est en mode "Host"
	 * @return
	 */
	public boolean isHost() {
		return isHost;
	}

	/**
	 * Défini si on est en mode "Host" ou non, pour les radioButtons
	 * @param isHost
	 */
	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}


	@Override
	/**
	 * Méthode a exécuter au lancement de l'extension
	 */
	public void run() {
		this.initGUI();
		Framework.handleEvent("message.received", this);
	}
	
	@Override
	public void handleEvent(String name, Object event) {
		
		if (name == "message.received") {
			this.receiveMessage((String)event);
		}
		
	}

}


// Action adapter for easy event-listener coding
class ActionAdapter implements ActionListener {
	public void actionPerformed(ActionEvent e) {}
}
//Keyboard listener for easy key-listener coding
class KeyboardListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}



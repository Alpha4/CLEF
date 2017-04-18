package extensions.simpleGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import framework.Event;
import framework.Framework;
import interfaces.IGUI;
import interfaces.INetworkClient;
import interfaces.INetworkServer;

/**
 * Interface graphique pour le chat
 */
public class SimpleGUI implements IGUI {
	
	private String applicationName;
	
	private String pseudo;
	private String address;
	private int port;
	private boolean isHost;
	
	private INetworkServer server;
	private INetworkClient client;
	
	private JFrame mainFrame;
	private JPanel mainPanel;
	
	private JPanel optionsPanel;
	private JTextField pseudoField;
	private JTextField addressField;
	private JTextField portField;
	private JRadioButton hostOption;
	private JRadioButton guestOption;
	private JButton connectButton;
	private JButton disconnectButton;
	private JButton sendButton;
	
	private JPanel chatPanel;
	private JTextArea chatText;
	private JTextField chatLine;
	
	private JLabel statusBar;
	
	// Public methods

	@Override
	public void start() {
		
		// Récupère le nom de l'application pour le titre de la fenêtre
		applicationName = Framework.getConfig().getName();
		
		// Récupère les informations courantes
		client = (INetworkClient) Framework.getExtension(INetworkClient.class);
		pseudo = client.getPseudo();
		address = client.getAddress();
		port = client.getPort();
		
		server = (INetworkServer) Framework.getExtension(INetworkServer.class);
		isHost = server.isStarted();
		
		// Créer la fenêtre
		initWindow();
		
		// Adjust to status
		if (client.isConnected()) {
			connected();
			receiveMessage("\n-- Connected --\n");
		}
		
		// Subscribe to events
		Framework.subscribeEvent("network", this);
		Framework.subscribeEvent("message.received", this);
	}
	
	@Override
	public void stop() {
		
		// Ferme la fenêtre
		mainFrame.setVisible(false);
		mainFrame.dispose();
		mainFrame = null;
	}
	
	@Override
	public void handleEvent(Event event) {
		
		if (event.is("network.client.connected")) {
			
			connected();
			receiveMessage("\n-- Connected --\n");
			
		} else if (event.is("network.client.disconnected")) {
			
			disconnected();
			receiveMessage("\n-- Disconnected --\n");
			
		} else if (event.is("network.client.failed")) {
			
			receiveMessage("Error: "+((String)event.getPayload()));
			disconnected();
			
		}  else if (event.is("network.server.failed")) {
			
			receiveMessage("Error: "+((String)event.getPayload()));
			disconnected();
			
		} else if (event.is("message.received")) {
			
			receiveMessage((String)event.getPayload());
			
		}
		
	}
	
	// Private methods
	
	/**
	 * Créer la JFrame et le panneau principal
	 * 
	 * Appel {@link #initOptionsPanel()}, {@link #initChatPanel()}, {@link #initStatusBar()}
	 */
	private void initWindow() {
		
		mainFrame = new JFrame(applicationName);
		mainFrame.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				Framework.exit();
			}
		});
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(initOptionsPanel(), BorderLayout.WEST);
		mainPanel.add(initChatPanel(), BorderLayout.CENTER);
		mainPanel.add(initStatusBar(), BorderLayout.SOUTH);
		
		mainFrame.setContentPane(mainPanel);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	/**
	 * Créer le JPanel d'options et ses composants
	 * 
	 * @return le JPanel d'options
	 */
	private JPanel initOptionsPanel() {
		
		JPanel panel = null;
		
		// Création du panneau d'options
		optionsPanel = new JPanel(new GridLayout(5, 1));
		
		// Pour le pseudo
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(new JLabel("Pseudo:"));
		pseudoField = new JTextField(15); pseudoField.setText(pseudo);
		pseudoField.setEditable(true);
		panel.add(pseudoField);
		optionsPanel.add(panel);
		
		// Pour l'adresse
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(new JLabel("IP address:"));
		addressField = new JTextField(15);
		addressField.setText(address);
		addressField.setEditable(true);
		panel.add(addressField);
		optionsPanel.add(panel);
		
		// Pour le port
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(new JLabel("Port:"));
		portField = new JTextField(10); portField.setEditable(true);
		portField.setText((new Integer(port)).toString());
		panel.add(portField);
		optionsPanel.add(panel);
		
		// Host/guest radio button
		ButtonGroup bg = new ButtonGroup();
		hostOption = new JRadioButton("Host", isHost);
		hostOption.setMnemonic(KeyEvent.VK_H);
		guestOption = new JRadioButton("Guest", !isHost);
		guestOption.setMnemonic(KeyEvent.VK_G);
		bg.add(hostOption);
		bg.add(guestOption);
		panel = new JPanel(new GridLayout(1, 2));
		panel.add(hostOption);
		panel.add(guestOption);
		optionsPanel.add(panel); 
		
		// Connect/disconnect boutons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		
		// Listener pour les boutons
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect")) {
					connect();
				} else if (e.getActionCommand().equals("disconnect")) {
					disconnect();
				} else if (e.getActionCommand().equals("send")) {
					sendMessage();
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
		optionsPanel.add(buttonPane);
		
		return optionsPanel;
	}
	
	/**
	 * Créer le JPanel de chat et ses composants
	 * 
	 * @return le JPanel de chat
	 */
	private JPanel initChatPanel() {
		
		chatPanel = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);      
		
		chatPanel.add(chatLine, BorderLayout.SOUTH);
		chatPanel.add(chatTextPane, BorderLayout.CENTER);
		chatPanel.setPreferredSize(new Dimension(200, 200));

		//listener pour envoyer les messages avec entrée
		chatLine.addKeyListener(new KeyboardListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		
		return chatPanel;
	}
	
	/**
	 * Créer le JLabel de status
	 * 
	 * @return le JLabel de status
	 */
	private JLabel initStatusBar() {
		statusBar = new JLabel();
		statusBar.setText("Offline");
		
		return statusBar;
	}
	
	/**
	 * Action à l'appuie du bouton "Connect"
	 */
	private void connect() {
		
		port = Integer.parseInt(portField.getText());
		
		// Si le RadioButton Host est sélectionné, on paramètre le port du serveur.
		if (hostOption.isSelected() == true) {
			server.setPort(port);
			server.startServer();
		}
		
		// Paramétrage du Client
		client.setPseudo(pseudoField.getText());
		pseudo = pseudoField.getText();
		client.setAddress(addressField.getText());
		client.setPort(port);
		
		client.connect();
	}
	
	/**
	 * Change l'interface pour afficher l'état connecté
	 */
	private void connected() {
		// Activation du chat / désactivation des options
		chatLine.setEnabled(true);
		sendButton.setEnabled(true);
		disconnectButton.setEnabled(true);
		connectButton.setEnabled(false);
		hostOption.setEnabled(false);
		guestOption.setEnabled(false);
		portField.setEnabled(false);
		addressField.setEnabled(false);
		pseudoField.setEnabled(false);

		statusBar.setText("Online");

		mainFrame.repaint();
	}
	
	/**
	 * Action à l'appuie du bouton "Disconnect"
	 */
	private void disconnect() {
	
		if (hostOption.isSelected() == true && server.isStarted()) {
			server.stopServer();						
		} else {
			client.disconnect();
		}
	}
	
	/**
	 * Change l'interface pour afficher l'état déconnecté
	 */
	private void disconnected() {
		
		// Désactivation du chat / activation des options
		connectButton.setEnabled(true);
		addressField.setEnabled(true);
		hostOption.setEnabled(true);
		guestOption.setEnabled(true);
		pseudoField.setEnabled(true);
		portField.setEnabled(true);
		sendButton.setEnabled(false);
		disconnectButton.setEnabled(false);
		
		chatLine.setText(""); 
		chatLine.setEnabled(false);

		statusBar.setText("Offline");

		mainFrame.repaint();
	}
	
	/**
	 * Action à l'appuie du bouton "Send"
	 */
	private void sendMessage() {
		
		String message = chatLine.getText();

		if (!message.isEmpty()){

			String chat = chatText.getText();
			String newChat = chat + "\n" + pseudo + " : " + message;			
			String newMessage = pseudo + " : " + message;
			chatText.setText(newChat);
			chatLine.setText("");			
			client.send(newMessage);
		}
		
		mainFrame.repaint();
	}
	
	/**
	 * Affiche le message reçu dans le chat
	 * 
	 * @param message le message reçu
	 */
	private void receiveMessage(String message) {
		
		String chat = chatText.getText();
		String newChat = chat + "\n" + message;

		chatText.setText(newChat);
		
		mainFrame.repaint();
	}
	
	/**
	 * Adapter pour un nouveau ActionListener facilement
	 */
	class ActionAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {}
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

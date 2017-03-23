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

public class SimpleGUI implements IGUI {
	// Connect status constants
	final static int DISCONNECTED = 0;
	final static int BEGIN_CONNECT = 1;
	final static int CONNECTED = 2;

	// Various GUI components and info
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

	// Connection info
	private String hostIP;
	private int connectionStatus;
	private int port;
	private boolean isHost;
	private String pseudo;

	private INetworkClient inetwork;


	// Constructeur
	public SimpleGUI(){

		inetwork = (INetworkClient) Framework.getExtension(INetworkClient.class);
		hostIP = "localhost";
		port = 1337;
		connectionStatus = DISCONNECTED;
		isHost = true;
		pseudo = "pseudo";

	}

	/**
	 * Generation du panneau de gauche (infos) 
	 * @return un JPanel
	 */
	private JPanel initOptionsPane() {

		JPanel pane = null;
		ActionAdapter buttonListener = null;

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(5, 1));

		// Pseudo input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Pseudo:"));
		pseudoField = new JTextField(15); pseudoField.setText(pseudo);
		pseudoField.setEditable(true);
		pane.add(pseudoField);
		optionsPane.add(pane);   

		// IP address input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(15); ipField.setText(hostIP);
		ipField.setEditable(true);
		pane.add(ipField);
		optionsPane.add(pane);
		
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10); portField.setEditable(true);
		portField.setText((new Integer(port)).toString());
		pane.add(portField);
		optionsPane.add(pane);
		
		// Host/guest option
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

		// Connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// Request a connection initiation
				if (e.getActionCommand().equals("connect")) {
					chatLine.setEnabled(true);
					sendButton.setEnabled(true);
					disconnectButton.setEnabled(true);
					connectButton.setEnabled(false);
					hostOption.setEnabled(false);
					guestOption.setEnabled(false);
					ipField.setEnabled(false);
					pseudoField.setEnabled(false);
					
					inetwork.setServer(ipField.getText());
					
					connectionStatus = BEGIN_CONNECT;

					statusBar.setText("Online");
					setPseudo(getPseudoField().getText());

					mainFrame.repaint();


				}
				// Disconnect
				if (e.getActionCommand().equals("disconnect")){

					connectButton.setEnabled(true);
					ipField.setEnabled(true);
					hostOption.setEnabled(true);
					guestOption.setEnabled(true);
					pseudoField.setEnabled(true);
					sendButton.setEnabled(false);
					disconnectButton.setEnabled(false);

					connectionStatus = DISCONNECTED;
					
					chatLine.setText(""); 
					chatLine.setEnabled(false);

					statusBar.setText("Offline");

					mainFrame.repaint();
				}

				if (e.getActionCommand().equals("send")){
					sendMessage();
					mainFrame.repaint();




				}



			}
		};
		
		//connection button
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		
		//disconnection button
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setMnemonic(KeyEvent.VK_D);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		
		//send button
		sendButton = new JButton("Send");
		sendButton.setMnemonic(KeyEvent.VK_ENTER);
		sendButton.setActionCommand("send");
		sendButton.addActionListener(buttonListener);
		sendButton.setEnabled(false);
		
		//ajout des buttons au panel
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		buttonPane.add(sendButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Initialisation du GUI complet
	 */
	public void initGUI() {
		// Set up the status bar
		statusBar = new JLabel();
		statusBar.setText("Offline");

		// Set up the options pane
		JPanel optionsPane = initOptionsPane();

		// Set up the chat pane
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

		chatLine.addKeyListener(keyListener);

		// Set up the main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);



		// Set up the main frame
		mainFrame = new JFrame("Amazing UDP Chat");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);


	}

	public void receiveMessage(String message) {

		/*String author = m.getAuthor();
		String text = m.getPlainText();
*/
		String chat = chatText.getText();
		//String message = author +  " : " + text;
		String newChat = chat + "\n" + message;

		chatText.setText(newChat);
		mainFrame.repaint();

	}


	public void sendMessage() {

		String message = chatLine.getText();


		if (!message.isEmpty()){

			//inetwork.send(message);
			String chat = chatText.getText();
			String newChat = chat + "\n" + pseudo + " : " + message;

			chatText.setText(newChat);

			chatLine.setText("");
			
			inetwork.send(message);
		}

	}

	@Override
	public void receiveRoomList() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectedToServResponse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectedFromServer() {
		// TODO Auto-generated method stub

	}



	public JFrame getMainFrame() {
		return mainFrame;
	}



	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}



	public JTextArea getChatText() {
		return chatText;
	}



	public void setChatText(JTextArea chatText) {
		this.chatText = chatText;
	}



	public JTextField getChatLine() {
		return chatLine;
	}



	public void setChatLine(JTextField chatLine) {
		this.chatLine = chatLine;
	}



	public JLabel getStatusBar() {
		return statusBar;
	}



	public void setStatusBar(JLabel statusBar) {
		this.statusBar = statusBar;
	}



	public JTextField getIpField() {
		return ipField;
	}


	public void setIpField(JTextField ipField) {
		this.ipField = ipField;
	}


	public JTextField getPseudoField() {
		return pseudoField;
	}



	public void setPseudoField(JTextField pseudoField) {
		this.pseudoField = pseudoField;
	}



	public JRadioButton getHostOption() {
		return hostOption;
	}



	public void setHostOption(JRadioButton hostOption) {
		this.hostOption = hostOption;
	}



	public JRadioButton getGuestOption() {
		return guestOption;
	}



	public void setGuestOption(JRadioButton guestOption) {
		this.guestOption = guestOption;
	}



	public JButton getConnectButton() {
		return connectButton;
	}



	public void setConnectButton(JButton connectButton) {
		this.connectButton = connectButton;
	}



	public JButton getDisconnectButton() {
		return disconnectButton;
	}



	public void setDisconnectButton(JButton disconnectButton) {
		this.disconnectButton = disconnectButton;
	}



	public JButton getSendButton() {
		return sendButton;
	}



	public void setSendButton(JButton sendButton) {
		this.sendButton = sendButton;
	}



	public String getHostIP() {
		return hostIP;
	}



	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
		/*try {
			getInetwork().setServer(InetAddress.getByName(hostIP));
		} catch (UnknownHostException e) {
			getStatusBar().setText("Unknown host");
			e.printStackTrace();
		}*/
	}




	public int getConnectionStatus() {
		return connectionStatus;
	}



	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}



	public boolean isHost() {
		return isHost;
	}



	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}



	public String getPseudo() {
		return pseudo;
	}



	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
		//getInetwork().setClientName(pseudo);
	}



	public INetworkClient getInetwork() {
		return inetwork;
	}



	public void setInetwork(INetworkClient inetwork) {
		this.inetwork = inetwork;
	}

	public void run() {
		this.initGUI();		
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



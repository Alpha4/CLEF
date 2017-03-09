package extensions.ihm;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import extensions.network.Message;
import framework.Framework;
import framework.plugin.IMessage;
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
   private int port ;
   private int connectionStatus;
   private boolean isHost;
   private String pseudo;

   private INetworkClient inetwork;
   
   
   // Constructeur
   public SimpleGUI(){
	   
	   //inetwork = (INetworkClient) Framework.get(INetworkClient.class);
	   hostIP = "localhost";
	   port = 1234;
	   connectionStatus = DISCONNECTED;
	   isHost = true;
	   pseudo = "pseudo";
	   
	 }
   
  

   private JPanel initOptionsPane() {
	   	   
      JPanel pane = null;
      ActionAdapter buttonListener = null;

      // Create an options pane
      JPanel optionsPane = new JPanel(new GridLayout(5, 1));

      // IP address input
      pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      pane.add(new JLabel("Host IP:"));
      ipField = new JTextField(10); ipField.setText(hostIP);
      ipField.setEditable(true);
      pane.add(ipField);
      optionsPane.add(pane);
      
      // Pseudo input
      pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      pane.add(new JLabel("Pseudo:"));
      pseudoField = new JTextField(10); pseudoField.setText(pseudo);
      pseudoField.setEditable(true);
      pane.add(pseudoField);
      optionsPane.add(pane);      

      // Port input
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
                  connectButton.setEnabled(false);
                  disconnectButton.setEnabled(true);
                  connectionStatus = BEGIN_CONNECT;
                  ipField.setEnabled(false);
                  portField.setEnabled(false);
                  hostOption.setEnabled(false);
                  guestOption.setEnabled(false);
                  chatLine.setEnabled(true);
                  statusBar.setText("Online");
                  setPseudo(getPseudoField().getText());
                  pseudoField.setEnabled(false);
                  sendButton.setEnabled(true);
                  mainFrame.repaint();

                 
               }
               // Disconnect
               if (e.getActionCommand().equals("disconnect")){
                  connectButton.setEnabled(true);
                  disconnectButton.setEnabled(false);
                  connectionStatus = DISCONNECTED;
                  ipField.setEnabled(true);
                  portField.setEnabled(true);
                  hostOption.setEnabled(true);
                  guestOption.setEnabled(true);
                  chatLine.setText(""); chatLine.setEnabled(false);
                  statusBar.setText("Offline");
                  pseudoField.setEnabled(true);
                  sendButton.setEnabled(false);
                  mainFrame.repaint();
               }
               
               if (e.getActionCommand().equals("send")){
            	   sendMessage();
            	   mainFrame.repaint();
            	   
            	   
            	   
            	   
            	   
               }
                      
               
               
            }
         };
      connectButton = new JButton("Connect");
      connectButton.setMnemonic(KeyEvent.VK_C);
      connectButton.setActionCommand("connect");
      connectButton.addActionListener(buttonListener);
      connectButton.setEnabled(true);
      disconnectButton = new JButton("Disconnect");
      disconnectButton.setMnemonic(KeyEvent.VK_D);
      disconnectButton.setActionCommand("disconnect");
      disconnectButton.addActionListener(buttonListener);
      disconnectButton.setEnabled(false);
      sendButton = new JButton("Send");
      sendButton.setMnemonic(KeyEvent.VK_C);
      sendButton.setActionCommand("send");
      sendButton.addActionListener(buttonListener);
      sendButton.setEnabled(false);
      buttonPane.add(connectButton);
      buttonPane.add(disconnectButton);
      buttonPane.add(sendButton);
      optionsPane.add(buttonPane);

      return optionsPane;
   }

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

      // Set up the main pane
      JPanel mainPane = new JPanel(new BorderLayout());
      mainPane.add(statusBar, BorderLayout.SOUTH);
      mainPane.add(optionsPane, BorderLayout.WEST);
      mainPane.add(chatPane, BorderLayout.CENTER);
      
         
      
      // Set up the main frame
      mainFrame = new JFrame("Simple TCP Chat");
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setContentPane(mainPane);
      mainFrame.setSize(mainFrame.getPreferredSize());
      mainFrame.setLocation(200, 200);
      mainFrame.pack();
      mainFrame.setVisible(true);
      
      
   }

   public static void main(String args[]) {
      SimpleGUI simple = new SimpleGUI();
      simple.initGUI();
    }


public void receiveMessage(IMessage m) {
		
	String author = m.getAuthor();
	String text = m.getPlainText();
	
	String chat = chatText.getText();
	String message = author +  " : " + text;
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



public JTextField getPortField() {
	return portField;
}



public void setPortField(JTextField portField) {
	this.portField = portField;
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
}



public int getPort() {
	return port;
}



public void setPort(int port) {
	this.port = port;
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
}



public INetworkClient getInetwork() {
	return inetwork;
}



public void setInetwork(INetworkClient inetwork) {
	this.inetwork = inetwork;
}




}



// Action adapter for easy event-listener coding
class ActionAdapter implements ActionListener {
   public void actionPerformed(ActionEvent e) {}
}



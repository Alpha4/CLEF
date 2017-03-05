package extensions.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import extensions.mockserv.Serveur;
 
public class Fenetre extends JFrame implements ActionListener{
	
  private JButton bouton = new JButton("Envoyer");
  protected JTextField textToSend = new JTextField("");
  private JTextArea textReceived = new JTextArea("");
  // private Serveur serv = Framework.get("Serveur");
  private Serveur serv = new Serveur();
  
  public Fenetre(){             
    this.setTitle("Wahou.");
    this.setSize(400, 200);
    this.setLocationRelativeTo(null);               
    
    
    Box b1 = Box.createVerticalBox();
    
    Font police = new Font("Arial", Font.BOLD, 14);
    textToSend.setFont(police);
    textToSend.setForeground(Color.BLUE);
    
    //on vérifie ce qui se passe sur le bouton
    bouton.addActionListener(this);
    
    
    b1.add(textToSend);
    b1.add(bouton);
    b1.add(textReceived);
    
    this.getContentPane().add(b1);
    
    this.setVisible(true);
    
  }     
  
  public void actionPerformed(ActionEvent arg0) {      
	 
	  
	 // serv.invoke(blablablabla)
	 textReceived.setText(textReceived.getText() + "\n" + serv.receiveAndSend(textToSend.getText()));
	 
	 
	 textToSend.setText("");

	 } 
  
  
}
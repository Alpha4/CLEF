package extensions.monitor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import framework.Config;
import framework.Event;
import framework.Framework;
import framework.IExtension;
import interfaces.IMonitoring;

/**
 * Affiche les status des différentes extensions de l'application<br>
 * Peut load ou kill une extension
 */
public class Monitor implements IMonitoring {

	private JPanel mainPanel;
	private JFrame mainFrame;
	private DefaultTableModel model;
	private JTable table;
	private JScrollPane listScrollPane;
	private JButton loadPluginButton;
	private JButton killPluginButton;
	private JButton exitButton;
	
	private List<IExtension> extensionsList;
	
	// Public methods
	
	@Override
	public void start() {
		
		// Create the window
		createGUI();
		
		// Get the extensions
		extensionsList = new ArrayList<IExtension>();
		for(Map<Class<?>,IExtension> extensions : Framework.getExtensions().values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()) {
				extensionsList.add(extension.getValue());
			}
		}
		
		// Subscribe to changes
		Framework.subscribeEvent("extension", this);
		
		// Update the table
		updateTable();
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
		
		if(event.is("extension")) {
			updateTable();
		}
	}
	
	// Private methods
	
	/**
	 * Créer la JFrame et ses composants
	 */
	public void createGUI() {
		
		Monitor self = this;
		
		mainFrame = new JFrame("Moniteur d'extensions");
		mainFrame.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				// Kill itself
				Framework.killExtension(self);
			}
		});
		
		mainPanel = new JPanel(new BorderLayout());
		
		// Initialisation du Modèle pour la table et de la Table
        model = new DefaultTableModel();
        table = new JTable(model);
        
        listScrollPane = new JScrollPane(table);      

		// Create buttons Load and Kill
		loadPluginButton = new JButton("Load");
		loadPluginButton.setActionCommand("Load");
		loadPluginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				IExtension extension = extensionsList.get(index);
				if (!Framework.loadExtension(extension)) {
					System.out.println("This extension could not be loaded");
				}
			}
		});

		killPluginButton = new JButton("Kill");
		killPluginButton.setActionCommand("Kill");
		killPluginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				IExtension extension = extensionsList.get(index);
				if (Framework.isKillable(extension)) {
					if (!Framework.killExtension(extension)) {
						System.out.println("This extension could not be killed");
					}
				} else {
					System.out.println("This extension is not killable");
				}
			}
		});
		
		exitButton = new JButton("Exit app");
		exitButton.setActionCommand("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Framework.exit();
			}
		});

		//Create a panel that uses BoxLayout.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,
				BoxLayout.LINE_AXIS));
		buttonPanel.add(exitButton);
		buttonPanel.add(Box.createHorizontalStrut(40));
		buttonPanel.add(loadPluginButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(killPluginButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		mainPanel.add(listScrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        
        mainFrame.add(mainPanel);
		
		//Display the window.
		mainFrame.pack(); // pour arranger la taille de la fenetre automatiquement.
		mainFrame.setVisible(true);
	}
	
	/**
	 * Met à jour l'affichage des status des plugins
	 */
	private void updateTable() {
		
		Vector<String> colNames = new Vector<String>();
		colNames.add("Type");
		colNames.add("Name");
		colNames.add("Status");
		Vector<Object> data = new Vector<Object>();
		
		for (IExtension extension : extensionsList) {
			Vector<String> row = new Vector<String>();
			Config config = Framework.getExtensionConfig(extension);
			row.add(config.getType());
			row.add(config.getName());
			row.add(Framework.getExtensionStatus(extension));
			data.add(row);
		}

		model.setDataVector(data, colNames);
		model.fireTableDataChanged();
	}
	
	/**
	 * Adapter pour un nouveau ActionListener facilement
	 */
	class ActionAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {}
	}
	
	/**
	 * Permet une gestion simplifié de la liste des extensions
	 */
	class Extension {
		public Class<?> extensionInterface;
		public Class<?> extensionClass;
		public IExtension extension;
		public String status;
		
		public Extension(Class<?> extensionInterface, Class<?> extensionClass, IExtension extension, String status) {
			this.extensionInterface = extensionInterface;
			this.extensionClass = extensionClass;
			this.extension = extension;
			this.status = status;
		}
	}
}

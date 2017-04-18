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

import framework.Event;
import framework.Framework;
import framework.IExtension;
import interfaces.IMonitoring;

public class Monitor implements IMonitoring {

	private JPanel mainPanel;
	private JFrame mainFrame;
	private DefaultTableModel model;
	private JTable table;
	private JScrollPane listScrollPane;
	private JButton loadPluginButton;
	private JButton killPluginButton;
	
	private List<Extension> extensionsList;
	
	// Public methods
	
	public void start() {
		
		createGUI();
		Framework.subscribeEvent("extension", this);
		
	}
	
	public void handleEvent(Event event) {
		
		if(event.is("extension")) {
			updateTable();
		}
	}
	
	// Private methods
	
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
        
        updateTable();
        
        listScrollPane = new JScrollPane(table);      

		// Create buttons Load and Kill
		loadPluginButton = new JButton("Load");
		loadPluginButton.setActionCommand("Load");
		loadPluginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				IExtension extension = extensionsList.get(index).extension;
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
				IExtension extension = extensionsList.get(index).extension;
				if (Framework.isKillable(extension)) {
					if (!Framework.killExtension(extension)) {
						System.out.println("This extension could not be killed");
					}
				} else {
					System.out.println("This extension is not killable");
				}
			}
		});


		//Create a panel that uses BoxLayout.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,
				BoxLayout.LINE_AXIS));
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
	
	public void refreshStatuses() {
		extensionsList = new ArrayList<Extension>();
		
		for(Map<Class<?>,IExtension> extensions : Framework.extensions.values()) {
			for(Entry<Class<?>,IExtension> extension : extensions.entrySet()) {
				extensionsList.add(new Extension(extension.getKey(),extension.getValue(),Framework.getExtensionStatus(extension.getValue())));
			}
		}
	}
	
	public void updateTable() {
		
		refreshStatuses();
		
		Vector<String> colNames = new Vector<String>();
		colNames.add("Plugin");
		colNames.add("Status");
		Vector<Object> data = new Vector<Object>();
		
		for (Extension extension : extensionsList) {
			Vector<String> row = new Vector<String>();
			row.add(extension.extensionClass.getName());
			row.add(extension.status);
			data.add(row);
		}

		model.setDataVector(data, colNames);
		model.fireTableDataChanged();
	}
	
	// Action adapter for easy event-listener coding
	class ActionAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {}
	}
	
	class Extension {
		public Class<?> extensionClass;
		public IExtension extension;
		public String status;
		
		public Extension(Class<?> extensionClass, IExtension extension, String status) {
			this.extensionClass = extensionClass;
			this.extension = extension;
			this.status = status;
		}
	}
}

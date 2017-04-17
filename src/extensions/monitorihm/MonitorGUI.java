package extensions.monitorihm;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import framework.Framework;
import framework.Event;
import interfaces.IMonitorGUI;
import interfaces.IMonitoring;


public class MonitorGUI implements  IMonitorGUI {
	
	private JFrame frame;
	private JPanel mainPanel;

	private JTable table;
	private DefaultTableModel model;
	private JScrollPane listScrollPane;

	private JButton loadPluginButton;
	private JButton killPluginButton;

	private IMonitoring monitor;
	private Map<Class<?>,String> extensionsMap;
	private Map<Integer,Class<?>> tableMap;
	
	/**
     * Création du GUI et affichage.
     */
	private void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Moniteur d'extensions");
		frame.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				// Kill itself
				((IMonitoring)Framework.getExtension(IMonitoring.class)).killExtension(MonitorGUI.class);
			}
		});
		
		mainPanel = new JPanel(new BorderLayout());

		// Initialisation du Modèle pour la table et de la Table
        model = new DefaultTableModel();
        table = new JTable(model);
        
        // Récupération du Monitor grâce au Framework
        monitor = (IMonitoring) Framework.getExtension(IMonitoring.class);
        // Récupération de l'état des extensions
        extensionsMap = monitor.getExtensionsStatus();
        
        // On initialise la Table
		tableMap = createMapTable();    	
		initTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );


		listScrollPane = new JScrollPane(table);      

		// Create buttons Load and Kill
		loadPluginButton = new JButton("Load");
		loadPluginButton.setActionCommand("Load");
		loadPluginButton.addActionListener(new ButtonListener());

		killPluginButton = new JButton("Kill");
		killPluginButton.setActionCommand("Kill");
		killPluginButton.addActionListener(new ButtonListener());


		//Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,
				BoxLayout.LINE_AXIS));
		buttonPane.add(loadPluginButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(killPluginButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		mainPanel.add(listScrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPane, BorderLayout.PAGE_END);

		frame.add(mainPanel);
		
		//Display the window.
		frame.pack(); // pour arranger la taille de la fenetre automatiquement.
		frame.setVisible(true);
	}

    /**
     * Méthode qui crée la tableMap pour associer les index des lignes à une classe.
     * @return Map
     */
	private Map<Integer, Class<?>> createMapTable(){

		Map<Integer, Class<?>> map = new TreeMap<Integer, Class<?>>();
		int i = 0;
		for (Class<?> cl : extensionsMap.keySet()) {
			map.put(i, cl);
			i++;
		}        
		return map;
	}

    /**
     * Méthode initialisant la Table des extensions.
     */
	private void initTable() {

		String colNames[] = {"Plugin", "Status"};

		Object[][] newData = {{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}};

		for(int index : tableMap.keySet()) {    		
			Object[] kek = {tableMap.get(index).getName() , extensionsMap.get(tableMap.get(index)) };
			newData[index]= kek;	   		
		}	 
		model.setDataVector(newData, colNames);
		model.fireTableDataChanged();
	}

    /**
     * Méthode de mise à jour de la Table des extensions.
     */
	private void updateTable() {
		extensionsMap = monitor.getExtensionsStatus();
		tableMap = createMapTable();    	
		initTable();
		frame.repaint();
	}

	public void start() {
		createAndShowGUI(); 
		Framework.subscribeEvent("extension", this);
	}
	
	public void stop() {
		
		// Ferme la fenêtre
		frame.setVisible(false);
		frame.dispose();
		frame = null;
	}

	public void handleEvent(Event event) {
		if(event.is("extension.loaded") || event.is("extension.killed") || event.is("extension.error")){
			updateTable();
		}

	}
	
	//ButtonListener pour les différents boutons du moniteur
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {      	

			if (e.getActionCommand().equals("Load")) {

				int index = table.getSelectedRow();
				monitor.loadExtension(tableMap.get(index));
			}

			if (e.getActionCommand().equals("Kill")) {

				int index = table.getSelectedRow();
				monitor.killExtension(tableMap.get(index));
			}

		}
	}

}
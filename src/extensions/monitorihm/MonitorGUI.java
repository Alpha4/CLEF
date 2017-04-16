package extensions.monitorihm;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import framework.Framework;
import interfaces.IMonitorGUI;
import interfaces.IMonitoring;


public class MonitorGUI extends JPanel
implements TableModelListener, IMonitorGUI {
	private JList list; 
	
	private static JFrame frame;

	private JTable table;
	private DefaultTableModel model;
	private JScrollPane listScrollPane;

	private JButton loadPluginButton;
	private JButton killPluginButton;
	private JButton RefreshButton;

	private IMonitoring monitor;
	private Map<Class<?>,String> extensionsMap;
	private Map<Integer,Class<?>> tableMap;


	public MonitorGUI() {
		super(new BorderLayout());

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

		RefreshButton = new JButton("Refresh");
		RefreshButton.setActionCommand("Refresh");
		RefreshButton.addActionListener(new ButtonListener());


		//Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,
				BoxLayout.LINE_AXIS));
		buttonPane.add(loadPluginButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(killPluginButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(RefreshButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		add(listScrollPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);

	}

//ButtonListener pour les différents boutons du moniteur
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {      	

			if (e.getActionCommand().equals("Load")) {

				int index = table.getSelectedRow();
				monitor.loadExtension(tableMap.get(index));	
				updateTable();
			}

			if (e.getActionCommand().equals("Kill")) {

				int index = table.getSelectedRow();
				monitor.killExtension(tableMap.get(index));
				updateTable();
			}

			if (e.getActionCommand().equals("Refresh")) {
				updateTable();
			}

		}
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
	}

    /**
     * Création du GUI et affichage.
     */
	private static void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Montiteur d'extensions");


		//Create and set up the content pane.
		JComponent newContentPane = new MonitorGUI();

		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack(); // pour arranger la taille de la fenetre automatiquement.
		frame.setVisible(true);
	}

	public void start() {
		createAndShowGUI(); 
		Framework.subscribeEvent("extension.loaded", this);
	}


	@Override
	public void tableChanged(TableModelEvent e) {

	}

	@Override
	public void handleEvent(String name, Object event) {
		if(name.equals("extension.loaded")){
			System.out.println("An extension has been loaded, please refresh");
		}

	}

}
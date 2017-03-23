package extensions.monitorihm;
 
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import extensions.monitoring.Monitoring;
import framework.ExtensionContainer;
import framework.Framework;
import framework.plugin.IMonitorGUI;
import framework.plugin.IMonitoring;

 

public class MonitorGUI extends JPanel
                      implements TableModelListener, IMonitorGUI {
    private JList list; 

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
         
        model = new DefaultTableModel();
        table = new JTable(model);
        
        monitor = (IMonitoring) Framework.getExtension(IMonitoring.class);
        extensionsMap = monitor.getExtensionsStatus();
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
        	
        	if (e.getActionCommand().equals("Refresh")) {
        		updateTable();
        	}
        	
        }
    }
    
    
 // fonction qui créé la tableMap pour associer les index des lignes à une classe
    private Map<Integer, Class<?>> createMapTable(){
    	
        Map<Integer, Class<?>> map = new TreeMap<Integer, Class<?>>();
        int i = 0;
        for (Class<?> cl : extensionsMap.keySet()) {
        	map.put(i, cl);
        	i++;
        }        
        return map;
    }
    
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

    private void updateTable() {
    	extensionsMap = monitor.getExtensionsStatus();
        tableMap = createMapTable();    	
        initTable();    	
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Montiteur d'extensions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
 
        //Create and set up the content pane.
        JComponent newContentPane = new MonitorGUI();

        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack(); // pour arranger la taille de la fenetre automatiquement.
        frame.setVisible(true);
    }
    
    
    public void run() {
    	createAndShowGUI();    	
    }
 

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		
	}
}
package extensions.monitorihm;
 
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import extensions.monitoring.Monitoring;
import framework.ExtensionContainer;
import framework.Framework;
import framework.plugin.IMonitoring;

 

public class MonitorGUI extends JPanel
                      implements TableModelListener {
    private JList list; 

    private JButton fireButton;
    private JTextField employeeName;
    
    private JTable table;
    private DefaultTableModel model;
    
    private JButton loadPluginButton;
	private JButton killPluginButton;
	
	private Monitoring monitor;
	private Map<Class<?>,String> extensionsMap;
	private Map<Integer,Class<?>> tableMap;
	
 
    public MonitorGUI() {
        super(new BorderLayout());
         
        // --------POUR ESSAYER------------:
        
        
        String colNames[] = {"Plugin", "Status"};
        Object[][] data = {{"SimpleGUI","Loaded"},{"Network","NotLoaded"},{"Message","Killed"},{"Client","Error"}};
        
      
        //---------------------------------
        
        
        //TODO : créer la table à afficher en fonction dex extensions et des status
        // faire une fonction de mise à jour de la table à appeler après avoir appuyé sur les boutons;
        // |-> getDataFromMapAndUpdate

        tableMap = createMapTable();
        //  Object[][] data = getDataFromMapAndUpdate(extensionsMap, tableMap);

        model = new DefaultTableModel(data,colNames);
        

        
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
        


        JScrollPane listScrollPane = new JScrollPane(table);      
        
 
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
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);

    }
 
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {      	
        	
        	if (e.getActionCommand().equals("Load")) {
        		
        		int index = table.getSelectedRow();
        		//monitor.load(tableMap.get(index));
        		extensionsMap = monitor.getExtensionsStatus();
        		
        		
        	}
        	
        	if (e.getActionCommand().equals("Kill")) {
        		
        		int index = table.getSelectedRow();
        		//monitor.kill(tableMap.get(index));
        		extensionsMap = monitor.getExtensionsStatus();
        	}
        	
        }
    }
 
    private Map<Integer, Class<?>> createMapTable(){
    	
        extensionsMap = monitor.getExtensionsStatus();
        Map<Integer, Class<?>> map = new HashMap<Integer, Class<?>>();
        int i = 0;
        for (Class<?> cl : extensionsMap.keySet()) {
        	map.put(i, cl);
        	i++;
        }
        
        return map;
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
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		
	}
}
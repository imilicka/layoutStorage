/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.storage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Area;
import org.fit.layout.model.Page;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.BigdataInterface;
import org.fit.layout.storage.BigdataLaunchInfo;
import org.fit.layout.storage.BigdataPage;
import org.fit.layout.storage.example.Processor;
import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryException;
import org.xml.sax.SAXException;

/**
 * 
 * @author burgetr
 */
public class StoragePlugin implements BrowserPlugin
{
    private Browser browser;
    BigdataInterface bdi = null;	

    
    private JToolBar rdfDbToolBar;
    private JPanel panel;
    private JTextField urlRDFDBJTextField;
    private JLabel lblRdfDb;
    private JButton loadDBDataButton;
    private JToolBar toolBar;
    private JPanel panel_1;
    private JLabel lblNewLabel;
    private JComboBox<String> urlsComboBox;
    private JLabel lblNewLabel_1;
    private JComboBox<BigdataLaunchInfo> launchesComboBox;
    private JButton LoadModelButton;
    private JToolBar toolBar_1;
    private JPanel panel_2;
    private JButton btnSave;
    private JButton btn_removeModel;
    private JButton btn_testInsert;
    private JButton btn_clearDB;

    private JTabbedPane sidebarPane;
    
    
    @Override
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolBar(getRdfDbToolBar());
        this.browser.addToolBar(getToolBar_2());
        this.browser.addToolBar(getToolBar_1_1() );
        
        
        
        this.browser.addStructurePanel("pokus", getSidebarPane());
        
        
        
        return true;
    }
    
    //=================================================================
    private JToolBar getRdfDbToolBar() {
		if (rdfDbToolBar == null) {
			rdfDbToolBar = new JToolBar();
			rdfDbToolBar.setMaximumSize(new Dimension(18, 2));
			rdfDbToolBar.setSize(new Dimension(230, 0));
			rdfDbToolBar.setFloatable(false);
			rdfDbToolBar.add(getPanel());
		}
		return rdfDbToolBar;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panel.add(getLblRdfDb());
			panel.add(getUrlRDFDBJTextField());
			panel.add(getLoadDBDataButton());
		}
		return panel;
	}
	private JTextField getUrlRDFDBJTextField() {
		if (urlRDFDBJTextField == null) {
			urlRDFDBJTextField = new JTextField();
			urlRDFDBJTextField.setMinimumSize(new Dimension(12, 20));
			urlRDFDBJTextField.setHorizontalAlignment(SwingConstants.LEFT);
			urlRDFDBJTextField.setText("http://localhost:8080/bigdata/sparql");
			urlRDFDBJTextField.setColumns(30);
		}
		return urlRDFDBJTextField;
	}
	private JLabel getLblRdfDb() {
		if (lblRdfDb == null) {
			lblRdfDb = new JLabel("RDF DB");
		}
		return lblRdfDb;
	}
	private JButton getLoadDBDataButton() {
		if (loadDBDataButton == null) {
			loadDBDataButton = new JButton("Establish Connection");
			loadDBDataButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					loadDistinctUrls();
				}
			});
		}
		return loadDBDataButton;
	}
	
	
	private JTabbedPane getSidebarPane()
    {
        if (sidebarPane == null)
        {
            sidebarPane = new JTabbedPane();
            /*
            sidebarPane.addTab("Area tree", null, getJPanel(), null);
            sidebarPane.addTab("Logical tree", null, getJPanel4(), null);
            sidebarPane.addTab("Box tree", null, getBoxTreePanel(), null);
            sidebarPane.addTab("Separators", null, getJPanel2(), null);
            sidebarPane.addTab("Paths", null, getPathsPanel(), null);*/ 
        }
        return sidebarPane;
    }

	
	/**
	 * it loads distinct URLs into ulrsComboBox
	 */
	private void loadDistinctUrls() {
		String DBConnectionUrl = urlRDFDBJTextField.getText();
		
		urlsComboBox.removeAllItems();
		
		try {
			bdi = new BigdataInterface(DBConnectionUrl, false);
			
			List<String> listURL = bdi.getDistinctUrlPages();
			for(String url : listURL) {
				urlsComboBox.addItem(url);
			}
			
			getBtnSave().setEnabled(true);
			getBtn_removeModel().setEnabled(true);
			getBtn_testInsert().setEnabled(true);
			getBtn_clearDB().setEnabled(true);
		} 
		catch (RepositoryException e) {

			/*
			JOptionPane.showMessageDialog(this,
				    "There is a problem with DB connection: "+e.getMessage(),
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
			*/
			
			e.printStackTrace();
		}
	}
	
	
	private JToolBar getToolBar_2() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.add(getPanel_1());
		}
		return toolBar;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.add(getLblNewLabel());
			panel_1.add(getUrlsComboBox());
			panel_1.add(getLblNewLabel_1());
			panel_1.add(getLaunchesComboBox());
			panel_1.add(getLoadModelButton());
		}
		return panel_1;
	}
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("URLs");
		}
		return lblNewLabel;
	}
	private JComboBox<String> getUrlsComboBox() {
		if (urlsComboBox == null) {
			urlsComboBox = new JComboBox<String>();
			urlsComboBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(launchesComboBox==null)
			    		return;
			    	
					launchesComboBox.removeAllItems();
			    	
			    	if( urlsComboBox.getItemCount()>0 ) {
			    		
			    		launchesComboBox.setEnabled(true);
			    		
			    		List<BigdataLaunchInfo> launchList = bdi.getLaunchesForUrl(urlsComboBox.getSelectedItem().toString() ); 
			    		
			    		
			        	//fill combobox with launches
			        	for(BigdataLaunchInfo launch : launchList) {
			        		launchesComboBox.addItem( launch );
			        	}
			    	}
			    	else {
			    		launchesComboBox.setEnabled(false);
			    	}
					
				}
			});
		}
		return urlsComboBox;
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("Launches");
		}
		return lblNewLabel_1;
	}
	private JComboBox<BigdataLaunchInfo> getLaunchesComboBox() {
		if (launchesComboBox == null) {
			launchesComboBox = new JComboBox<BigdataLaunchInfo>();
			launchesComboBox.setRenderer(new DefaultListCellRenderer() {

				private static final long serialVersionUID = 2525351383652612796L;

					@Override 
		            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		                if(value instanceof BigdataLaunchInfo){
		                    BigdataLaunchInfo launch = (BigdataLaunchInfo) value;
		                    setText(launch.getDate());
		                } 
		                return this;
		            } 
		    });
			launchesComboBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(launchesComboBox==null)
			    		return;
			    	
			    	if( launchesComboBox.getItemCount()>0 ) {
			    		LoadModelButton.setEnabled(true);
			    	}
			    	else {
			    		LoadModelButton.setEnabled(false);
			    	}
					
				}
			});
		}
		return launchesComboBox;
	}
	private JButton getLoadModelButton() {
		if (LoadModelButton == null) {
			LoadModelButton = new JButton("Load Model");
			LoadModelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					BigdataLaunchInfo launch = (BigdataLaunchInfo) launchesComboBox.getSelectedItem();
	
	
			        try {
			        	
			        	Model modelStatements = bdi.getLaunchModel(launch.getDate().toString());
						
						Page page = new BigdataPage(modelStatements, launch.getUrl() );
						browser.setPage(page);
						
					} catch (Exception e1) {
						
						/*
						JOptionPane.showMessageDialog(mainWindow,
							    "Cannot load defined launch!",
							    "Loading Error",
							    JOptionPane.ERROR_MESSAGE);
						*/
						e1.printStackTrace();
					}
					
				}
			});
			LoadModelButton.setEnabled(false);
		}
		return LoadModelButton;
	}
	private JToolBar getToolBar_1_1() {
		if (toolBar_1 == null) {
			toolBar_1 = new JToolBar();
			toolBar_1.add(getPanel_2());
		}
		return toolBar_1;
	}
	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			panel_2.add(getBtnSave());
			panel_2.add(getBtn_removeModel());
			panel_2.add(getBtn_testInsert());
			panel_2.add(getBtn_clearDB());
		}
		return panel_2;
	}
	private JButton getBtnSave() {
		if (btnSave == null) {
			btnSave = new JButton("Save page To RDF DB");
			btnSave.setEnabled(false);
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					Page page = browser.getPage();
					
					if(page!=null) {
						bdi.insertPage(page);
						
						//bdi.insertAreaTree( proc.getAreaTree() , page.getSourceURL().toString() );
						
						loadDistinctUrls();
					}
					else {
						/*
						JOptionPane.showMessageDialog(mainWindow,
							    "There is not loaded web page! You have to load some before saving it into RDF DB!",
							    "Info",
							    JOptionPane.ERROR_MESSAGE);
							    */
					}
				}
			});
		}
		return btnSave;
	}
	private JButton getBtn_removeModel() {
		if (btn_removeModel == null) {
			btn_removeModel = new JButton("Remove Model");
			btn_removeModel.setEnabled(false);
			btn_removeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String launchDate = "-";

					try {
						BigdataLaunchInfo launch = (BigdataLaunchInfo) launchesComboBox
								.getSelectedItem();
						launchDate = launch.getDate().toString();

						bdi.removeLaunch(launchDate);
						
						loadDistinctUrls();
					} catch (Exception e) {
						e.printStackTrace();

						/*
						JOptionPane.showMessageDialog(mainWindow,
								"Cannot remove launch model for " + launchDate,
								"ERROR", JOptionPane.ERROR_MESSAGE);
						*/
					}
						
				}
			});
			
		}
		return btn_removeModel;
	}
	
	
	
	private JButton getBtn_testInsert() {
		if (btn_testInsert == null) {
			btn_testInsert = new JButton("Download links");
			btn_testInsert.setEnabled(false);
			btn_testInsert.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					
					
					
					
					//groupInsert();
					
				}
			});
		}
		return btn_testInsert;
	}
	
	
	private void groupInsert() {
	/*	
		String[] pages = new String[] { "http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk",
				"http://www.fit.vutbr.cz", "http://www.idnes.cz", "http://www.google.com", "http://www.aktualne.cz","http://www.centrum.cz","http://www.atlas.cz","http://www.seznam.cz","http://www.bbc.co.uk"
				};
		
		int celkem = pages.length;
		int i = 0;
		
		for(String urlstring: pages) {
						//displayURL(urlstring);
			browser.setPage(page);
			bdi.insertPage(page);

			System.out.println(i++ +" z "+celkem);
		}
		
		bdi.insertPage(page);
		*/
	}
	
	private JButton getBtn_clearDB() {
		if (btn_clearDB == null) {
			btn_clearDB = new JButton("Clear DB");
			btn_clearDB.setEnabled(false);
			btn_clearDB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bdi.clearRDFDatabase();
					loadDistinctUrls();
				}
			});
		}
		return btn_clearDB;
	}
    
    //=================================================================
    
    
}

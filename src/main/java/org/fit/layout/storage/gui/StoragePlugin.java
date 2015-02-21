/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.storage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.AreaTreeProvider;
import org.fit.layout.api.BoxTreeProvider;
import org.fit.layout.classify.FeatureAnalyzer;
import org.fit.layout.classify.VisualClassifier;
import org.fit.layout.classify.op.TagEntitiesOperator;
import org.fit.layout.classify.op.VisualClassificationOperator;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.BigdataInterface;
import org.fit.layout.storage.BigdataPageInfo;
import org.fit.layout.storage.model.BigdataAreaTree;
import org.fit.layout.storage.model.BigdataPage;
import org.fit.segm.grouping.SegmentationAreaTree;
import org.fit.segm.grouping.op.FindLineOperator;
import org.fit.segm.grouping.op.HomogeneousLeafOperator;
import org.openrdf.model.Model;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;



/**
 * 
 * @author imilicka
 */
public class StoragePlugin implements BrowserPlugin
{
    private Browser browser;
    BigdataInterface bdi = null;	
    
    private JPanel pnl_main;
    private JPanel tbr_connection;
    private JTextField tfl_urlRDFDB;
    private JLabel lbl_rdfDb;
    private JButton btn_loadDBData;
    private JPanel tbr_storageSelection;
    private JLabel lbl_urls;
    private JComboBox<String> cbx_urls;
    private JLabel lbl_launches;
    private JComboBox<BigdataPageInfo> cbx_launches;
    private JButton btn_loadBoxModel;
    private JPanel tbr_control;
    private JButton btn_saveBoxTreeModel;
    private JButton btn_removePage;
    private JButton btn_clearDB;
    private Boolean oneUrlOccurence = true;
    private JButton btn_saveAreaTreeModel;
    private JComboBox<String> cbx_areaTrees;
    private JButton btnNewButton;
    
    
	//=============================
    
    /**
     * @wbp.parser.entryPoint
     */
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolPanel("Model Storage", getPnl_main()  );
        return true;
    }
    
    
    private JPanel getPnl_main() {
    	
    	 if (pnl_main == null) {
    		 
             pnl_main = new JPanel();
             GridBagLayout gbl_main = new GridBagLayout();
             gbl_main.columnWeights = new double[] { 0.0 };
             gbl_main.rowWeights = new double[] { 0.0, 0.0, 0.0 };
             pnl_main.setLayout(gbl_main);
             GridBagConstraints gbc_connection = new GridBagConstraints();
             gbc_connection.weightx = 1.0;
             gbc_connection.anchor = GridBagConstraints.EAST;
             gbc_connection.fill = GridBagConstraints.BOTH;
             gbc_connection.insets = new Insets(0, 0, 5, 0);
             gbc_connection.gridx = 0;
             gbc_connection.gridy = 0;
             pnl_main.add(getPnl_connection(), gbc_connection);
             GridBagConstraints gbc_storageSelection = new GridBagConstraints();
             gbc_storageSelection.weightx = 1.0;
             gbc_storageSelection.fill = GridBagConstraints.BOTH;
             gbc_storageSelection.insets = new Insets(0, 0, 5, 0);
             gbc_storageSelection.gridx = 0;
             gbc_storageSelection.gridy = 1;
             pnl_main.add(getPnl_storageSelection(), gbc_storageSelection);
             GridBagConstraints gbc_control = new GridBagConstraints();
             gbc_control.weightx = 1.0;
             gbc_control.anchor = GridBagConstraints.EAST;
             gbc_control.fill = GridBagConstraints.BOTH;
             gbc_control.insets = new Insets(0, 0, 5, 0);
             gbc_control.gridx = 0;
             gbc_control.gridy = 2;
             pnl_main.add(getPnl_control(), gbc_control);
         }
         return pnl_main;
    }
    
    
    //Connection panel=================================================================
    
    private JPanel getPnl_connection() 
    {
		if (tbr_connection == null) {
			tbr_connection = new JPanel();
			tbr_connection.add(getLbl_RdfDb());
			tbr_connection.add(getTfl_urlRDFDB());
			tbr_connection.add(getBtn_loadDBData());
		}
		return tbr_connection;
	}
	
    private JTextField getTfl_urlRDFDB() 
    {
		if (tfl_urlRDFDB == null) {
			tfl_urlRDFDB = new JTextField();
			//tfl_urlRDFDB.setMinimumSize(new Dimension(12, 20));
			tfl_urlRDFDB.setHorizontalAlignment(SwingConstants.LEFT);
			tfl_urlRDFDB.setText("http://localhost:8080/bigdata/sparql");
			//tfl_urlRDFDB.setColumns(30);
		}
		return tfl_urlRDFDB;
	}
	
	private JLabel getLbl_RdfDb() 
	{
		if (lbl_rdfDb == null) {
			lbl_rdfDb = new JLabel("RDF DB");
		}
		return lbl_rdfDb;
	}
	
	private JButton getBtn_loadDBData() 
	{
		if (btn_loadDBData == null) {
			btn_loadDBData = new JButton("Establish Connection");
			btn_loadDBData.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					loadDistinctUrls();
				}
			});
		}
		return btn_loadDBData;
	}
	
	
	//Selection panel =============================
	
	private JPanel getPnl_storageSelection() 
	{
		if (tbr_storageSelection == null) {
			tbr_storageSelection = new JPanel();
			tbr_storageSelection.add(getLbl_urls());
			tbr_storageSelection.add(getCbx_urls());
			tbr_storageSelection.add(getLbl_launches());
			tbr_storageSelection.add(getCbx_launches());
			tbr_storageSelection.add(getBtn_loadBoxModel());
			tbr_storageSelection.add(getCbx_areaTrees());
			tbr_storageSelection.add(getBtnNewButton());
			
		}
		return tbr_storageSelection;
	}
	
	private JLabel getLbl_urls() 
	{
		if (lbl_urls == null) {
			lbl_urls = new JLabel("URLs");
		}
		return lbl_urls;
	}
	
	private JComboBox<String> getCbx_urls() 
	{
		if (cbx_urls == null) {
			cbx_urls = new JComboBox<String>();
			cbx_urls.setMaximumRowCount(8);
			cbx_urls.setPreferredSize(new Dimension(300,25));
			cbx_urls.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					
					if(cbx_launches==null)
			    		return;
			    	
					cbx_launches.removeAllItems();
					cbx_areaTrees.removeAllItems();
			    	
			    	if( cbx_urls.getItemCount()>0 ) {
			    		
			    		cbx_launches.setEnabled(true);
			    		cbx_areaTrees.setEnabled(true);
			    		
			    		
			    		List<BigdataPageInfo> launchList = bdi.getPagesForUrl(cbx_urls.getSelectedItem().toString() ); 
			    		
			    		try {
							List<String> areaTrees = bdi.getPageAreaModels(cbx_urls.getSelectedItem().toString() );
							for(String area: areaTrees) {
								System.out.println("area" + area);
								cbx_areaTrees.addItem(area);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
			    		
			    		
			    		
			        	//fill combobox with launches
			        	for(BigdataPageInfo launch : launchList) {
			        		cbx_launches.addItem( launch );
			        	}
			    	}
			    	else {
			    		cbx_launches.setEnabled(false);
			    	}
					
				}
			});
		}
		return cbx_urls;
	}
	
	private JLabel getLbl_launches() 
	{
		if (lbl_launches == null) {
			lbl_launches = new JLabel("Launches");
			
			if(oneUrlOccurence) {
				lbl_launches.setVisible(false);
			}
		}
		return lbl_launches;
	}
	
	private JComboBox<BigdataPageInfo> getCbx_launches() 
	{
		if (cbx_launches == null) {
			cbx_launches = new JComboBox<BigdataPageInfo>();
			cbx_launches.setMaximumRowCount(0);
			cbx_launches.setPreferredSize(new Dimension(300,25));
			cbx_launches.setRenderer(new DefaultListCellRenderer() {

				private static final long serialVersionUID = 2525351383652612796L;

					@Override 
		            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		                if(value instanceof BigdataPageInfo){
		                    BigdataPageInfo launch = (BigdataPageInfo) value;
		                    setText(launch.getDate());
		                } 
		                return this;
		            } 
		    });
			cbx_launches.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					
					if(cbx_launches==null)
			    		return;
			    	
			    	if( cbx_launches.getItemCount()>0 ) {
			    		btn_loadBoxModel.setEnabled(true);
			    	}
			    	else {
			    		btn_loadBoxModel.setEnabled(false);
			    	}
					
				}
			});
		
			if(oneUrlOccurence) {
				cbx_launches.setVisible(false);
			}
		}
		return cbx_launches;
	}
	
	private JButton getBtn_loadBoxModel() 
	{
		if (btn_loadBoxModel == null) {
			btn_loadBoxModel = new JButton("Load Box Model");
			btn_loadBoxModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					BigdataPageInfo launch = (BigdataPageInfo) cbx_launches.getSelectedItem();
	
			        try {
			        	
			        	Model modelStatements = bdi.getPageBoxModelFromTimestamp(launch.getDate().toString());
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
			btn_loadBoxModel.setEnabled(false);
		}
		return btn_loadBoxModel;
	}
	
	/**
	 * it loads distinct URLs into ulrsComboBox
	 */
	private void loadDistinctUrls() 
	{
		String DBConnectionUrl = tfl_urlRDFDB.getText();
		
		cbx_urls.removeAllItems();
		
		try {
			bdi = new BigdataInterface(DBConnectionUrl, false);
			
			List<String> listURL = bdi.getDistinctUrlPages();
			for(String url : listURL) {
				cbx_urls.addItem(url);
			}
			
			getBtn_saveBoxTreeModel().setEnabled(true);
			getBtn_removePage().setEnabled(true);
			getBtn_clearDB().setEnabled(true);
			getBtn_saveAreaTreeModel().setEnabled(true);
		} 
		catch (RepositoryException e) {

			JOptionPane.showMessageDialog((Component) browser,
				    "There is a problem with DB connection: "+e.getMessage(),
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	
	//Control panel =============================
	
	private JPanel getPnl_control() 
	{
		if (tbr_control == null) {
			tbr_control = new JPanel();
			tbr_control.add(getBtn_saveBoxTreeModel());
			tbr_control.add(getBtn_saveAreaTreeModel());
			tbr_control.add(getBtn_removePage());
			tbr_control.add(getBtn_clearDB());
		}
		return tbr_control;
	}
	
	private JButton getBtn_saveBoxTreeModel() 
	{
		if (btn_saveBoxTreeModel == null) {
			btn_saveBoxTreeModel = new JButton("Save Box Tree to DB");
			btn_saveBoxTreeModel.setEnabled(false);
			btn_saveBoxTreeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					Page page = browser.getPage();
					
					if(page!=null) {
						bdi.insertPage(page);
						
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
		return btn_saveBoxTreeModel;
	}
	
	private JButton getBtn_removePage() 
	{
		if (btn_removePage == null) {
			btn_removePage = new JButton("Remove Model");
			btn_removePage.setEnabled(false);
			btn_removePage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
				{
					removeSelectedPageLaunch();
				}
			});
		}
		return btn_removePage;
	}
	
	private JButton getBtn_clearDB() 
	{
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
    

	private void removeSelectedPageLaunch() 
	{
		String launchDate = "-";

		try {
			BigdataPageInfo launch = (BigdataPageInfo) cbx_launches
					.getSelectedItem();
			launchDate = launch.getDate().toString();

			bdi.removePage(launchDate);
			
			loadDistinctUrls();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void segmentPage(Page page)
    {
        //area tree
		SegmentationAreaTree atree = new SegmentationAreaTree(page);
        atree.findBasicAreas();
        
        //apply the area tree operations
        Vector<AreaTreeOperator> operations = new Vector<AreaTreeOperator>();
        operations.add(new FindLineOperator(false, 1.5f));
        operations.add(new HomogeneousLeafOperator());
        ////operations.add(new FindColumnsOperator());
        //operations.add(new SuperAreaOperator(2)); //TODO misto pass limit by se hodilo nejake omezeni granularity na zaklade vlastnosti oblasti
        ////operations.add(new CollapseAreasOperator());
        //operations.add(new ReorderOperator());
        
        System.out.println("OPERATORS");
        for (AreaTreeOperator op : operations)
        {
            System.out.println(op.toString());
            op.apply(atree);
        }
        
        //tagging
        AreaTreeOperator tagop = new TagEntitiesOperator();
        tagop.apply(atree);
       
        /*//visual features
        features = new FeatureAnalyzer(atree.getRoot());
        //if (weights != null)
        //    features.setWeights(weights);
        
        //visual classification
        vcls = new VisualClassifier("train_mix.arff", 1);
        //vcls = new VisualClassifier("train_reuters2.arff", 1);
        vcls.classifyTree(atree.getRoot(), features);*/
        
        VisualClassificationOperator vcop = new VisualClassificationOperator("train_mix.arff", 1);
        vcop.apply(atree);
        FeatureAnalyzer features = vcop.getFeatures();
        VisualClassifier vcls = vcop.getVisualClassifier();
        
        Set<Tag> tags = atree.getUsedTags();
        showAllTags(tags);
        
        System.out.println("DONE");
        
    }
	
	private void showAllTags(Set<Tag> tags) {
		Iterator<Tag> it = tags.iterator();
        while(it.hasNext()) {
        	Tag t = it.next();
        	System.out.println( t.getValue() );
        	
        }
	}
	
	private JButton getBtn_saveAreaTreeModel() {
		if (btn_saveAreaTreeModel == null) {
			btn_saveAreaTreeModel = new JButton("Save Area Tree to DB");
			btn_saveAreaTreeModel.setEnabled(false);
			btn_saveAreaTreeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Page page = browser.getPage();
					AreaTree atree = browser.getAreaTree();
					
					if(atree!=null && page!=null) {
						bdi.insertAreaTree( atree , page.getSourceURL().toString() );
					}
					
					loadDistinctUrls();
				}
			});
			
			
		}
		return btn_saveAreaTreeModel;
	}
	
	private JComboBox getCbx_areaTrees() {
		if (cbx_areaTrees == null) {
			cbx_areaTrees = new JComboBox();
			cbx_areaTrees.setPreferredSize(new Dimension(300,25));
			
		}
		return cbx_areaTrees;
	}
	
	private JButton getBtnNewButton() {
		if (btnNewButton == null) {
			btnNewButton = new JButton("Load AreaTree");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					if(cbx_areaTrees.getItemCount()>0) {
						
						Model m;
						try {
							m = bdi.getPageAreaModel(new URIImpl(cbx_areaTrees.getSelectedItem().toString())  );
							BigdataAreaTree bdAreaTree = new BigdataAreaTree(m, browser.getPage().getSourceURL().toString());
							browser.setAreaTree(bdAreaTree);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			});
		}
		return btnNewButton;
	}
}

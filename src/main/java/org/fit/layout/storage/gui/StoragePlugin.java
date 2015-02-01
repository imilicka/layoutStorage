/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.storage.gui;

import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.classify.FeatureAnalyzer;
import org.fit.layout.classify.VisualClassifier;
import org.fit.layout.classify.op.TagEntitiesOperator;
import org.fit.layout.classify.op.VisualClassificationOperator;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Page;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.BigdataInterface;
import org.fit.layout.storage.BigdataLaunchInfo;
import org.fit.layout.storage.BigdataPage;
import org.fit.segm.grouping.SegmentationAreaTree;
import org.fit.segm.grouping.op.FindLineOperator;
import org.fit.segm.grouping.op.HomogeneousLeafOperator;
import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryException;


/**
 * 
 * @author imilicka
 */
public class StoragePlugin implements BrowserPlugin
{
    private Browser browser;
    BigdataInterface bdi = null;	
    
    private JToolBar tbr_connection;
    private JTextField tfl_urlRDFDB;
    private JLabel lbl_rdfDb;
    private JButton btn_loadDBData;
    private JToolBar tbr_storageSelection;
    private JLabel lbl_urls;
    private JComboBox<String> cbx_urls;
    private JLabel lbl_launches;
    private JComboBox<BigdataLaunchInfo> cbx_launches;
    private JButton btn_loadModel;
    private JToolBar tbr_control;
    private JButton btn_saveModel;
    private JButton btn_removeModel;
    private JButton btn_clearDB;

    
	//=============================
    
    @Override
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolBar(getTbr_connection());
        this.browser.addToolBar(getTbr_storageSelection());
        this.browser.addToolBar(getTbr_control() );
        return true;
    }
    
    
    //Connection panel=================================================================
    
    private JToolBar getTbr_connection() 
    {
		if (tbr_connection == null) {
			tbr_connection = new JToolBar();
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
			tfl_urlRDFDB.setMinimumSize(new Dimension(12, 20));
			tfl_urlRDFDB.setHorizontalAlignment(SwingConstants.LEFT);
			tfl_urlRDFDB.setText("http://localhost:8080/bigdata/sparql");
			tfl_urlRDFDB.setColumns(30);
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
	
	private JToolBar getTbr_storageSelection() 
	{
		if (tbr_storageSelection == null) {
			tbr_storageSelection = new JToolBar();
			tbr_storageSelection.add(getLbl_urls());
			tbr_storageSelection.add(getCbx_urls());
			tbr_storageSelection.add(getLbl_launches());
			tbr_storageSelection.add(getCbx_launches());
			tbr_storageSelection.add(getBtn_loadModel());
			
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
			cbx_urls.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(cbx_launches==null)
			    		return;
			    	
					cbx_launches.removeAllItems();
			    	
			    	if( cbx_urls.getItemCount()>0 ) {
			    		
			    		cbx_launches.setEnabled(true);
			    		
			    		List<BigdataLaunchInfo> launchList = bdi.getLaunchesForUrl(cbx_urls.getSelectedItem().toString() ); 
			    		
			    		
			        	//fill combobox with launches
			        	for(BigdataLaunchInfo launch : launchList) {
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
		}
		return lbl_launches;
	}
	
	private JComboBox<BigdataLaunchInfo> getCbx_launches() 
	{
		if (cbx_launches == null) {
			cbx_launches = new JComboBox<BigdataLaunchInfo>();
			cbx_launches.setRenderer(new DefaultListCellRenderer() {

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
			cbx_launches.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(cbx_launches==null)
			    		return;
			    	
			    	if( cbx_launches.getItemCount()>0 ) {
			    		btn_loadModel.setEnabled(true);
			    	}
			    	else {
			    		btn_loadModel.setEnabled(false);
			    	}
					
				}
			});
		}
		return cbx_launches;
	}
	
	private JButton getBtn_loadModel() 
	{
		if (btn_loadModel == null) {
			btn_loadModel = new JButton("Load Model");
			btn_loadModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					BigdataLaunchInfo launch = (BigdataLaunchInfo) cbx_launches.getSelectedItem();
	
	
			        try {
			        	
			        	Model modelStatements = bdi.getLaunchModel(launch.getDate().toString());
						
						Page page = new BigdataPage(modelStatements, launch.getUrl() );
						browser.setPage(page);
						
						segmentPage(page);
						
						
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
			btn_loadModel.setEnabled(false);
		}
		return btn_loadModel;
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
			
			getBtn_saveModel().setEnabled(true);
			getBtn_removeModel().setEnabled(true);
			getBtn_clearDB().setEnabled(true);
		} 
		catch (RepositoryException e) {

			JOptionPane.showMessageDialog((Component) browser,
				    "There is a problem with DB connection: "+e.getMessage(),
				    "Connection Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	//Control panel =============================
	
	//Control panel =============================
	
	private JToolBar getTbr_control() 
	{
		if (tbr_control == null) {
			tbr_control = new JToolBar();
			tbr_control.add(getBtn_saveModel());
			tbr_control.add(getBtn_removeModel());
			tbr_control.add(getBtn_clearDB());
		}
		return tbr_control;
	}
	
	private JButton getBtn_saveModel() 
	{
		if (btn_saveModel == null) {
			btn_saveModel = new JButton("Save page to DB");
			btn_saveModel.setEnabled(false);
			btn_saveModel.addActionListener(new ActionListener() {
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
		return btn_saveModel;
	}
	
	private JButton getBtn_removeModel() 
	{
		if (btn_removeModel == null) {
			btn_removeModel = new JButton("Remove Model");
			btn_removeModel.setEnabled(false);
			btn_removeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String launchDate = "-";

					try {
						BigdataLaunchInfo launch = (BigdataLaunchInfo) cbx_launches
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
    
	
	
	public void segmentPage(Page page)
    {
		
		System.out.println("toto je moje segmentation");
		
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
}

/**
 * MultiPageAnalyzerPlugin.java
 *
 * Created on 30. 1. 2015, by imilicka
 */
package org.fit.layout.storage.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.classify.FeatureAnalyzer;
import org.fit.layout.classify.VisualClassifier;
import org.fit.layout.classify.op.TagEntitiesOperator;
import org.fit.layout.classify.op.VisualClassificationOperator;
import org.fit.layout.cssbox.CSSBoxTreeBuilder;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Area;
import org.fit.layout.model.Page;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.BigdataInterface;
import org.fit.layout.storage.example.LinkCrawler;
import org.fit.segm.grouping.SegmentationAreaTree;
import org.fit.segm.grouping.op.FindLineOperator;
import org.xml.sax.SAXException;

/**
 * 
 * @author imilicka
 */
public class MultiPageAnalyzerPlugin implements BrowserPlugin {
	private Browser browser;
	BigdataInterface bdi = null;

	private JToolBar tbr_control;
	private JTextField tfl_multiPageSeed;
	private JSpinner tfl_pageLimit;
	private JButton btn_multiPageAnalyzation;
	private JPanel pnl_crawledLink;
	private JScrollPane pathListScroll;
	private JScrollPane extractionScroll;
	private JTable classificationTable;
	private JList<String> pathList;
	private DefaultListModel<String> urlListModel;
	private JPanel controlPanel;
	private JButton btn_doAnalysis;
	private DefaultTableModel tagModelTable;
	
	// =============================

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public boolean init(Browser browser) {
		this.browser = browser;
		this.browser.addToolBar(getTbr_control());
		
		this.browser.addStructurePanel("Multipage Analyzer", getPnl_crawledLinks());
		return true;
	}

	
	// Control panel =============================

	private JToolBar getTbr_control() {
		if (tbr_control == null) {
			tbr_control = new JToolBar();
			tbr_control.add(getTfl_multiPageSeed());
			tbr_control.add(getTfl_pageLimit());
			tbr_control.add(getBtn_multiPageAnalyzation());
		}
		return tbr_control;
	}

	private JTextField getTfl_multiPageSeed() {
		if (tfl_multiPageSeed == null) {
			tfl_multiPageSeed = new JTextField();
			tfl_multiPageSeed.setMinimumSize(new Dimension(12, 20));
			tfl_multiPageSeed.setHorizontalAlignment(SwingConstants.LEFT);
			tfl_multiPageSeed.setText("http://www.fit.vutbr.cz/");
			tfl_multiPageSeed.setColumns(30);
		}
		return tfl_multiPageSeed;
	}

	private JSpinner getTfl_pageLimit() {
		if (tfl_pageLimit == null) {
			tfl_pageLimit = new JSpinner();
			tfl_pageLimit.setValue(10);
			//tfl_pageLimit.setHorizontalAlignment(SwingConstants.LEFT);
			//tfl_pageLimit.setText("10");
			//tfl_pageLimit.setColumns(3);
		}
		return tfl_pageLimit;
	}
	
	private JButton getBtn_multiPageAnalyzation() {
		if (btn_multiPageAnalyzation == null) {
			btn_multiPageAnalyzation = new JButton("Run multipage crawling");
			btn_multiPageAnalyzation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					String urlstring = tfl_multiPageSeed.getText();

					if (!urlstring.startsWith("http:")
							&& !urlstring.startsWith("ftp:")
							&& !urlstring.startsWith("file:"))
						urlstring = "http://" + urlstring;

					int urlLimit = (int)getTfl_pageLimit().getValue();
					
					List<String> urls = getCrawledUrlsFor(urlstring, urlLimit);
					
					for (String key : urls) {
						urlListModel.addElement(key);
					}

				}
			});
		}
		return btn_multiPageAnalyzation;
	}

	/**
	 * Goes over all urls and does segmentation and classification
	 * 
	 * @param urls
	 * @return analyzed pages
	 */
	private HashMap<String, AnalyzedPage> getAnalyzedAllUrls(List<String> urls) {
		HashMap<String, AnalyzedPage> analyzedPages = new HashMap<String, AnalyzedPage>();

		for (String url : urls) {

			try {
				AnalyzedPage ap = getAnalyzedUrl(url);
				analyzedPages.put(url, ap);
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		return analyzedPages;
	}

	/**
	 * Analysis of the particular url
	 * 
	 * @param url
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private AnalyzedPage getAnalyzedUrl(String urlstring)
			throws MalformedURLException, IOException, SAXException {
		
		AnalyzedPage pageAnalysis = new AnalyzedPage();

		Graphics2D g = browser.getOutputDisplay().getGraphics();
		Rectangle r = g.getDeviceConfiguration().getBounds();

		// it does page rendering
		CSSBoxTreeBuilder build = new CSSBoxTreeBuilder(r.getSize());
		build.parse(urlstring);
		pageAnalysis.setPage(build.getPage());

		// it does page segmentation and classification
		SegmentationAreaTree atree = getPageSegmentationTree(build.getPage());
		pageAnalysis.setAtree(atree);

		return pageAnalysis;
	}

	/**
	 * Gets crawled urls regarding to the limit
	 * @param urlstring
	 * @param urlLimit
	 * @return
	 */
	private List<String> getCrawledUrlsFor(String urlstring, int urlLimit) {
		LinkCrawler lc = new LinkCrawler();
		lc.setSeed(urlstring);
		lc.setLinksLimit(urlLimit);
		lc.start();

		return lc.getUrls();
	}

	/**
	 * gets segemented and classificated page
	 * @param page
	 * @return
	 */
	public SegmentationAreaTree getPageSegmentationTree(Page page) {
		// area tree
		SegmentationAreaTree atree = new SegmentationAreaTree(page);
		atree.findBasicAreas();

		// apply the area tree operations
		Vector<AreaTreeOperator> operations = new Vector<AreaTreeOperator>();
		operations.add(new FindLineOperator(false, 1.5f));
		// operations.add(new HomogeneousLeafOperator());
		// //operations.add(new FindColumnsOperator());
		// operations.add(new SuperAreaOperator(2)); //TODO misto pass limit by
		// se hodilo nejake omezeni granularity na zaklade vlastnosti oblasti
		// //operations.add(new CollapseAreasOperator());
		// operations.add(new ReorderOperator());

		System.out.println("OPERATORS");
		for (AreaTreeOperator op : operations) {
			System.out.println(op.toString());
			op.apply(atree);
		}

		// tagging
		AreaTreeOperator tagop = new TagEntitiesOperator();
		tagop.apply(atree);

		/*
		 * //visual features features = new FeatureAnalyzer(atree.getRoot());
		 * //if (weights != null) // features.setWeights(weights);
		 * 
		 * //visual classification vcls = new VisualClassifier("train_mix.arff",
		 * 1); //vcls = new VisualClassifier("train_reuters2.arff", 1);
		 * vcls.classifyTree(atree.getRoot(), features);
		 */

		VisualClassificationOperator vcop = new VisualClassificationOperator(
				"train_mix.arff", 1);
		vcop.apply(atree);
		FeatureAnalyzer features = vcop.getFeatures();
		VisualClassifier vcls = vcop.getVisualClassifier();

		System.out.println("DONE");
		
		return atree;
	}


	
	//structural panel ===============================
	
    private JPanel getPnl_crawledLinks()
    {
        if (pnl_crawledLink == null)
        {
            pnl_crawledLink = new JPanel();
            GridBagLayout gbl_pathsPanel = new GridBagLayout();
            gbl_pathsPanel.columnWidths = new int[] { 0, 0 };
            gbl_pathsPanel.rowHeights = new int[] { 0, 0, 0 };
            gbl_pathsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
            gbl_pathsPanel.rowWeights = new double[] { 1.0, 0.1, 1.0,
                    Double.MIN_VALUE };
            pnl_crawledLink.setLayout(gbl_pathsPanel);
            
            GridBagConstraints gbc_pathListScroll = new GridBagConstraints();
            gbc_pathListScroll.insets = new Insets(0, 0, 5, 0);
            gbc_pathListScroll.fill = GridBagConstraints.BOTH;
            gbc_pathListScroll.gridx = 0;
            gbc_pathListScroll.gridy = 0;
            pnl_crawledLink.add(getPathListScroll(), gbc_pathListScroll);
            
            GridBagConstraints gbc_extractionScroll2 = new GridBagConstraints();
            gbc_extractionScroll2.fill = GridBagConstraints.HORIZONTAL;
            gbc_extractionScroll2.gridx = 0;
            gbc_extractionScroll2.gridy = 1;
            pnl_crawledLink.add(getPnl_control(), gbc_extractionScroll2);
            
            GridBagConstraints gbc_extractionScroll = new GridBagConstraints();
            gbc_extractionScroll.fill = GridBagConstraints.BOTH;
            gbc_extractionScroll.gridx = 0;
            gbc_extractionScroll.gridy = 2;
            pnl_crawledLink.add(getExtractionScroll(), gbc_extractionScroll);
            
        }
        return pnl_crawledLink;
    }

    private JScrollPane getPathListScroll()
    {
        
		if (pathListScroll == null)
        {
            pathListScroll = new JScrollPane();
            pathListScroll.setViewportView(getList_crawledLinks());
        }
        return pathListScroll;
    }

    private JScrollPane getExtractionScroll()
    {
        if (extractionScroll == null)
        {
            extractionScroll = new JScrollPane();
            extractionScroll.setViewportView(getTbl_classificationTags());
        }
        return extractionScroll;
    }

    private JPanel getPnl_control()
    {
        if (controlPanel == null)
        {
        	controlPanel = new JPanel();
        	controlPanel.add(getBtn_doAnalysis());
        }
        return controlPanel;
    }
    
	private JButton getBtn_doAnalysis() {
		if (btn_doAnalysis == null) {
			btn_doAnalysis = new JButton("Run Analysis");
			btn_doAnalysis.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					List<String> urls = pathList.getSelectedValuesList();
					
					HashMap<String,AnalyzedPage> analyzedUrls = getAnalyzedAllUrls(urls);
					Set<String> keys = analyzedUrls.keySet();

					
					for (String key : keys) {
						Area rootArea = analyzedUrls.get(key).getAtree().getRoot();
						showTagsForAreas(rootArea);
					}

					
				}
			});
		}
		return btn_doAnalysis;
	}

	
	/**
	 * Shows used tags and its text
	 * @param areas
	 */
	private void showTagsForAreas(Area parent) 
	{
		List<Area> areas = parent.getChildAreas();
		for(Area a : areas) {
			Map<Tag,Float> map = a.getTags();
			Set<Tag> keys = map.keySet();
			
			//appends into table
			for(Tag key : keys) {
				tagModelTable.addRow(new Object[] { key,a.getText() } );
			}
			
			showTagsForAreas(a);
		}
	}
	
    private JTable getTbl_classificationTags()
    {
        if (classificationTable == null)
        {
        	tagModelTable = new DefaultTableModel(); 
        	tagModelTable.addColumn("Tag"); 
        	tagModelTable.addColumn("Content");
        	
            classificationTable = new JTable(tagModelTable);
        }
        return classificationTable;
    }
    
    private JList<String> getList_crawledLinks()
    {
        if (pathList == null)
        {
        	urlListModel = new DefaultListModel<String>();
            pathList = new JList<String>(urlListModel);
        }
        return pathList;
    }

    
	//private class ========================================================
    
	/**
	 * Keeps information about particular page analysis
	 * 
	 * @author milicka
	 * 
	 */
	private class AnalyzedPage {

		private Page page;
		private SegmentationAreaTree atree;

		public Page getPage() {
			return page;
		}

		public void setPage(Page page) {
			this.page = page;
		}

		public SegmentationAreaTree getAtree() {
			return atree;
		}

		public void setAtree(SegmentationAreaTree atree) {
			this.atree = atree;
		}

	}
}

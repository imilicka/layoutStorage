package org.fit.layout.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Rectangular;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

public class BigdataAreaModelBuilder {

	Graph graph = null;
	ValueFactoryImpl vf;
	String url;
	String uniqueID;
	URIImpl pageNode;
	URIImpl areaTreeNode;

	public BigdataAreaModelBuilder(AreaTree areaTree, URIImpl pageNode, String url) {
		
		this.graph = new LinkedHashModel(); // it holds whole model
		this.vf = ValueFactoryImpl.getInstance(); // constructor for the value
		this.pageNode = pageNode;
		this.url = url;

		createAreaTreeModel(pageNode, areaTree);
	}

	public Graph getGraph() {
		return graph;
	}
	
	
	//============================================
	
	
	private void createAreaTreeModel(URIImpl pageNode, AreaTree areaTree) {
		
		this.uniqueID = getUniqueId();
		this.areaTreeNode = new URIImpl(this.url + "#" + this.uniqueID);

		// it adds root node
		this.graph.add(this.pageNode, new URIImpl(SEGM.hasAreaTree.toString()), this.areaTreeNode);

		
		addArea(areaTree.getRoot());
		insertAllAreas( areaTree.getRoot().getChildAreas() );
	}

	/**
	 * inserts children areas
	 * @param areas
	 */
	private void insertAllAreas(List<Area> areas) {
		
		if(areas==null)
			return;
		
		for(Area area : areas) {
			addArea(area);
			insertAllAreas(area.getChildAreas());
		}
	}

	/**
	 * adds area info model
	 * @param area
	 */
	private void addArea(Area area) {

		// unique identification from CSSBox
		int id = area.getId();

		
		URI individual = new URIImpl(this.url + "#" + this.uniqueID + "-" + id);
		graph.add(individual, RDF.TYPE, SEGM.Area);
		graph.add(individual, BOX.belongsTo, this.pageNode );
		

		// appends geometry
		Rectangular rec = area.getBounds();
		graph.add(individual, BOX.height, vf.createLiteral(rec.getHeight()));
		graph.add(individual, BOX.width, vf.createLiteral(rec.getWidth()));
		graph.add(individual, BOX.positionX, vf.createLiteral(rec.getX1()));
		graph.add(individual, BOX.positionY, vf.createLiteral(rec.getY1()));

		// appends tags
		if (area.getTags().size() > 0) {

			Map<Tag, Float> tags = area.getTags();

			Set<Tag> tagKeys = tags.keySet();
			for (Tag t : tagKeys) {
				Float support = tags.get(t);
				if (support > 0) {
					graph.add(individual, SEGM.hasTag, vf.createLiteral(t.getValue()));
				}
			}
		}
		
		
		// TODO
		// area.getBackgroundColor(); 
		// area.getFontSize(); 
		// area.getFontStyle();
		// area.getFontWeight(); 
		//area.getUnderline();
		
	}

	/**
	 * gets unique id for areaTree
	 * @return
	 */
	private String getUniqueId() {
		String dateTime = getDateTime();
		dateTime = dateTime.replace(":", "");
		dateTime = dateTime.replace("-", "");
		dateTime = dateTime.replace(" ", "");

		return "at-" + dateTime;
	}

	/**
	 * gets actual datetime
	 * @return
	 */
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return dateFormat.format(date);
	}
	
}

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
import org.fit.layout.storage.ontology.AreaOnt;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

public class BigdataModelBuilding {

	Graph graph = null;
	ValueFactoryImpl vf;
	String url;
	String uniqueID;
	URIImpl launchNode;
	URIImpl modelBuildingNode;

	public BigdataModelBuilding(AreaTree areaTree, URIImpl launchNode,
			String url) {
		inicialization();
		this.launchNode = launchNode;
		this.url = url;

		createStatementsFromTree(launchNode, areaTree);
	}

	private void inicialization() {
		this.graph = new LinkedHashModel(); // it holds whole model
		this.vf = ValueFactoryImpl.getInstance(); // constructor for the value
	}

	private void createStatementsFromTree(URIImpl launchNode, AreaTree areaTree) {
		this.uniqueID = getUniqueId();
		this.modelBuildingNode = new URIImpl(this.url + "#" + this.uniqueID);

		// it adds root node
		this.graph.add(this.launchNode,
				new URIImpl(AreaOnt.ModelBuilding.toString()),
				this.modelBuildingNode);

		fillGraph(launchNode, areaTree);
	}

	private void fillGraph(URIImpl modelBuilding, AreaTree atree) {
		
		insertArea(atree.getRoot());
		insertAllAreas( atree.getRoot().getChildAreas() );
	}

	private void insertAllAreas(List<Area> areas) {
		
		if(areas==null)
			return;
		
		for(Area area : areas) {
			insertArea(area);
			insertAllAreas(area.getChildAreas());
		}
	}

	
	private void insertArea(Area area) {

		// unique identification from CSSBox
		int id = area.getId();

		
		URI individual = new URIImpl(AreaOnt.Area + "#" + this.uniqueID + "-" + id);
		graph.add(individual, RDF.TYPE, vf.createURI(AreaOnt.Area));
		graph.add(individual, new URIImpl(AreaOnt.build), this.modelBuildingNode );
		

		// appends geometry
		Rectangular rec = area.getBounds();
		graph.add(individual, new URIImpl(BoxOnt.height),
				vf.createLiteral(rec.getHeight()));
		graph.add(individual, new URIImpl(BoxOnt.width),
				vf.createLiteral(rec.getWidth()));
		graph.add(individual, new URIImpl(BoxOnt.positionX),
				vf.createLiteral(rec.getX1()));
		graph.add(individual, new URIImpl(BoxOnt.positionY),
				vf.createLiteral(rec.getY1()));
		

		// appends tags
		if (area.getTags().size() > 0) {

			Map<Tag, Float> tags = area.getTags();

			Set<Tag> tagKeys = tags.keySet();
			for (Tag t : tagKeys) {
				Float support = tags.get(t);
				if (support > 0) {
					graph.add(individual, new URIImpl(BoxOnt.hasTag),
							vf.createLiteral(t.getValue()));
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

	private String getUniqueId() {
		String dateTime = getDateTime();
		dateTime = dateTime.replace(":", "");
		dateTime = dateTime.replace("-", "");
		dateTime = dateTime.replace(" ", "");

		return "mb-" + dateTime;
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return dateFormat.format(date);
	}

	public Graph getGraph() {
		return graph;
	}
}

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
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

public class BigdataAreaModelBuilder {

	Graph graph = null;
	ValueFactoryImpl vf;
	String url;
	String uniqueID;
	URI pageNode;
	URI areaTreeNode;

	public BigdataAreaModelBuilder(AreaTree areaTree, URI pageNode, String url) {
		
		graph = new LinkedHashModel();
		vf = ValueFactoryImpl.getInstance();
		this.pageNode = pageNode;
		this.url = url;

		createAreaTreeModel(pageNode, areaTree);
	}

	public Graph getGraph() {
		return graph;
	}
	
	
	//============================================
	
	
	private void createAreaTreeModel(URI pageNode, AreaTree areaTree) {
		
		uniqueID = getUniqueId();
		areaTreeNode = vf.createURI(this.url + "#" + this.uniqueID);
		graph.add(areaTreeNode, RDF.TYPE, SEGM.AreaTree);
		graph.add(areaTreeNode, SEGM.sourcePage, pageNode);
		
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

		final URI individual = getAreaUri(area);
		graph.add(individual, RDF.TYPE, SEGM.Area);
        graph.add(individual, SEGM.belongsTo, this.areaTreeNode);

        if (area.getParentArea() != null)
            graph.add(individual, SEGM.isChildOf, getAreaUri(area.getParentArea()));
        
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
				if (support != null && support > 0) {
				    addTag(area, individual, t, support);
				}
			}
		}
		
        if (area.getBackgroundColor() != null)
        {
            final String bgcol = String.format("#%02x%02x%02x", 
                    area.getBackgroundColor().getRed(),
                    area.getBackgroundColor().getGreen(),
                    area.getBackgroundColor().getBlue());
            graph.add(individual, BOX.backgroundColor, vf.createLiteral(bgcol));
        }

        // font attributes
        graph.add(individual, BOX.fontSize, vf.createLiteral(area.getFontSize()));
        graph.add(individual, BOX.fontWeight, vf.createLiteral(area.getFontWeight()));
        graph.add(individual, BOX.fontStyle, vf.createLiteral(area.getFontStyle()));
        graph.add(individual, BOX.underline, vf.createLiteral(area.getUnderline()));
        graph.add(individual, BOX.lineThrough, vf.createLiteral(area.getLineThrough()));
		
	}

	public void addTag(Area area, URI areaNode, Tag tag, float support) {
	    URI tagNode = getTagUri(area, tag);
	    graph.add(tagNode, RDF.TYPE, SEGM.Tag);
	    graph.add(tagNode, SEGM.hasType, vf.createLiteral(tag.getType()));
        graph.add(tagNode, SEGM.hasName, vf.createLiteral(tag.getValue()));
        graph.add(tagNode, SEGM.hasSupport, vf.createLiteral(support));
        graph.add(areaNode, SEGM.hasTag, tagNode);
	}
	
	public URI getAreaUri(Area area) {
	    return vf.createURI(url + "#" + uniqueID + "-" + area.getId());
	}
	
	public URI getTagUri(Area area, Tag tag) {
        return vf.createURI(url + "#" + uniqueID + "-" + area.getId()
                + "-" + tag.getType() + "." + tag.getValue());
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

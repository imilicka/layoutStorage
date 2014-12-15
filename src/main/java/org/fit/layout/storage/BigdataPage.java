package org.fit.layout.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.fit.layout.impl.DefaultBox;
import org.fit.layout.impl.DefaultPage;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;



public class BigdataPage extends DefaultPage {

	
	/**
	 * it initialize the page model from the given statements
	 * 
	 * @param pageStatements
	 * @throws MalformedURLException 
	 */
	public BigdataPage(Graph pageStatements, String urlString) throws MalformedURLException {
		
		super(new URL(urlString));
		
		List<DefaultBox> boxes = getAllPageBoxes(pageStatements);
		createDocumentTree(boxes);
		
	}
	
	
	/**
	 * conversion all RDF statements into a list of Boxes
	 * @param pageStatements
	 * @return list of Boxes that must be converted into a tree
	 */
	private List<DefaultBox> getAllPageBoxes(Graph pageStatements ) 
	{
		List<DefaultBox> allElements = new ArrayList<>();

		
		Iterator<Statement> elementStatements = pageStatements.match(null, RDF.TYPE, null);
			
		while( elementStatements.hasNext()) {
			
			Statement s = elementStatements.next();
			Iterator<Statement> attributes = pageStatements.match(s.getSubject(), null, null);
			allElements.add( new BigdataBox(attributes) ); 
			
		}
		
		return allElements;
	}
	
	
	//////////////////////////////////////////////////////////////
	// PAGE building
	//////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param model
	 * @return
	 */
	private void createDocumentTree(List<DefaultBox> elements) 
	{
		
	    //sorting the individuals
		List<DefaultBox> list = sortBySize(elements);
		
		
	    DefaultBox root = list.get(0);
	    this.setHeight(root.getHeight());
	    this.setWidth(root.getWidth());
	    
	    
		this.setRoot(root);
		list.remove(list.get(0)); 
		
		//process inner elements in the document tree
		appendTo(list.get(0), getInnerIndividuals(list.get(0), list));
	}
	
	/**
	 * it sorts page's elements from higher to lower volume
	 * @param elements
	 * @return list of individuals
	 */
	public static List<DefaultBox> sortBySize(List<DefaultBox> elements) {
		
	    //list sorting
	    Collections.sort(elements, new Comparator<DefaultBox>(){
            public int compare(DefaultBox one, DefaultBox two){
            	int width1 = one.getWidth();
		        int height1 = one.getHeight();
		        int width2 = two.getWidth();
		        int height2 = two.getHeight();
		        
				return (width2*height2)-(width1*height1); 
            }}); 
	    
	    return elements;
	}
	
	
	/**
	 * it appends individual into tree
	 * @param appendInd
	 * @param list
	 */
	private void appendTo(DefaultBox appendInd, List<DefaultBox> list) 
	{
		
		if(list==null || list.size()==0)
			return;
		
		//append actual node into tree
		DefaultBox actual = (DefaultBox)list.get(0);
		appendInd.add(actual);
		list.remove(actual);
		
		if(list.size()==0)
			return;
		
		List<DefaultBox> inner = getInnerIndividuals(actual, list);
		List<DefaultBox> outer = getOuterIndividuals( actual , list);
		
		
		if(inner.size()>0)
			appendTo(list.get(0), inner);
		
		if(outer.size()>0)
			appendTo(appendInd, outer);
	}
	
	
	/**
	 * it returns innner individuals from individualBuffer that are inside the individual
	 * @return
	 */
	private List<DefaultBox> getInnerIndividuals(DefaultBox individual, List<DefaultBox> list) {
		List<DefaultBox> listNested = new ArrayList<DefaultBox>();
		
		for(DefaultBox ind: list) {
			if(isNested(individual, ind)) {
				listNested.add(ind);
			}
		}
	
		return listNested;
	}
	
	
	/**
	 * it returns individuals from individualBuffer that are not inner
	 * @param individual
	 * @return
	 */
	private List<DefaultBox> getOuterIndividuals(DefaultBox individual, List<DefaultBox> list) {
		List<DefaultBox> listNested = new ArrayList<DefaultBox>();
		
		for(DefaultBox ind: list) {
			if(!isNested(individual, ind)) {
				listNested.add(ind);
			}
		}
		
		return listNested;
	} 
	
	/**
	 * detects if in2 is nested in in1 
	 * @param in1
	 * @param in2
	 * @return
	 */
	private Boolean isNested(DefaultBox in1, DefaultBox in2) {
		
		int width1 = in1.getWidth();
        int height1 = in1.getHeight();
        int posYAbs1 = in1.getY1();
    	int posXAbs1 = in1.getX1();
    	
    	int width2 = in2.getWidth();
        int height2 = in2.getHeight();
        int posYAbs2 = in2.getY1();
    	int posXAbs2 = in2.getX1();
    	
    	if( posXAbs1<=posXAbs2 && (posXAbs1+width1)>=(posXAbs2+width2) &&
    		posYAbs1<=posYAbs2 && (posYAbs1+height1)>=(posYAbs2+height2) ) 
    		return true;
    	else
    		return false;
	}
	
	
}

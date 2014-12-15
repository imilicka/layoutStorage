package org.fit.layout.storage;

import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.fit.cssbox.layout.BackgroundImage;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.layout.ReplacedBox;
import org.fit.cssbox.layout.ReplacedContent;
import org.fit.cssbox.layout.ReplacedImage;
import org.fit.cssbox.layout.TextBox;
import org.fit.layout.cssbox.ContentImageImpl;
import org.fit.layout.model.Box;
import org.fit.layout.model.ContentImage;
import org.fit.layout.model.Page;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;





/**
 * Class creates RDF graph model from the given Page model
 * @author milicka
 *
 */
public class BigdataGraph {
	
	private Graph graph;
	private String baseUrl;
	private ValueFactoryImpl vf;
	private String uniqueID;
	private URIImpl launchNode;
	private int number = 0;
	
	
	public BigdataGraph(Page page) {
		
		inicializeGraph(page.getSourceURL().toString() );
		
		insertAllBoxes(page.getRoot());
	}
	
	/*
	 * it initializes graph model
	 * 
	 * @param url defines page url for the identification
	 * @return launch node for the element linking
	 */
	private URI inicializeGraph(String url) {
		
		this.graph = new GraphImpl();				//it holds whole model
		this.baseUrl = url;							//web site url
		this.vf = ValueFactoryImpl.getInstance();	//constructor for the value creation
		this.uniqueID = getUniqueIdentifier();		//it represents unique id
		
		
		//inicialization with launch node
		this.launchNode = new URIImpl(baseUrl+"#"+ this.uniqueID) ;
		graph.add(this.launchNode, RDF.TYPE, new URIImpl(BoxOnt.Launch) );
		graph.add(this.launchNode, new URIImpl( BoxOnt.LaunchDatetime.toString() ), vf.createLiteral( this.uniqueID));
		graph.add(this.launchNode, new URIImpl( BoxOnt.sourceUrl.toString() ), vf.createLiteral( this.baseUrl));
		
		
		//inicialization of counter for the element storing
		number = 0;
		
		return this.launchNode;
	}
	

	/**
	 * It generates unique identifier for database storing
	 * 
	 * @return
	 */
	private String getUniqueIdentifier() 
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date date = new java.util.Date();
		return dateFormat.format(date);
	}
	
	
	
	/**
	 * it goes through page model and appends information in triples into graph model
	 * @param root
	 */
	private void insertAllBoxes(Box parent) {
		
		//in case of text block
		if((parent instanceof TextBox)) {
			return;
		}
		
		//in case of element with children
		
		for (int i = 0; i < parent.getChildCount(); i++) {
			Box sub1 = parent.getChildBox(i);
			this.insertBox(sub1);
		}
		
		//if there are some children
		for (int i = 0; i < parent.getChildCount(); i++) {
			Box sub1 = parent.getChildBox(i);

			if(!(parent instanceof TextBox)) {
				insertAllBoxes( sub1 );
			}
		}
	}

	
	/**
	 * it appends particular box into graph model
	 * @param box
	 */
	private void insertBox(Box box) 
	{
		//add BOX individual into graph
	    URI individual = new URIImpl(BoxOnt.Box+"#"+this.uniqueID+"-"+number);
	    graph.add(individual, RDF.TYPE, vf.createURI(BoxOnt.Box) );
	    
	    
	    //pin to launch node
	    graph.add( individual, new URIImpl(BoxOnt.belongsTo.toString()), this.launchNode);
	    
	    
	    //store position and size of element
	    Rectangular rec = box.getContentBounds();//  ((ElementBox)box).getAbsoluteBounds();
	    graph.add( individual, new URIImpl(BoxOnt.height.toString()) , vf.createLiteral(rec.getHeight()) );
	    graph.add( individual, new URIImpl(BoxOnt.width.toString()), vf.createLiteral(rec.getWidth()) );
	    graph.add( individual, new URIImpl(BoxOnt.positionX.toString()), vf.createLiteral(rec.getX1()) );
	    graph.add( individual, new URIImpl(BoxOnt.positionY.toString()), vf.createLiteral(rec.getY1()) );

	   
	   
	    //it prepares color string to hex definition
	    try {
				int intAlpha = Integer.valueOf(String.valueOf( box.getBackgroundColor().getAlpha() ));
				int intRed = Integer.valueOf(String.valueOf(box.getBackgroundColor().getRed()));
				int intGreen = Integer.valueOf(String.valueOf(box.getBackgroundColor().getGreen()));
				int intBlue = Integer.valueOf(String.valueOf(box.getBackgroundColor().getBlue()));
				
				graph.add(individual,
						  new URIImpl(BoxOnt.backgroundColor.toString()), 
						  vf.createLiteral("#"+
									  stringPad( Integer.toHexString( intRed ), "00")+ 
									  stringPad( Integer.toHexString( intGreen ), "00")+
									  stringPad( Integer.toHexString( intBlue ), "00")) );
		} catch(Exception ex) { }
		
	    
		//it appends the text content
	    try {
	    	if(box instanceof TextBox) {
	    		graph.add(individual, new URIImpl(BoxOnt.hasText.toString()), vf.createLiteral(box.getText()) );
	    	} else if(box instanceof ReplacedBox) {
	    				
	    	}
	    } catch(Exception ex) { }
	    
	    
	    //individual.addLiteral(BoxOnto.color, colorString(sub.getVisualContext().getColor()));
	    graph.add(individual, new URIImpl(BoxOnt.fontFamily), vf.createLiteral(box.getFontFamily() )  );
	    graph.add(individual, new URIImpl(BoxOnt.fontSize), vf.createLiteral(box.getFontSize()) );
	    graph.add(individual, new URIImpl(BoxOnt.fontWeight), vf.createLiteral(box.getFontWeight()  ));
	    graph.add(individual, new URIImpl(BoxOnt.fontStyle), vf.createLiteral( box.getFontStyle() ) );

	    
	    /*
	    //append image url into graph
	    if(box instanceof ContentImage ) {
    		ReplacedContent rc =((ReplacedBox)box).getContentObj();
    		
    		//there is element with image
    		if(rc instanceof ReplacedImage ) {
    			graph.add(individual, new URIImpl(BoxOnt.imageUrl.toString()), vf.createLiteral( ((ContentImage)rc) getUrl().toString() ));
    		}
    	}
	    */
    	
	    
	    //append background images into graph
	    try {
	    	List<BackgroundImage> bimages = ((ElementBox)box).getBackgroundImages();
    	    
    	    //all images
    	    if(bimages!=null) {
    		    for(BackgroundImage image : bimages) {
    		    	graph.add(individual, new URIImpl(BoxOnt.backgroundImageUrl), vf.createLiteral(image.getUrl().toString()) );
    		    	graph.add(individual, new URIImpl(BoxOnt.backgroundImagePosition), vf.createLiteral(image.getImgX() +","+image.getImgY()) );
    		    }
    	    }
    		    
	    } catch(Exception ex) { }
	    
	    
	    
	    //append background-color into graph
	    try {
			int intAlpha = Integer.valueOf(String.valueOf(box.getColor().getAlpha()  ));
			int intRed = Integer.valueOf(String.valueOf(box.getColor().getRed() ));
			int intGreen = Integer.valueOf(String.valueOf(box.getColor().getGreen() ));
			int intBlue = Integer.valueOf(String.valueOf(box.getColor().getBlue() ));
			
			graph.add(individual, new URIImpl(BoxOnt.color.toString()), 
								  vf.createLiteral("#"+
								  stringPad( Integer.toHexString( intRed ), "00")+ 
								  stringPad( Integer.toHexString( intGreen ), "00")+
								  stringPad( Integer.toHexString( intBlue ), "00")) );
		} catch(Exception ex) { }
	    
	    
	    number++;
		
	}
	
	
	/*
	 * Add the pad to the left of string then take as many characters from the right 
	 * that is the same length as the pad.
	 * This would normally mean starting my substring at 
	 * pad.length() + string.length() - pad.length() but obviously the pad.length()'s 
	 * cancel.
	 *
	 * 00000000sss
	 *    ^ ----- Cut before this character - pos = 8 + 3 - 8 = 3
	 */
	protected static String stringPad(String string, String pad) {	  
		  return (pad + string).substring(string.length());
	}
	

	public Graph getGraph() {
		return graph;
	}
}

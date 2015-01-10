package org.fit.layout.storage;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;



import org.fit.layout.model.Box;
import org.fit.layout.model.Box.Type;
import org.fit.layout.model.Page;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BoxOnt;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
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
	private String dateTime;
	private URIImpl launchNode;
	
	
	
	public BigdataGraph(Page page) {
		
		inicializeGraph(page.getSourceURL().toString() );
		
		Box bdb = page.getRoot();
		  this.insertBox(bdb);
		  
		  
		insertAllBoxes(bdb);
	}
	
	
	/*
	 * it initializes graph model
	 * 
	 * @param url defines page url for the identification
	 * @return launch node for the element linking
	 */
	private URI inicializeGraph(String url) {
		
		
		this.graph = new LinkedHashModel();				//it holds whole model
		this.baseUrl = url;							//web site url
		this.vf = ValueFactoryImpl.getInstance();	//constructor for the value creation
		this.dateTime = getDateTime();
		this.uniqueID = getLaunchIdFromDatetime(this.dateTime);		//it represents unique id
		
		
		//inicialization with launch node
		this.launchNode = new URIImpl(baseUrl+"#"+ this.uniqueID) ;
		graph.add(this.launchNode, RDF.TYPE, new URIImpl(BoxOnt.Launch) );
		graph.add(this.launchNode, new URIImpl( BoxOnt.LaunchDatetime.toString() ), vf.createLiteral( this.dateTime));
		graph.add(this.launchNode, new URIImpl( BoxOnt.sourceUrl.toString() ), vf.createLiteral( this.baseUrl));
		
		
		return this.launchNode;
	}
	

	/**
	 * It generates unique identifier for database storing
	 * 
	 * @return
	 */
	private String getDateTime() 
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return dateFormat.format(date);
	}
	
	
	/**
	 * it creates unique identification for the current launch
	 * @param dateTime
	 * @return
	 */
	private String getLaunchIdFromDatetime(String dateTime) {
		dateTime = dateTime.replace(":", "");
		dateTime = dateTime.replace("-", "");
		dateTime = dateTime.replace(" ", "");
		
		return dateTime;
	}
	
	
	/**
	 * it goes through page model and appends information in triples into graph model
	 * @param root
	 */
	private void insertAllBoxes(Box parent) {
		
		//in case of element with children
		for (int i = 0; i < parent.getChildCount(); i++) {
			Box sub1 = parent.getChildBox(i);
			
			this.insertBox(sub1);
		}
		
		//if there are some children
		for (int i = 0; i < parent.getChildCount(); i++) {
			Box sub1 = parent.getChildBox(i);
			insertAllBoxes( sub1 );
		}
	}

	
	/**
	 * it appends particular box into graph model
	 * @param box
	 */
	private void insertBox(Box box) {
		
		//unique identification from CSSBox
		int id = box.getId();
		
		
		//add BOX individual into graph
		URI individual = new URIImpl(BoxOnt.Box+"#"+this.uniqueID+"-"+id);
	    graph.add(individual, RDF.TYPE, vf.createURI(BoxOnt.Box) );
	    
	    
	    //unique identificator of box
	    graph.add(individual, new URIImpl(BoxOnt.id.toString()) , vf.createLiteral(id) );
	    
	    
	    //pin to launch node
	    graph.add( individual, new URIImpl(BoxOnt.belongsTo.toString()), this.launchNode);
	    
	    
	    //store position and size of element
	    //Rectangular rec = box.getContentBounds();
	    Rectangular rec = box.getBounds();
	    graph.add( individual, new URIImpl(BoxOnt.height.toString()) , vf.createLiteral(rec.getHeight()) );
	    graph.add( individual, new URIImpl(BoxOnt.width.toString()), vf.createLiteral(rec.getWidth()) );
	    graph.add( individual, new URIImpl(BoxOnt.positionX.toString()), vf.createLiteral(rec.getX1()) );
	    graph.add( individual, new URIImpl(BoxOnt.positionY.toString()), vf.createLiteral(rec.getY1()) );

	   
	   
	    //it prepares color string to hex definition
	    try {
				//int intAlpha = Integer.valueOf(String.valueOf( box.getBackgroundColor().getAlpha() ));
				int intRed = Integer.valueOf(String.valueOf(box.getBackgroundColor().getRed()));
				int intGreen = Integer.valueOf(String.valueOf(box.getBackgroundColor().getGreen()));
				int intBlue = Integer.valueOf(String.valueOf(box.getBackgroundColor().getBlue()));
				
				graph.add(individual,
						  new URIImpl(BoxOnt.backgroundColor.toString()), 
						  vf.createLiteral("#"+
									  stringPad( Integer.toHexString( intRed ), "00")+ 
									  stringPad( Integer.toHexString( intGreen ), "00")+
									  stringPad( Integer.toHexString( intBlue ), "00")) );
		} 
	    catch(Exception ex) { }
		
	    
	    //add text content into element
	    if(box.getType() == Type.TEXT_CONTENT) {
	    	graph.add(individual, new URIImpl(BoxOnt.hasText.toString()), vf.createLiteral(box.getText()) );
	    	
	    	//font attributes
	    	graph.add(individual, new URIImpl(BoxOnt.fontFamily), vf.createLiteral(box.getFontFamily() )  );
		    graph.add(individual, new URIImpl(BoxOnt.fontSize), vf.createLiteral(box.getFontSize()) );
		    graph.add(individual, new URIImpl(BoxOnt.fontWeight), vf.createLiteral(box.getFontWeight()  ));
		    graph.add(individual, new URIImpl(BoxOnt.fontStyle), vf.createLiteral( box.getFontStyle() ) );
	    }
	    
	    
	    
	    String tagName = box.getTagName();
	    if( tagName!=null && !tagName.isEmpty() ) {
	    	
	    	graph.add(individual, new URIImpl(BoxOnt.hasTag), vf.createLiteral( tagName ) );
	    }
	    
	    
	    /* 
	    //TODO - add images
	    //append background images into graph
	    try {
	    	List<BackgroundImage> bimages = ((ElementBox)box). getBackgroundImages();
    	    
    	    //all images
    	    if(bimages!=null) {
    		    for(BackgroundImage image : bimages) {
    		    	graph.add(individual, new URIImpl(BoxOnt.backgroundImageUrl), vf.createLiteral(image.getUrl().toString()) );
    		    	graph.add(individual, new URIImpl(BoxOnt.backgroundImagePosition), vf.createLiteral(image.getImgX() +","+image.getImgY()) );
    		    }
    	    }
    		    
	    } catch(Exception ex) { }
	    */
	    
	    
	    //append background-color into graph
	    try {
			//int intAlpha = Integer.valueOf(String.valueOf(box.getColor().getAlpha()  ));
			int intRed = Integer.valueOf(String.valueOf(box.getColor().getRed() ));
			int intGreen = Integer.valueOf(String.valueOf(box.getColor().getGreen() ));
			int intBlue = Integer.valueOf(String.valueOf(box.getColor().getBlue() ));
			
			graph.add(individual, new URIImpl(BoxOnt.color.toString()), 
								  vf.createLiteral( "#" +
								  stringPad( Integer.toHexString( intRed ), "00") + 
								  stringPad( Integer.toHexString( intGreen ), "00")+
								  stringPad( Integer.toHexString( intBlue ), "00")) );
		} catch(Exception ex) { }
	    
		
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
